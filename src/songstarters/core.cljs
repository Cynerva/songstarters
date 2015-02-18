(ns songstarters.core 
  (:require
    [cljs.core.async :refer [<! chan to-chan]]
    [reagent.core :as reagent]
  )
  (:require-macros [cljs.core.async.macros :refer [go]])
)

(defn my-component []
  [:div
    [:h1 "Songstarters"]
  ]
)

(reagent/render-component [my-component] (.-body js/document))
