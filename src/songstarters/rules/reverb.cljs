(ns songstarters.rules.reverb
  (:require
    [cljs.core.async :refer [chan >! <!]]
  )
  (:require-macros [cljs.core.async.macros :refer [go]])
)

(def noise-colors {
  :white (fn [length]
    (loop [l length result []]
      (if (= l 0)
        result
        (recur (dec l) (conj result (- (rand 2) 1)))
      )
    )
  )
  :pink (fn [length]
    (loop [l length result [] b0 0 b1 0 b2 0 b3 0 b4 0 b5 0 b6 0]
      (if (= l 0)
        result
        (let [
          white (- (rand 2) 1)
          b0 (+ (* 0.99886 b0) (* white 0.0555179)) 
          b1 (+ (* 0.99332 b1) (* white 0.0750759))
          b2 (+ (* 0.96900 b2) (* white 0.1538520))
          b3 (+ (* 0.86650 b3) (* white 0.3104856))
          b4 (+ (* 0.55000 b4) (* white 0.5329522))
          b5 (- (* -0.7616 b5) (* white 0.0168980))
          pink (+ b0 b1 b2 b3 b4 b5 b6 (* white 0.5362))
          b6 (* white 0.115926)
        ] (recur (dec l) (conj result pink) b0 b1 b2 b3 b4 b5 b6))
      )
    )
  )
  :brown (fn [length]
    (loop [l length result [] last 0]
      (if (= l 0)
        result
        (let [
          white (- (rand 2) 1)
          value (/ (+ last (* white 0.02)) 1.02)
        ] (recur (dec l) (conj result value) value))
      )
    )
  )
})

(defn impulse-response [context color duration decay]
  (let [
    sample-rate (.-sampleRate context)
    length (* sample-rate duration)
    buffer (.createBuffer context 2 length sample-rate)
    channels [(.getChannelData buffer 0) (.getChannelData buffer 1)]
    noise ((noise-colors color) length)
    _ (doseq [channel channels i (range length)]
      (aset channel i (*
        (get noise i)
        (Math/pow (- 1 (/ i length)) decay)
      ))
    )
  ] buffer)
)

(def default-duration 1)

(defn create-convolver [context dests color]
  (let [convolver (.createConvolver context)]
    (set! (.-buffer convolver) (impulse-response context color default-duration 2))
    (doseq [dest dests]
      (.connect convolver dest)
    )
    convolver
  )
)

(defn create-gain [context dests value]
  (let [gain (.createGain context)]
    (set! (.-value (.-gain gain)) value)
    (doseq [dest dests]
      (.connect gain dest)
    )
    gain
  )
)

(def rule {:reverb {
  :allow? (fn [params]
    (not (contains? params :reverb))
  )
  :apply (fn [params]
    (let [
      child-params (assoc params :reverb true)
      color (rand-nth (keys noise-colors))
      dry-gain (rand)
      reverb [:reverb color dry-gain ((:dispatch params) child-params)]
    ] reverb)
  )
  :player (fn [node params]
    (let [
      context (:context params)
      dests (:dests params)
      color (get node 1)
      dry-gain (get node 2)
      convolver (create-convolver context dests color)
      gain (create-gain context dests dry-gain)
      child-params (assoc params :dests [gain convolver])
      child ((:dispatch params) (get node 3) child-params)
    ] child)
  )
  :max-time (fn [node when dispatch]
    (+ (dispatch (last node) when) default-duration)
  )
}})
