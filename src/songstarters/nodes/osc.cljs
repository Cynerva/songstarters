(ns songstarters.nodes.osc
  (:require
    [cljs.core.async :refer [chan >! <!]]
    [songstarters.nodes.node :as node]
  )
  (:require-macros [cljs.core.async.macros :refer [go]])
)

(defn play-osc [context dest when duration osc-type freq]
  (let [osc (.createOscillator context)]
    (aset osc "type" osc-type)
    (aset (.-frequency osc) "value" freq)
    (.connect osc dest)
    (.start osc when)
    (.stop osc (+ when duration))
  )
)

(defmethod node/allow? :osc [_ params]
  (< (:duration params) 0.5)
)

(defmethod node/random :osc [_ params]
  (go (let [
    osc-type (rand-nth ["sine" "triangle" "sawtooth" "square"])
    freq (* (rand) 1000)
    osc [:osc osc-type freq]
  ] osc))
)

(defmethod node/player :osc [node params]
  (go (fn [when]
    (play-osc
      (:context params)
      (:dest params)
      when
      (:duration params)
      (get node 1)
      (get node 2)
    )
  ))
)
