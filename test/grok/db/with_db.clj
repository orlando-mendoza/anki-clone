(ns grok.db.with-db
  (:require [clojure.spec.alpha :as s]
            [clojure.test.check.generators :as gen]
            [grok.db.core :as SUT]
            [grok.db.schema :refer [schema]]
            [grok.db.user :as user]
            [datomic.api :as d]))


(def ^:dynamic *conn* nil)

(def user-params )

(def sample-user
  (-> (s/gen ::user/user)
      gen/generate
      (merge {:user/id (d/squuid)
              :user/email "test@test.com"})))

(defn fresh-db []
  (let [db-uri (str "datomic:mem://" (gensym))
        conn (SUT/create-conn db-uri)]
    (d/transact conn schema)
    (d/transact conn [sample-user])
    conn))

(defn with-db [f]
  (binding [*conn* (fresh-db)]
    (f)))
