(ns user
  (:require [mount.core :as mount]
            [clojure.tools.namespace.repl :as tn]
            [grok.core]))

(defn refresh-ns
  "Refresh/reloads all namespaces"
  []
  (tn/refresh-all))

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
  (refresh-ns)
  (start))

(comment
  (restart-dev)
  ,)
