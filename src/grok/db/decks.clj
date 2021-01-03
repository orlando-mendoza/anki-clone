(ns grok.db.decks
  (:require [datomic.api :as d]
            [grok.db.core :refer [conn]]))

;; - List
(defn browse
  "Browse a list of decks belonging to a certain user"
  [db user-id]
  (d/q '[:find [(pull ?deck [*]) ...]
         :in $ ?uid
         :where
         [?user :user/id ?uid]
         [?deck :deck/author ?user]]
       db user-id))

;; - Read
(defn fetch
  "Fetch a single deck by ID"
  [db user-id deck-id]
  nil)


;; - Create
(defn create!
  "Create a new deck"
  [conn user-id deck-params])

;; - Update
(defn edit!
  "Edit an existing deck"
  [conn user-id deck-id deck-params])


;; - Delete
(defn delete!
  "Delete a deck"
  [conn user-id deck-id])

(comment
  ;; lets create a deck
  (def new-deck
    {:deck/id (d/squuid)
     :deck/title "Learning Clojure"
     :deck/tags #{"Clojure" "Programming"}
     :deck/author [:user/id #uuid "5fed0349-8bd5-491d-bb72-495e127fd7d7"] })

  (def deck2
    {:deck/id (d/squuid)
     :deck/title "Learning Domain-Driven Design"
     :deck/tags #{"Software" "Architecture" "Domain" "Design"}
     :deck/author [:user/id #uuid "5fed0349-8bd5-491d-bb72-495e127fd7d7"]})

  (d/transact conn [new-deck])
  (d/transact conn [deck2])

  (d/q '[:find [(pull ?deck [*]) ...]
         :in $ ?uid
         :where
         [?user :user/id ?uid]
         [?deck :deck/author ?user]]
       (d/db conn) #uuid "5fed0349-8bd5-491d-bb72-495e127fd7d7")

  ,)
