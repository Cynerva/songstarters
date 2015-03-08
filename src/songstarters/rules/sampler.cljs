(ns songstarters.rules.sampler
  (:require
    [cljs.reader :refer [read-string]]
    [cljs.core.async :refer [<! timeout]]
    [cljs-http.client :as http]
    [songstarters.audio :refer [load-buffer play-buffer]]
  )
  (:require-macros [cljs.core.async.macros :refer [go]])
)

(defn sample-paths []
  (go (->
    "sampleList"
    http/get <!
    :body
    read-string
  ))
)

(def rule {:sampler {
  :allow? (fn [params]
    (<= (:duration params) (:max-note-duration params))
  )
  :apply (fn [params]
    [:sampler (:duration params) (rand-nth (:sample-paths params))]
  )
  :player (fn [node params]
    (go (let [
      context (:context params)
      dests (:dests params)
      duration (get node 1)
      sample-path (str "samples/" (get node 2))
      buffer (<! (load-buffer context sample-path))
      playback-rate (/ (.-duration buffer) duration)
      stopped (atom false)
      player {
        :play #(go
          (when-not @stopped
            (<! (timeout (* (- % (.-currentTime context) 1) 1000)))
            (play-buffer context buffer dests % playback-rate)
          )
        )
        :stop #(reset! stopped true)
      }
    ] player))
  )
}})
