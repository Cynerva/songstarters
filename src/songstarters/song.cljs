(ns songstarters.song
  (:require
    [cljs.core.async :refer [<!]]
    [songstarters.nodes.sampler :as sampler]
    [songstarters.nodes.looper :as looper]
    [songstarters.nodes.splitter :as splitter]
    [songstarters.nodes.reverb :as reverb]
    [songstarters.nodes.osc :as osc]
  )
  (:require-macros [cljs.core.async.macros :refer [go]])
)

(def rules (merge
  sampler/rule
  looper/rule
  splitter/rule
  reverb/rule
  osc/rule
))

(defn dispatch-apply [params]
  ((:apply
    (rand-nth
      (filter #((:allow? %) params) (vals rules))
    )
  ) params)
)

(defn dispatch-player [node params]
  ((:player ((first node) rules)) node params)
)

(defn random-song
  ([] (random-song {}))
  ([params]
    (dispatch-apply (merge {
      :duration 60
      :dispatch dispatch-apply
    } params))
  )
)

(defn play-song
  ([song] (play-song song {}))
  ([song params]
    (go (let [
      context (or (:context params) (js/AudioContext.))
      default-params {
        :duration 60
        :context context
        :dest (.-destination context)
        :dispatch dispatch-player
      }
      player (<! (dispatch-player song (merge default-params params)))
    ] (player (.-currentTime context))))
  )
)
