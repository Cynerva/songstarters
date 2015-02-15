(ns main 
    [:require [reagent.core :as reagent]]
)

(defn my-component []
    [:div
        [:p "Hello world!"]
    ]
)

(reagent/render-component [my-component] (.-body js/document))
