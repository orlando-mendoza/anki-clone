(ns grok.db.user-test
  (:require
   [clojure.test :refer [is deftest testing use-fixtures]]
   [datomic.api :as d]
   [grok.db.user :as SUT]
   [grok.db.with-db :refer [with-db *conn*]]
   [clojure.spec.alpha :as s]
   [clojure.test.check.generators :as gen]))

(use-fixtures :each with-db)

(deftest user
  (testing "create!"
    (let [user-params (gen/generate (s/gen ::SUT/user))
          uid (SUT/create! *conn* user-params)]
      (is (not (nil? uid)))
      (is (= true (uuid? uid)))))
  (testing "fetch"
    (let [uid (SUT/create! *conn* (gen/generate (s/gen ::SUT/user)))
          user (SUT/fetch (d/db *conn*) uid)]
      (is (= true (s/valid? ::SUT/user user)))))
  (testing "edit"
    (let [uid (SUT/create! *conn* (gen/generate (s/gen ::SUT/user)))
          user (SUT/edit! *conn* uid {:user/username "edit.ed"})]
      (is (= true (s/valid? ::SUT/user user)))
      (is (= "edit.ed" (:user/username user)))))
  (testing "delete!"
    (let [uid (SUT/create! *conn* (gen/generate (s/gen ::SUT/user)))
          user (SUT/delete! *conn* uid)]
      (is (= true (s/valid? ::SUT/user user)))
      (is (= nil (SUT/fetch (d/db *conn*) uid))))))
