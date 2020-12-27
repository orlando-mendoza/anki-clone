(ns grok.db.with-db
  (:require
    [grok.db.schema :refer [schema]]
    [datomic.api :as d]))

(def ^:dynamic *conn* nil)

(defn create-conn [db-uri]
  (when db-uri
    (d/create-database db-uri)
    (let [conn (d/connect db-uri)]
      conn)))

(defn fresh-db []
  (let [db-uri (str "datomic:mem://" (gensym))
        conn (create-conn db-uri)]
    (d/transact conn schema)
    conn ))

(defn with-db [f]
  (binding [*conn* (fresh-db)]
    (f)))