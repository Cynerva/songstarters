(ns songstarters.rules.osc
  (:require
    [cljs.core.async :refer [<! timeout]]
    [songstarters.rules.scale :refer [note->freq]]
  )
  (:require-macros [cljs.core.async.macros :refer [go]])
)

(defn play-osc [context dest osc-type freq duration when]
  (let [osc (.createOscillator context)]
    (set! (.-type osc) osc-type)
    (set! (-> osc .-frequency .-value) freq)
    (.connect osc dest)
    (.start osc when)
    (.stop osc (+ when duration))
  )
)

(def rule {:osc {
  :allow? (fn [params]
    (and 
      (<= (:duration params) (:max-note-duration params))
      (contains? params :scale)
    )
  )
  :apply (fn [params]
    (let [
      osc-type (rand-nth ["sine" "triangle" "sawtooth" "square"])
      note (rand-nth (:scale params))
      osc [:osc osc-type note (:duration params)]
    ] osc)
  )
  :player (fn [node params]
    (go (let [
      context (:context params)
      dests (:dests params)
      gain (.createGain context)
      _ (do
        (set! (.-value (.-gain gain)) (/ 1.0 3.0))
        (doseq [dest dests]
          (.connect gain dest)
        )
      )
      osc-type (get node 1)
      freq (note->freq (get node 2))
      duration (get node 3)
      stopped (atom false)
      player {
        :play #(go
          (when-not @stopped
            (<! (timeout (* (- % (.-currentTime context) 1) 1000)))
            (play-osc context gain osc-type freq duration %)
          )
        )
        :stop #(reset! stopped true)
      }
    ] player))
  )
}})
