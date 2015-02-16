(ns client.main 
    (:require
        [cljs.core.async :refer [<!]]
        [reagent.core :as reagent]
        [client.audio :refer [context]]
        [client.sampler :refer [create-random-sampler]]
        [client.reverb :refer [create-reverb]]
    )
    (:require-macros [cljs.core.async.macros :refer [go]])
)

(defn my-component []
    [:div
        [:h1 "Songstarterj"]
    ]
)

(reagent/render-component [my-component] (.-body js/document))

(go (let [
    dest (aget context "destination")
    song (<! (create-random-sampler))
]
    (song dest 0)
    (song dest 1)
    (song dest 2)
    (song dest 3)
))
