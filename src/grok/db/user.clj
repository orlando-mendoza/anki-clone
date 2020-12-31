(ns grok.db.user
  (:require [datomic.api :as d]
            [grok.db.core :refer [conn]]
            [clojure.spec.alpha :as s]
            [clojure.test.check.generators :as gen ]))

(defn validate-email [email]
  (let [email-regex #"^([a-zA-Z0-9_\.\-]+)@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([a-zA-Z0-9\-]+\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\]?)$"]
    (re-matches email-regex email)))

(s/def :user/email
       (s/with-gen
        (s/and validate-email string?)
        #(s/gen #{"john@gmail.com" "jane@me.com" "joe@cloud.com"})))

(s/def :user/password
       (s/with-gen
         (and string? #(> (count %) 6))
        #(s/gen #{"abcdefg" "kaGLKdnas8631nF" "Hgt43ak9yG1"})))

(s/def :user/username
       (s/with-gen
         string?
         #(s/gen #{"cindy.nero" "elba.lazo" "norines.paredes"})))

(s/def :user/token
       (s/with-gen
         string?
         #(s/gen #{"abcdefg" "kaGLKdnas8631nF" "Hgt43ak9yG1"})))

(s/def :user/id uuid?)

(s/def ::user
       (s/keys :req [:user/email :user/password]
               :opt [:user/id :user/token :user/username]))


(defn create! [conn user-params]
  (if (s/valid? ::user user-params)
    (let [user-id (d/squuid)
          tx-data (merge user-params {:user/id user-id})]
      (d/transact conn [tx-data])
      user-id)
    (throw (ex-info "User is invalid"
                    {:grok/error-id :validation
                     :error "Invalid email or password provided"}))))

;; ------------------------------------------------------------
;; Fetch a user by ID

(defn fetch
  ([db user-id]
   (fetch db user-id '[*]))
  ([db user-id pattern]
   (d/q '[:find (pull ?uid pattern) .
          :in $ ?user-id pattern
          :where
          [?uid :user/id ?user-id]]
        db user-id pattern)))

;;------------------------------------------------------------
;; Edit User by ID
(defn edit!
  [conn user-id user-params]
  (if (fetch (d/db conn) user-id)
    (let [tx-data (merge user-params {:user/id user-id})
          db-after (:db-after @(d/transact conn [tx-data]))]
      (fetch db-after user-id))
    (throw (ex-info "Unable to update user"
                    {:grok/error-id :server-error
                     :error "Unable to update user"}))))



;; ------------------------------------------------------------------
;; comment

(comment

  (create! conn (gen/generate (s/gen ::user)))


  (fetch
   (d/db conn)
   #uuid "5fed0349-8bd5-491d-bb72-495e127fd7d7")


  (def sample-user {:user/email "juan@juan.com"
                    :user/password "1234567"})

  (s/valid? ::user sample-user)
  ;; => true
  ;;
  ;; generating random data
  (gen/generate (s/gen :user/email))
  (gen/generate (s/gen :user/password))
  (gen/generate (s/gen ::user))

  (def db (d/db conn))

  (d/q '[:find ?email ?password ?user-id
         :in $ ?user-id
         :where
         [?e :user/id ?user-id]
         [?e :user/email ?email]
         [?e :user/password ?password]]
       db #uuid "5fed0349-8bd5-491d-bb72-495e127fd7d7")
  ;; => #{["jane@me.com" "abcdefg" #uuid "5fed0349-8bd5-491d-bb72-495e127fd7d7"]}


  (d/pull db
          [{:user/id [:db/ident]}
           {:user/email [:db/ident]}
           {:user/password [:db/ident]}]
          [:user/id #uuid "5fed0349-8bd5-491d-bb72-495e127fd7d7"])

  (d/q '[:find (pull ?uid [*]) .
         :in $ ?user-id
         :where
         [?uid :user/id ?user-id]]
       (d/db conn) #uuid "5fed0349-8bd5-491d-bb72-495e127fd7d7")
  ,)
