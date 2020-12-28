(ns grok.db.user-test
  (:require 
   [clojure.test :refer [is deftest testing use-fixtures]]
   [grok.db.user :as SUT]
   [grok.db.with-db :refer [with-db *conn*]]))

(use-fixtures :each with-db)

(deftest user
  (testing "create!"
    (let [user-params {:user/email "john@john.com"
                       :user/password "password"}
          uid (SUT/create! *conn* user-params)]
      (is (not (nil? uid)))
      (is (= true (uuid? uid))))))
