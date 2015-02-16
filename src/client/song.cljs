(ns client.song
    (:require
        [cljs.core.async :refer [chan >! <!]]
        [client.audio :refer [context]]
        [client.reverb :refer [create-random-reverb]]
        [client.sampler :refer [create-random-sampler]]
        [client.looper :refer [create-random-looper]]
        [client.splitter :refer [create-random-splitter]]
        [client.mixer :refer [create-random-mixer]]
    )
    (:require-macros [cljs.core.async.macros :refer [go]])
)

(def terminals [
    create-random-sampler
])

(def nodes [
    ;create-random-reverb
    create-random-looper
    create-random-splitter
    ;create-random-mixer
])

(defn create-random-song [params]
    (let [c (chan)]
        (go (let [
            params (merge {
                :dest (aget context "destination")
                :duration 10
                :create-child create-random-song
            } params)
            duration (:duration params)
            options (if (< duration 0.1) terminals nodes)
            node (<! ((rand-nth options) params))
        ]
            (.log js/console "Meep")
            (>! c node)
        ))
        c
    )
)
