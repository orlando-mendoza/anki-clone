(ns grok.db.core
  (:require [datomic.api :as d]
            [grok.config :refer [env]]
            [mount.core :as mount :refer [defstate]]
            [grok.db.schema :refer [schema]]))

(defn create-conn [db-uri]
  (when db-uri
    (d/create-database db-uri)
    (let [conn (d/connect db-uri)]
      conn)))

;; the connection
(defstate conn
  :start (create-conn (:database-uri env))
  :stop (.release conn))

;; ------------------------------------------------------------
;; Create schema and transact into the database
(comment
  (def tx @(d/transact conn schema)))



(comment
  ;; First create a database using the REPL from the Peer section
  ;; cd /Users/omendozar/datomic/datomic-pro-1.0.6222
  ;; $ bin/repl
  ;; user=> (require '[datomic.api :as d])
  ;; nil
  ;; user=> (def db-uri "datomic:dev://localhost:4334/grok")
  ;; #'user/db-uri
  ;; user=> (d/create-database db-uri)
  ;; true
  ;; ------------------------------------------------------------
  ;; Then start a Peer server

  ;; $ bin/run -m datomic.peer-server -h localhost -p 8998 -a myaccesskey,mysecret -d grok,datomic:dev://localhost:4334/grok
  ;; Serving datomic:dev://localhost:4334/grok as grok

  ;; Transact to test the connection


  (keys tx)
  ;; => (:db-before :db-after :tx-data :tempids)

  (def db-before (:db-before tx))
  (def db-after (:db-after tx))

  ;; ------------------------------------------------------------
  ;; Query

  (d/q '[:find ?doc
         :where
         [_ :db/doc ?doc]]
       db-after)

  (mount/stop)

  ,)
