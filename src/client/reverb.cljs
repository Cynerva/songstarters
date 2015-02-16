(ns client.reverb
    (:require
        [client.audio :refer [context]]
    )
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

; TODO: figure out how to pass in children properly, duration, decay
(defn create-reverb [child]
    (let [convolver (.createConvolver context)]
        (aset convolver "buffer" (create-impulse-response 1 2))
        (fn [dest when]
            (.connect convolver dest)
            (child convolver when)
        )
    )
)
