(ns grok.db.user-test
  (:require 
   [clojure.test :refer [is deftest testing use-fixtures]]
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
      (is (= true (uuid? uid))))))
