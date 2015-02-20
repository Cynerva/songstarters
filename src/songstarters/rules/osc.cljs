(ns songstarters.rules.osc
  (:require
    [cljs.core.async :refer [chan >! <!]]
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

(def rule {:osc {
  :allow? (fn [params]
    (< (:duration params) 0.5)
  )
  :apply (fn [params]
    (go (let [
      osc-type (rand-nth ["sine" "triangle" "sawtooth" "square"])
      freq (* (rand) 1000)
      osc [:osc osc-type freq]
    ] osc))
  )
  :player (fn [node params]
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
}})
