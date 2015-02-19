(ns songstarters.nodes.reverb
  (:require
    [cljs.core.async :refer [chan >! <!]]
  )
  (:require-macros [cljs.core.async.macros :refer [go]])
)

;(defn create-impulse-response [duration decay]
;  (let [
;    sample-rate (aget context "sampleRate")
;    length (* sample-rate duration)
;    buffer (.createBuffer context 2 length sample-rate)
;    channels [(.getChannelData buffer 0) (.getChannelData buffer 1)]
;    sample-value (fn [index]
;      (* (- (* (rand) 2) 1) (Math/pow (- 1 (/ index length)) decay))
;    )
;    _ (doseq [channel channels i (range length)]
;      (aset channel i (sample-value i))
;    )
;  ] buffer)
;)

;(defn create-random-reverb [params]
;  (let [c (chan)]
;    (go (let [
;      convolver (.createConvolver context)
;      child (<! ((:create-child params) (assoc params :dest convolver)))
;      _ (do
;        (aset convolver "buffer" (create-impulse-response 0.5 2))
;        (.connect convolver (:dest params))
;        (>! c child)
;      )
;    ]))
;    c
;  )
;)
