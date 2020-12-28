(ns grok.db.user
  (:require 
   [datomic.api :as d]))

;; To create a user, the following values are required 
;; - email
;; - password
;; - the rest are optional values

(defn create! [conn user-params]
  (let [user-id (d/squuid)
        tx-data (merge {:user/id user-id} user-params)]
    (d/transact conn [tx-data])
    user-id))
