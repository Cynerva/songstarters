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

(defn play-buffer [context buffer dest when]
  (let [source (.createBufferSource context)]
    (aset source "buffer" buffer)
    (.connect source dest)
    (.start source when)
  )
)
