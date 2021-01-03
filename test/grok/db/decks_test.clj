(ns grok.db.decks-test
  (:require  [clojure.test :refer [is deftest testing use-fixtures]]
             [datomic.api :as d]
             [clojure.spec.alpha :as s]
             [clojure.test.check.generators :as gen]
             [grok.db.decks :as decks]
             [grok.db.user :as user]
             [grok.db.with-db :refer [with-db *conn*]]))


(use-fixtures :each with-db)

(deftest decks
  (testing "browse - returns empty vector if the user has not created any deck"
    (let [user-params (gen/generate (s/gen ::user/user))
          uid (user/create! *conn* user-params)
          decks (decks/browse (d/db *conn*) uid)]
      (is (= true (vector? decks)))
      (is (= true (empty? decks)))))

  (testing "browse - returns vector of cards if available"
    (let [user-params (gen/generate (s/gen ::user/user))
          uid (user/create! *conn* user-params)
          new-deck {:deck/id (d/squuid)
                    :deck/title "Learning Clojure"
                    :deck/tags #{"Clojure" "programming"}
                    :deck/author [:user/id uid]}]
      @(d/transact *conn* [new-deck])
      (let [decks (decks/browse (d/db *conn*) uid)]
        (is (= true (vector? decks)))
        (is (= false (empty? decks)))))))
