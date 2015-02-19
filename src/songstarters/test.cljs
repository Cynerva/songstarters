(ns songstarters.test
  (:require
    [cljs.core.async :refer [<!]]
    [songstarters.nodes.node :as node]
  )
  (:require-macros [cljs.core.async.macros :refer [go]])
)

(go (let [
  context (js/AudioContext.)
  dest (.-destination context)
  params {:context context :dest dest :duration 10}
  node (<! (node/random :any params))
  _ (.log js/console (pr-str node))
  player (<! (node/player node params))
  _ (.log js/console (pr-str player))
  _ (player (.-currentTime context))
]))
