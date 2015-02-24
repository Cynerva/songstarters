(ns songstarters.rules.sampler
  (:require
    [cljs.reader :refer [read-string]]
    [cljs.core.async :refer [chan >! <!]]
    [cljs-http.client :as http]
    [songstarters.audio :refer [load-buffer play-buffer]]
  )
  (:require-macros [cljs.core.async.macros :refer [go]])
)

; TODO: memoize sample list
(defn random-sample-path []
  (go (->
    "sampleList"
    http/get <!
    :body
    read-string
    rand-nth
  ))
)

(def rule {:sampler {
  :allow? (fn [params]
    (<= (:duration params) (:max-note-duration params))
  )
  :apply (fn [params]
    (go [:sampler (:duration params) (<! (random-sample-path))])
  )
  :player (fn [node params]
    (go (let [
      context (:context params)
      dests (:dests params)
      duration (get node 1)
      sample-path (get node 2)
      buffer (<! (load-buffer context sample-path))
      playback-rate (/ (.-duration buffer) duration)
      player (fn [when] (play-buffer context buffer dests when playback-rate))
    ] player))
  )
}})
