(ns client.main 
    (:require 
        [reagent.core :as reagent]
        [client.audio :refer [context]]
        [client.sampler :refer [create-sampler]]
        [client.reverb :refer [create-reverb]]
    )
)

(defn my-component []
    [:div
        [:h1 "Songstarterj"]
    ]
)

(reagent/render-component [my-component] (.-body js/document))

(let [
    dest (aget context "destination")
    song (create-reverb (create-sampler))
]
    (doall (for [i (range 1 5)] (song dest i)))
)
