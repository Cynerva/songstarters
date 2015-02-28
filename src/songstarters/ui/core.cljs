(ns songstarters.ui.core
  (:require
    [cljs.core.async :refer [<!]]
    [reagent.core :as reagent :refer [atom]]
    [songstarters.song :refer [random-song play-song]]
  )
  (:require-macros [cljs.core.async.macros :refer [go]])
)

(defn generate-button [param-state song-state]
  [:button.btn.btn-primary {
    :type "button"
    :on-click #(go (let [song (<! (random-song @param-state))]
      (reset! song-state song)
      (play-song song)
    ))
  } "Generate"]
)

(defn song-display [state]
  (let [song @state]
    (if-not (nil? song)
      [:div.well
        (pr-str song)
      ]
    )
  )
)

(defn slider [min max on-change]
  (let [state (atom min)]
    (on-change min)
    (fn [min max on-change]
      [:div
        [:input {:type "range" :min min :max max :value @state
          :on-change #(let [value (-> % .-target .-value)]
            (reset! state value)
            (on-change value)
          )
        }]
      ]
    )
  )
)

(defn song-params [params]
  [:div
    "Duration: " (:duration @params)
    [slider 10 60 #(swap! params assoc :duration %)]
    "Note length: " (:max-note-duration @params)
    [slider -2 3 #(swap! params assoc
      :min-note-duration (Math/pow 2 (dec %))
      :max-note-duration (Math/pow 2 %)
    )]
  ]
)

(defn song-controls []
  (let [params (atom {}) song (atom nil)]
    [:div.row
      [:div.col-xs-6
        [:div.well
          [song-params params]
          [generate-button params song]
        ]
      ]
      [:div.col-xs-6
        [song-display song]
      ]
    ]
  )
)

(defn header []
  [:div.well
    [:div.row
      [:div.col-xs-6 [:h1 "Songstarters"]]
      [:div.col-xs-6 
        [:a {:href "https://github.com/Cynerva/songstarters"} "github"]
      ]
    ]
  ]
)

(defn page []
  [:div.container
    [header]
    [song-controls]
  ]
)

(reagent/render-component [page] (.-body js/document))
