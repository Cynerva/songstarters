(ns songstarters.test
  (:require
    [cljs.core.async :refer [<!]]
    [songstarters.song :refer [random-song play-song]]
  )
  (:require-macros [cljs.core.async.macros :refer [go]])
)

(go (let [
  song (<! (random-song {
    :duration (+ (rand 30) 30)
    :min-note-duration 0.125
    :max-note-duration (Math/pow 2 (- (rand-int 4) 2))
  }))
  _ (do
    (.log js/console (pr-str song))
    (play-song song)
  )
]))
