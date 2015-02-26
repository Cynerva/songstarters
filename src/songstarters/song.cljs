(ns songstarters.song
  (:require
    [cljs.core.async :refer [<!]]
    [songstarters.audio :refer [new-compressor]]
    [songstarters.rules.sampler :as sampler]
    [songstarters.rules.looper :as looper]
    [songstarters.rules.splitter :as splitter]
    [songstarters.rules.reverb :as reverb]
    [songstarters.rules.osc :as osc]
    [songstarters.rules.scale :as scale]
    [songstarters.rules.mixer :as mixer]
  )
  (:require-macros [cljs.core.async.macros :refer [go]])
)

(def rules (merge
  sampler/rule
  mixer/rule
  looper/rule
  splitter/rule
  reverb/rule
  osc/rule
  scale/rule
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
  ([params] (go
    (dispatch-apply (merge {
      :duration 60
      :min-note-duration 0.1
      :max-note-duration 0.2
      :dispatch dispatch-apply
      :sample-paths (<! (sampler/sample-paths))
    } params))
  ))
)

(defn play-song
  ([song] (play-song song {}))
  ([song params]
    (go (let [
      context (or (:context params) (js/AudioContext.))
      dest (new-compressor context (.-destination context))
      default-params {
        :context context
        :dests [dest]
        :dispatch dispatch-player
      }
      player (<! (dispatch-player song (merge default-params params)))
    ] (player (.-currentTime context))))
  )
)
