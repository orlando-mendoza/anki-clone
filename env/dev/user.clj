(ns user
  (:require [mount.core :as mount]
            [grok.core]))

(defn start
  "Mount starts lifecycle of running state"
  []
  (mount/start))

(defn stop
  "Mount stops lifecycle of running state"
  []
  (mount/stop))

(defn restart-dev
  []
  (stop)
  (start))

(comment
  (restart-dev)
  ,)
