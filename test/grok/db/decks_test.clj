(ns grok.db.decks-test
  (:require  [clojure.test :refer [is deftest testing use-fixtures]]
             [datomic.api :as d]
             [clojure.spec.alpha :as s]
             [clojure.test.check.generators :as gen]
             [grok.db.decks :as SUT]
             [grok.db.user :as user]
             [grok.db.with-db :refer [with-db *conn*]]))


(use-fixtures :each with-db)

(deftest decks
  (let [user-id (:user/id (d/entity (d/db *conn*) [:user/email "test@test.com"]))]

    (testing "browse - returns empty vector if the user has not created any deck"
      (let [decks (SUT/browse (d/db *conn*) user-id)]
        (is (= true (vector? decks)))
        (is (= true (empty? decks)))))

    (testing "browse - returns vector of cards if available"
      (let [new-deck (merge (gen/generate (s/gen ::SUT/deck))
                            {:deck/author [:user/id user-id]})]
        @(d/transact *conn* [new-deck])
        (let [decks (SUT/browse (d/db *conn*) user-id)]
          (is (= true (vector? decks)))
          (is (= false (empty? decks))))))

    (testing "fetch - returns a single deck by deck ID, belonging to a user"
      (let [deck-id  (d/squuid)
            new-deck (merge (gen/generate (s/gen ::SUT/deck))
                            {:deck/id deck-id
                             :deck/author [:user/id user-id]})]
        @(d/transact *conn* [new-deck])
        (let [decks (SUT/fetch (d/db *conn*) user-id deck-id)]
          (is (= true (map? decks)))
          (is (= false (empty? decks))))))

    (testing "fetch - returns nil if not found"
      (let [deck-id  (d/squuid)
            deck (SUT/fetch (d/db *conn*) user-id deck-id)]
        (is (= false (map? deck)))
        (is (= true (nil? deck)))))

    (testing "create! - create a new deck"
      (let [new-deck (gen/generate (s/gen ::SUT/deck))
            deck-id (SUT/create! *conn* user-id new-deck)]
        (is (uuid? deck-id))))))
