(ns grok.db.user
  (:require [datomic.api :as d]
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

(s/def :user/id string?)

(s/def ::user
       (s/keys :req [:user/email :user/password]
               :opt [:user/id :user/token :user/username]))


(defn create! [conn user-params]
  (if (s/valid? ::user user-params)
    (let [user-id (d/squuid)
          tx-data (merge {:user/id user-id} user-params)]
      (d/transact conn [tx-data])
      user-id)
    (throw (ex-info "User is invalid"
                    {:grok/error-id :validation
                     :error "Invalid email or password provided"}))))

(comment
  (def sample-user {:user/email "juan@juan.com"
                    :user/password "1234567"})
  
  (s/valid? ::user sample-user)
  ;; => true
  ;; 
  ;; generating random data
  (gen/generate (s/gen :user/email))
  (gen/generate (s/gen :user/password))
  (gen/generate (s/gen ::user))

  
  ,)