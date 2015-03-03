(ns songstarters.rules.mixer
  (:require
    [cljs.core.async :refer [chan >! <!]]
  )
  (:require-macros [cljs.core.async.macros :refer [go]])
)

(def rule {:mixer {
  :allow? (fn [params]
    (> (:max-simultaneous params) 1)
  )
  :apply (fn [params]
    (let [
      child-params (assoc params :max-simultaneous
        (/ (:max-simultaneous params) 2)
      )
      mixer [:mixer
        ((:dispatch params) child-params)
        ((:dispatch params) child-params)
      ]
    ] mixer)
  )
  :player (fn [node params]
    (go (let [
      children (loop [nodes (rest node) result []]
        (if (empty? nodes)
          result
          (recur (rest nodes) (conj result
            (<! ((:dispatch params) (first nodes) params))
          ))
        )
      )
      player {
        :play #(let [channels (doseq [child children] ((:play child) %))]
          (go (doseq [channel channels] (<! channel)))
        )
        :stop #(doseq [child children] ((:stop child)))
      }
    ] player))
  )
  :max-time (fn [node when dispatch]
    (apply max (for [child (rest node)] (dispatch child when)))
  )
}})
