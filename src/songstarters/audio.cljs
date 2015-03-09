(ns songstarters.audio
  (:require [cljs.core.async :refer [chan >!]])
  (:require-macros [cljs.core.async.macros :refer [go]])
)

(defn load-buffer [context url]
  (let [
    c (chan)
    req (js/XMLHttpRequest.)
    _ (do
      (.open req "GET" (js/encodeURIComponent url) true)
      (set! (.-responseType req) "arraybuffer")
      (set! (.-onload req) (fn []
        (.decodeAudioData context (.-response req) #(go (>! c %1)))
      ))
      (.send req)
    )
  ] c)
)

(defn play-buffer [context buffer dests when playback-rate]
  (let [source (.createBufferSource context)]
    (set! (.-buffer source) buffer)
    (set! (.-value (.-playbackRate source)) playback-rate)
    (doseq [dest dests]
      (.connect source dest)
    )
    (.start source when)
  )
)

(defn new-compressor [context dest]
  (let [compressor (.createDynamicsCompressor context)]
    (set! (.-value (.-threshold compressor)) -1)
    (set! (.-value (.-knee compressor)) 1)
    (set! (.-value (.-ratio compressor)) 128)
    (set! (.-value (.-attack compressor)) 0)
    (set! (.-value (.-release compressor)) 0.5)
    (.connect compressor dest)
    compressor
  )
)

(defn download-buffer [buffer filename]
  ; Kinda hacky
  (js/saveAs (js/Blob. (clj->js [(js/exportWAV buffer)])) filename)
)
