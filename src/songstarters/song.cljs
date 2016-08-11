(ns songstarters.song
  (:require
    [cljs.core.async :refer [<! >! chan]]
    [songstarters.audio :refer [new-compressor download-buffer]]
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
  ;reverb/rule
  ;osc/rule
  ;scale/rule
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
      :max-simultaneous 4
      :dispatch dispatch-apply
      :sample-paths (<! (sampler/sample-paths))
    } params))
  ))
)

(defn max-time [song]
  ((fn dispatch [node when]
    ((:max-time ((first node) rules)) node when dispatch)
  ) song 0)
)

(def default-context (js/AudioContext.))

(defn song-player
  ([song] (song-player song {}))
  ([song params]
    (go (let [
      context (or (:context params) default-context)
      compressor (new-compressor context (.-destination context))
      default-params {
        :context context
        :dests [compressor]
        :dispatch dispatch-player
      }
      child (<! (dispatch-player song (merge default-params params)))
      player {
        :context context
        :child child
        :compressor compressor
      }
    ] player))
  )
)

(defn start-player [player]
  ((-> player :child :play) (-> player :context .-currentTime))
)

(defn stop-player [player]
  (-> player :compressor .disconnect)
  ((-> player :child :stop))
)

(defn render-song [song]
  (let [channel (chan)]
    (go (let [
      context (js/OfflineAudioContext. 2 (* (max-time song) 44100) 44100)
      player (<! (song-player song {:context context}))
    ]
      (<! (start-player player))
      (.startRendering context)
      (set! (.-oncomplete context) #(go
        (>! channel (.-renderedBuffer %))
      ))
    ))
    channel
  )
)

(defn download-song [song song-title]
  (go (download-buffer (<! (render-song song)) (str song-title ".wav")))
)
