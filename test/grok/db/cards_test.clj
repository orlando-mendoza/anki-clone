(ns grok.db.cards-test
  (:require  [clojure.test :refer [is deftest testing use-fixtures]]
             [datomic.api :as d]
             [clojure.spec.alpha :as s]
             [clojure.test.check.generators :as gen]
             [grok.db.cards :as SUT]
             [grok.db.decks :as decks]
             [grok.db.with-db :refer [with-db *conn*]]))

;; Invoke use fixtures so that new db is created for every test
(use-fixtures :each with-db)

(deftest cards
  (let [user-id (:user/id (d/entity (d/db *conn*) [:user/email "test@test.com"]))]
    (testing "browse - returns an empty vector if the user has not created any card for the deck"
      (let [new-deck (merge (gen/generate (s/gen ::decks/deck)))
            deck-id (decks/create! *conn* user-id new-deck)
            cards (SUT/browse (d/db *conn*) deck-id)]
        (is (empty? cards))
        (is (vector? cards))))

    (testing "browse - returns a list of cards for the deck"
      (let [new-deck (merge (gen/generate (s/gen ::decks/deck)))
            deck-id (decks/create! *conn* user-id new-deck)
            new-card {:card/id (d/squuid)
                      :card/deck [:deck/id deck-id]
                      :card/front "What is clojure"
                      :card/back "A programming language"}]
        @(d/transact *conn* [new-card])
        (let [cards (SUT/browse (d/db *conn*) deck-id)
              card (first cards)]
          (is (seq cards))
          (is (s/valid? ::SUT/card card))
          (is (vector? cards)))))
    
    (testing "fetch - returns a card by ID"
      (let [new-deck (merge (gen/generate (s/gen ::decks/deck)))
            deck-id (decks/create! *conn* user-id new-deck)
            card-id (d/squuid)
            new-card {:card/id card-id
                      :card/deck [:deck/id deck-id]
                      :card/front "What is Clojure"
                      :card/back "A programming language"}]
        @(d/transact *conn* [new-card])
        (let [card (SUT/fetch (d/db *conn*) deck-id card-id)]
          (is (s/valid? ::SUT/card card)))))
    
    (testing "create! - Creates a new card and returns the card ID"
      (let [new-deck (merge (gen/generate (s/gen ::decks/deck)))
            deck-id (decks/create! *conn* user-id new-deck)
            new-card {:card/deck [:deck/id deck-id]
                      :card/front "What is Clojure"
                      :card/back "A programming language"}
            card-id (SUT/create! *conn* deck-id new-card)]
        (is (uuid? card-id))))
    
    (testing "edit! - Edit an existing card and returns the updated card"
      (let [new-deck (merge (gen/generate (s/gen ::decks/deck)))
            deck-id (decks/create! *conn* user-id new-deck)
            new-card {:card/deck [:deck/id deck-id]
                      :card/front "What is Clojure"
                      :card/back "A programming language"}
            card-id (SUT/create! *conn* deck-id new-card)
            card-params {:card/back "A functional programming language"}
            edited-card (SUT/edit! *conn* deck-id card-id card-params)]
        (is (s/valid? ::SUT/card edited-card))))
    
    (testing "delete! - Delete an existing card - returns deleted card"
      (let [new-deck (merge (gen/generate (s/gen ::decks/deck)))
            deck-id (decks/create! *conn* user-id new-deck)
            new-card {:card/deck [:deck/id deck-id]
                      :card/front "What is Clojure"
                      :card/back "A programming language"}
            card-id (SUT/create! *conn* deck-id new-card)
            deleted-card (SUT/delete! *conn* deck-id card-id)]
        (is (s/valid? ::SUT/card deleted-card))
        (is (nil? (SUT/fetch (d/db *conn*) deck-id card-id)))))))


