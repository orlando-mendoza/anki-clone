(ns grok.config
  (:require [mount.core :refer [defstate]]
            [config.core :as config]))

(defstate env
  :start config/env)
