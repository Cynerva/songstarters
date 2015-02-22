(ns songstarters.test
  (:require
    [cljs.core.async :refer [<!]]
    [songstarters.song :refer [random-song play-song]]
  )
  (:require-macros [cljs.core.async.macros :refer [go]])
)

(go (let [
  duration (+ (rand 30) 30)
  min-duration (+ (rand 0.4) 0.1)
  max-duration (+ min-duration (- (Math/pow 2 (rand 3)) 1))
  song (<! (random-song {
    :duration duration
    :min-duration min-duration
    :max-duration max-duration
  }))
  _ (do
    (.log js/console (pr-str song))
    (play-song song)
  )
]))
