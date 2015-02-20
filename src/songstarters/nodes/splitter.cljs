(ns songstarters.nodes.splitter
  (:require
    [cljs.core.async :refer [chan >! <!]]
  )
  (:require-macros [cljs.core.async.macros :refer [go]])
)

(def rule {:splitter {
  :allow? (fn [params]
    (> (:duration params) 0.1)
  )
  :apply (fn [params]
    (go (let [
      duration (:duration params)
      interval (/ duration 2)
      child-params (assoc params :duration interval)
      splitter [:splitter
        (<! ((:dispatch params) child-params))
        (<! ((:dispatch params) child-params))
      ]
    ] splitter))
  )
  :player (fn [node params]
    (go (let [
      duration (:duration params) ; fixme
      interval (/ duration 2)
      child-params (assoc params :duration interval)
      first-child (<! ((:dispatch params) (get node 1) child-params))
      second-child (<! ((:dispatch params) (get node 2) child-params))
      player (fn [when]
        (first-child when)
        (second-child (+ when interval))
      )
    ] player))
  )
}})
