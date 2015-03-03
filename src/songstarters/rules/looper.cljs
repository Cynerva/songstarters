(ns songstarters.rules.looper
  (:require
    [cljs.core.async :refer [<! timeout]]
  )
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
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
      context (:context params)
      player {
        :play (fn [when]
          (go-loop [i subdivide when when]
            ; FIXME: should not have to specify clojure.core/when here
            (clojure.core/when (> i 0)
              (<! ((:play child) when))
              (recur (dec i) (+ when interval))
            )
          )
        )
        :stop (:stop child)
      }
    ] player))
  )
  :max-time (fn [node when dispatch]
    (let [[interval subdivide child] (rest node)]
      (dispatch child (+ when (* interval (dec subdivide))))
    )
  )
}})
