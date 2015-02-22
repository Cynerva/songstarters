(ns songstarters.rules.looper
  (:require
    [cljs.core.async :refer [chan >! <!]]
  )
  (:require-macros [cljs.core.async.macros :refer [go]])
)

(def rule {:looper {
  :allow? (fn [params]
    (>= (:duration params) (* (:min-note-duration params) 2))
  )
  :apply (fn [params]
    (go (let [
      duration (:duration params)
      interval (/ duration 2)
      child-params (assoc params :duration interval)
      looper [:looper interval (<! ((:dispatch params) child-params))]
    ] looper))
  )
  :player (fn [node params]
    (go (let [
      [interval child-node] (rest node)
      child (<! ((:dispatch params) child-node params))
      player (fn [when]
        (child when)
        (child (+ when interval))
      )
    ] player))
  )
}})
