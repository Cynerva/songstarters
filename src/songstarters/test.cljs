(ns songstarters.test
  (:require
    [cljs.core.async :refer [<!]]
    [songstarters.song :refer [random-song play-song]]
  )
  (:require-macros [cljs.core.async.macros :refer [go]])
)

(go (let [
  song (<! (random-song))
  _ (do
    (.log js/console (pr-str song))
    (play-song song)
  )
]))
