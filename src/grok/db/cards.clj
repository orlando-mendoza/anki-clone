(ns grok.db.cards
  (:require [clojure.spec.alpha :as s]
            [datomic.api :as d]
            [clojure.test.check.generators :as gen]))

(s/def :card/id uuid?)
(s/def :card/front string?)
(s/def :card/back string?)
(s/def :card/progress (s/and #(> % 0) int?))
(s/def :card/next-study-date inst?)

(s/def ::card
  (s/keys :req [:card/front :card/back]
          :opt [:card/id :card/progress :card/next-study-date]))

;; List - list all the cards belonging to a certain deck
;; [*] means fetch everything belonging to card entity
(defn browse
  "list all the cards belonging to a certain deck"
  [db deck-id]
  (d/q '[:find [(pull  ?cards [*]) ...]
         :in $ ?deck-id
         :where
         [?deck :deck/id ?deck-id]
         [?cards :card/deck ?deck]]
       db deck-id))

;; Read - Fetch a single card by ID 
(defn fetch
  "Fetch a single card by ID, return nil if not found"
  [db deck-id card-id]
  (d/q '[:find (pull ?card [*]) . 
         :in $ ?deck-id ?card-id
         :where
         [?deck :deck/id ?deck-id]
         [?card :card/id ?card-id]
         [?card :card/deck ?deck]]
       db deck-id card-id))
