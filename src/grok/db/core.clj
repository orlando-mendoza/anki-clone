(ns grok.db.core
  (:require
   [datomic.client.api :as d]
   [config.core :refer [env]]
   [grok.db.schema :refer [schema]]))

(:database-uri env)

;; the config
(def cfg {:server-type :peer-server
          :access-key "myaccesskey"
          :secret "mysecret"
          :endpoint "localhost:8998"
          :validate-hostnames false})
;; the client
(def client (d/client cfg))

;; the connection
(def conn (d/connect client {:db-name "grok"}))

;; ------------------------------------------------------------
;; Create schema and transact into the database
(def tx (d/transact conn {:tx-data schema}))



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

  ,)
