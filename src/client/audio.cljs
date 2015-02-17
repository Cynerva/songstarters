(ns client.audio
  (:require [cljs.core.async :refer [chan >!]])
  (:require-macros [cljs.core.async.macros :refer [go]])
)

(def context (js/AudioContext.))

(defn load-buffer [url]
  (let [c (chan)]
    (let [req (js/XMLHttpRequest.)]
      (.open req "GET" url true)
      (aset req "responseType" "arraybuffer")
      (aset req "onload" (fn []
        (.decodeAudioData context (aget req "response") #(go (>! c %1)))
      ))
      (.send req)
    )
    c
  )
)
