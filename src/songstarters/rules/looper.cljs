(ns songstarters.rules.looper
  (:require
    [cljs.core.async :refer [chan >! <!]]
  )
  (:require-macros [cljs.core.async.macros :refer [go]])
)

(def rule {:looper {
  :allow? (fn [params]
    (> (:duration params) 0.1)
  )
  :apply (fn [params]
    (go (let [
      duration (:duration params)
      interval (/ duration 2)
      child-params (assoc params :duration interval)
      looper [:looper (<! ((:dispatch params) child-params))]
    ] looper))
  )
  :player (fn [node params]
    (go (let [
      duration (:duration params) ; fixme
      interval (/ duration 2)
      child-params (assoc params :duration interval) ; fixme
      child (<! ((:dispatch params) (first (rest node)) child-params))
      player (fn [when]
        (child when)
        (child (+ when interval))
      )
    ] player))
  )
}})
