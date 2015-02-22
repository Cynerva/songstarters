(ns songstarters.rules.osc
  (:require
    [cljs.core.async :refer [chan >! <!]]
    [songstarters.rules.scale :refer [note->freq]]
  )
  (:require-macros [cljs.core.async.macros :refer [go]])
)

(defn play-osc [context dest osc-type freq duration when]
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
    (<= (:duration params) (:max-note-duration params))
  )
  :apply (fn [params]
    (go (let [
      osc-type (rand-nth ["sine" "triangle" "sawtooth" "square"])
      note (rand-nth (:scale params))
      osc [:osc osc-type note (:duration params)]
    ] osc))
  )
  :player (fn [node params]
    (go (let [
      context (:context params)
      dest (:dest params)
      gain (.createGain context)
      _ (do
        (set! (.-value (.-gain gain)) 0.5)
        (.connect gain dest)
      )
      osc-type (get node 1)
      freq (note->freq (get node 2))
      duration (get node 3)
      player (fn [when] (play-osc context gain osc-type freq duration when))
    ] player))
  )
}})
