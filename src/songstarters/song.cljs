(ns songstarters.song
  (:require
    [cljs.core.async :refer [chan >! <!]]
    [songstarters.audio :refer [context]]
    [songstarters.reverb :refer [create-random-reverb]]
    [songstarters.sampler :refer [create-random-sampler]]
    [songstarters.looper :refer [create-random-looper]]
    [songstarters.splitter :refer [create-random-splitter]]
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
])

(defn create-random-song
  ([] (create-random-song {}))
  ([params]
    (let [c (chan)]
      (go (let [
        default-params {
          :dest (.-destination context)
          :duration 60
          :create-child create-random-song
        }
        params (merge default-params params)
        duration (:duration params)
        options (if (< duration 0.1) terminals nodes)
        song (<! ((rand-nth options) params))
        _ (>! c song)
        _ (.log js/console "Created a node")
      ]))
      c
    )
  )
)
