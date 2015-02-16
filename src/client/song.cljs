(ns client.song
    (:require
        [cljs.core.async :refer [chan >! <!]]
        [client.reverb :refer [create-random-reverb]]
    )
    (:require-macros [cljs.core.async.macros :refer [go]])
)

(defn create-random-song [params]
    (let [c (chan)]
        (go (>! c (<! (create-random-reverb params))))
        c
    )
)
