(ns client.reverb
    (:require
        [cljs.core.async :refer [chan >! <!]]
        [client.audio :refer [context]]
        [client.sampler :refer [create-random-sampler]]
    )
    (:require-macros [cljs.core.async.macros :refer [go]])
)

(defn create-impulse-response [duration decay]
    (let [
        sample-rate (aget context "sampleRate")
        length (* sample-rate duration)
        buffer (.createBuffer context 2 length sample-rate)
        channels [(.getChannelData buffer 0) (.getChannelData buffer 1)]
        sample-value (fn [index]
            (* (- (* (rand) 2) 1) (Math/pow (- 1 (/ index length)) decay))
        )
    ]
        (doall (for [channel channels i (range length)]
            (aset channel i (sample-value i))
        ))
        buffer
    )
)

(defn create-random-reverb [dest duration]
    (let [c (chan)]
        (go (let [
            convolver (.createConvolver context)
            child (<! (create-random-sampler convolver duration))
        ]
            (aset convolver "buffer" (create-impulse-response 1 2))
            (.connect convolver dest)
            (>! c child)
        ))
        c
    )
)
