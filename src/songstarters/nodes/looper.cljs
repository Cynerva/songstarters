(ns songstarters.nodes.looper
  (:require
    [cljs.core.async :refer [chan >! <!]]
    [songstarters.nodes.node :as node]
  )
  (:require-macros [cljs.core.async.macros :refer [go]])
)

(defmethod node/allow? :looper [_ params]
  (> (:duration params) 0.1)
)

(defmethod node/random :looper [_ params]
  (go (let [
    duration (:duration params)
    interval (/ duration 2)
    child-params (assoc params :duration interval)
    looper [:looper (<! (node/random :any child-params))]
  ] looper))
)

(defmethod node/player :looper [node params]
  (go (let [
    duration (:duration params)
    interval (/ duration 2)
    child-params (assoc params :duration interval)
    child (<! (node/player (first (rest node)) child-params))
    player (fn [when]
      (child when)
      (child (+ when interval))
    )
  ] player))
)
