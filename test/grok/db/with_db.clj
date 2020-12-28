(ns grok.db.with-db
  (:require
    [grok.db.schema :refer [schema]]
    [datomic.api :as d]
    [grok.db.core :as SUT]))

(def ^:dynamic *conn* nil)

(defn fresh-db []
  (let [db-uri (str "datomic:mem://" (gensym))
        conn (SUT/create-conn db-uri)]
    (d/transact conn schema)
    conn))

(defn with-db [f]
  (binding [*conn* (fresh-db)]
    (f)))