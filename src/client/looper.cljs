(ns client.looper
    (:require
        [cljs.core.async :refer [chan >! <!]]
    )
    (:require-macros [cljs.core.async.macros :refer [go]])
)

(defn create-random-looper [params]
    (let [c (chan)]
        (go (let [
            half-duration (/ (:duration params) 2)
            child-params (assoc params :duration half-duration)
            child (<! ((:create-child params) child-params))
        ]
            (>! c (fn [when]
                (child when)
                (child (+ when half-duration))
            ))
        ))
        c
    )
)
