(ns songstarters.rules.splitter
  (:require
    [cljs.core.async :refer [chan >! <!]]
  )
  (:require-macros [cljs.core.async.macros :refer [go]])
)

(def rule {:splitter {
  :allow? (fn [params]
    (> (:duration params) 0.2)
  )
  :apply (fn [params]
    (go (let [
      duration (:duration params)
      interval (/ duration 2)
      child-params (assoc params :duration interval)
      splitter [:splitter interval
        (<! ((:dispatch params) child-params))
        (<! ((:dispatch params) child-params))
      ]
    ] splitter))
  )
  :player (fn [node params]
    (go (let [
      interval (get node 1)
      first-child (<! ((:dispatch params) (get node 2) params))
      second-child (<! ((:dispatch params) (get node 3) params))
      player (fn [when]
        (first-child when)
        (second-child (+ when interval))
      )
    ] player))
  )
}})
