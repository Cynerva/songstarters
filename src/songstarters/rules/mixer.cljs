(ns songstarters.rules.mixer
  (:require
    [cljs.core.async :refer [chan >! <!]]
  )
  (:require-macros [cljs.core.async.macros :refer [go]])
)

(def rule {:mixer {
  :allow? (fn [params]
    (not (contains? params :mixer))
  )
  :apply (fn [params]
    (go (let [
      child-params (assoc params :mixer true)
      mixer [:mixer
        (<! ((:dispatch params) child-params))
        (<! ((:dispatch params) child-params))
      ]
    ] mixer))
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
      player (fn [when]
        (doseq [child children]
          (child when)
        )
      )
    ] player))
  )
}})
