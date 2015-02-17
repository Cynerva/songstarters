(ns client.main 
  (:require
    [cljs.core.async :refer [<!]]
    [reagent.core :as reagent]
    [client.audio :refer [context]]
    [client.song :refer [create-random-song]]
  )
  (:require-macros [cljs.core.async.macros :refer [go]])
)

(defn my-component []
  [:div
    [:h1 "Songstarters"]
  ]
)

(reagent/render-component [my-component] (.-body js/document))

(go (let [
  dest (aget context "destination")
  song (<! (create-random-song {:duration 60}))
  _ (song (aget context "currentTime"))
]))
