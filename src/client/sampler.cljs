(ns client.sampler
    (:require
        [cljs.reader :refer [read-string]]
        [cljs.core.async :refer [chan >! <!]]
        [cljs-http.client :as http]
        [client.audio :refer [context load-buffer]]
    )
    (:require-macros [cljs.core.async.macros :refer [go]])
)

(defn play-buffer [buffer dest when]
    (let [source (.createBufferSource context)]
        (aset source "buffer" buffer)
        (.connect source dest)
        (.start source when)
    )
)

(defn create-sampler [dest sample-path]
    (let [c (chan)]
        (go (let [buffer (<! (load-buffer sample-path))]
            (>! c (fn [when] (play-buffer buffer dest when)))
        ))
        c
    )
)

(defn create-random-sampler [dest duration]
    (let [c (chan)]
        (go (let [
            response (<! (http/get "/sampleList"))
            sample-list (read-string (:body response))
            sample-path (rand-nth sample-list)
            sampler (<! (create-sampler dest sample-path))
        ]
            (>! c sampler)
        ))
        c
    )
)
