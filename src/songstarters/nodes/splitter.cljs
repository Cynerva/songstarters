(ns songstarters.nodes.splitter
  (:require
    [cljs.core.async :refer [chan >! <!]]
    [songstarters.nodes.node :as node]
  )
  (:require-macros [cljs.core.async.macros :refer [go]])
)

(defmethod node/allow? :splitter [_ params]
  (> (:duration params) 0.1)
)

(defmethod node/random :splitter [_ params]
  (go (let [
    duration (:duration params)
    interval (/ duration 2)
    child-params (assoc params :duration interval)
    splitter [:splitter
      (<! (node/random :any child-params))
      (<! (node/random :any child-params))
    ]
  ] splitter))
)

(defmethod node/player :splitter [node params]
  (go (let [
    duration (:duration params)
    interval (/ duration 2)
    child-params (assoc params :duration interval)
    first-child (<! (node/player (get node 1) child-params))
    second-child (<! (node/player (get node 2) child-params))
    player (fn [when]
      (first-child when)
      (second-child (+ when interval))
    )
  ] player))
)
