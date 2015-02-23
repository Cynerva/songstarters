(ns songstarters.rules.splitter
  (:require
    [cljs.core.async :refer [chan >! <!]]
  )
  (:require-macros [cljs.core.async.macros :refer [go]])
)

(def rule {:splitter {
  :allow? (fn [params]
    (>= (:duration params) (* (:min-note-duration params) 2))
  )
  :apply (fn [params]
    (go (let [
      duration (:duration params)
      subdivide 2
      interval (/ duration subdivide)
      child-params (assoc params :duration interval)
      children (loop [i subdivide result []]
        (if (<= i 0)
          result
          (recur (dec i) (conj result
            (<! ((:dispatch params) child-params))
          ))
        )
      )
      splitter (into [:splitter interval] children)
    ] splitter))
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
      player (fn [when]
        (loop [children children when when]
          ; FIXME: This should be a (when) call, not (if (do))
          ; Unfortunately, the variable itself is named "when" so we'd have to use
          ; the fully qualified name. Need to rename this param project-wide.
          (if (seq children) (do
            ((first children) when)
            (recur (rest children) (+ when interval))
          ))
        )
      )
    ] player))
  )
}})
