(ns client.splitter
  (:require [cljs.core.async :refer [chan >! <!]])
  (:require-macros [cljs.core.async.macros :refer [go]])
)

(defn create-random-splitter [params]
  (let [c (chan)]
    (go (let [
      half-duration (/ (:duration params) 2)
      child-params (assoc params :duration half-duration)
      first-child (<! ((:create-child params) child-params))
      second-child (<! ((:create-child params) child-params))
      splitter (fn [when]
        (first-child when)
        (second-child (+ when half-duration))
      )
      _ (>! c splitter)
    ]))
    c
  )
)
