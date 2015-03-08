(ns songstarters.rules.splitter
  (:require
    [cljs.core.async :refer [<! timeout]]
  )
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
)

(def rule {:splitter {
  :allow? (fn [params]
    (>= (:duration params) (* (:min-note-duration params) 2))
  )
  :apply (fn [params]
    (let [
      duration (:duration params)
      subdivide 2
      interval (/ duration subdivide)
      child-params (assoc params :duration interval)
      children (loop [i subdivide result []]
        (if (<= i 0)
          result
          (recur (dec i) (conj result
            ((:dispatch params) child-params)
          ))
        )
      )
      splitter (into [:splitter interval] children)
    ] splitter)
  )
  :player (fn [node params]
    (go (let [
      [interval & child-nodes] (rest node)
      children (loop [child-nodes child-nodes result []]
        (if (empty? child-nodes)
          result
          (recur (rest child-nodes) (conj result
            (<! ((:dispatch params) (first child-nodes) params))
          ))
        )
      )
      context (:context params)
      player {
        :play (fn [when]
          (go-loop [children children when when]
            ; FIXME: should not have to specify clojure.core/when here
            (clojure.core/when (seq children)
              (<! ((:play (first children)) when))
              (recur (rest children) (+ when interval))
            )
          )
        )
        :stop #(doseq [child children] ((:stop child)))
      }
    ] player))
  )
}})
