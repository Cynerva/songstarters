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
    (let [
      duration (:duration params)
      subdivide 2
      interval (/ duration subdivide)
      child-params (assoc params :duration interval)
      child ((:dispatch params) child-params)
      looper [:looper interval subdivide child]
    ] looper)
  )
  :player (fn [node params]
    (go (let [
      [interval subdivide child-node] (rest node)
      child (<! ((:dispatch params) child-node params))
      player (fn [when]
        (loop [i subdivide when when]
          ; FIXME: should be a (when) call...
          (if (> i 0) (do
            (child when)
            (recur (dec i) (+ when interval))
          ))
        )
      )
    ] player))
  )
}})
