(ns client.mixer
    (:require
        [cljs.core.async :refer [chan >! <!]]
    )
    (:require-macros [cljs.core.async.macros :refer [go]])
)

; TODO: maybe run children through gain nodes to control volume
(defn create-random-mixer [params]
    (let [c (chan)]
        (go (let [
            first-child (<! ((:create-child params) params))
            second-child (<! ((:create-child params) params))
        ]
            (>! c (fn [when]
                (first-child when)
                (second-child when)
            ))
        ))
        c
    )
)
