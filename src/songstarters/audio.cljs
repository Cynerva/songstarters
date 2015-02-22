(ns songstarters.audio
  (:require [cljs.core.async :refer [chan >!]])
  (:require-macros [cljs.core.async.macros :refer [go]])
)

(defn load-buffer [context url]
  (let [
    c (chan)
    req (js/XMLHttpRequest.)
    _ (do
      (.open req "GET" url true)
      (aset req "responseType" "arraybuffer")
      (aset req "onload" (fn []
        (.decodeAudioData context (aget req "response") #(go (>! c %1)))
      ))
      (.send req)
    )
  ] c)
)

(defn play-buffer [context buffer dests when]
  (let [source (.createBufferSource context)]
    (aset source "buffer" buffer)
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
