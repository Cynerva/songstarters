(ns songstarters.rules.reverb
  (:require
    [cljs.core.async :refer [chan >! <!]]
  )
  (:require-macros [cljs.core.async.macros :refer [go]])
)

(defn create-impulse-response [context duration decay]
  (let [
    sample-rate (aget context "sampleRate")
    length (* sample-rate duration)
    buffer (.createBuffer context 2 length sample-rate)
    channels [(.getChannelData buffer 0) (.getChannelData buffer 1)]
    sample-value (fn [index]
      (* (- (* (rand) 2) 1) (Math/pow (- 1 (/ index length)) decay))
    )
    _ (doseq [channel channels i (range length)]
      (aset channel i (sample-value i))
    )
  ] buffer)
)

(defn create-convolver [context dest]
  (let [convolver (.createConvolver context)]
    (aset convolver "buffer" (create-impulse-response context 1 2))
    (.connect convolver dest)
    convolver
  )
)

(def rule {:reverb {
  :allow? (fn [params]
    (not (contains? params :reverb))
  )
  :apply (fn [params]
    (go (let [
      child-params (assoc params :reverb true)
      reverb [:reverb (<! ((:dispatch params) child-params))]
    ] reverb))
    )
  :player (fn [node params]
    (let [
      context (:context params)
      dest (:dest params)
      convolver (create-convolver context dest)
      child-params (assoc params :dest convolver)
      child ((:dispatch params) (get node 1) child-params)
    ] child)
  )
}})