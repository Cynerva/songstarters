(ns client.sampler
    (:require
        [cljs.core.async :refer [<!]]
        [client.audio :refer [context load-buffer]]
    )
    (:require-macros
        [cljs.core.async.macros :refer [go]]
    )
)

(defn play-buffer [buffer dest when]
    (let [source (.createBufferSource context)]
        (aset source "buffer" buffer)
        (.connect source dest)
        (.start source when)
    )
)

; TODO: make this support multiple sample files
(defn create-sampler []
    (fn [dest when]
        (go (play-buffer (<! (load-buffer "samples/Chip_Snr_4.wav")) dest when))
    )
)
