(ns songstarters.test
  (:require
    [cljs.core.async :refer [<!]]
    [songstarters.song :refer [random-song play-song]]
  )
  (:require-macros [cljs.core.async.macros :refer [go]])
)

(go (let [
  duration (+ (rand 30) 30)
  song (<! (random-song {
    :duration duration
  }))
  _ (do
    (.log js/console (pr-str song))
    (play-song song)
  )
]))
