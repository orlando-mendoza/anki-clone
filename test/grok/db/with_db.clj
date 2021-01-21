(ns grok.db.with-db
 (:require [clojure.spec.alpha :as s]
           [clojure.test.check.generators :as gen]
           [grok.db.core :as SUT]
           [grok.db.schema :refer [schema]]
           [grok.db.user :as user]
           [datomic.api :as d]))


(def ^:dynamic *conn* nil)

(def user-params)

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

(comment

 ;; Trying concurrency on Brave and True Clojure book
  (future (Thread/sleep 4000)
          (println "I'll print after 4 seconds"))
  (println "I'll print immediately")

 (let [result (future (println "this prints once")
                      (+ 1 1))]
   (println "deref: " (deref result))
   (println "@: " @result))




 ,)
