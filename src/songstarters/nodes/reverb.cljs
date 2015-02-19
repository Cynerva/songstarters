(ns songstarters.nodes.reverb
  (:require
    [cljs.core.async :refer [chan >! <!]]
    [songstarters.nodes.node :as node]
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

(defmethod node/allow? :reverb [_ params]
  (not (contains? params :reverb))
)

(defmethod node/random :reverb [_ params]
  (go (let [
    child-params (assoc params :reverb true)
    reverb [:reverb (<! (node/random :any child-params))]
  ] reverb))
)

(defmethod node/player :reverb [node params]
  (let [
    context (:context params)
    dest (:dest params)
    convolver (create-convolver context dest)
    child-params (assoc params :dest convolver)
    child (node/player (get node 1) child-params)
  ] child)
)
