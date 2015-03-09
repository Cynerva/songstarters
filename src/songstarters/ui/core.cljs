(ns songstarters.ui.core
  (:require
    [cljs.core.async :refer [<!]]
    [reagent.core :as reagent :refer [atom]]
    [songstarters.song :refer [random-song song-player start-player stop-player download-song]]
    [songstarters.namegen :refer [random-title]]
  )
  (:require-macros [cljs.core.async.macros :refer [go]])
)

(def song-title (atom nil))
(def song (atom nil))
(def player (atom nil))
(def params (atom {}))

(defn button [text params]
  [:button.btn.btn-primary (merge {:type "button"} params) text]
)

(defn generate-button []
  (let [generating (atom false)] (fn []
    (if @generating
      [button "Generating..." {:disabled true}]
      [button "Generate" {
        :on-click #(go
          (reset! generating true)
          (if-not (nil? @player) (stop-player @player))
          (let [
            new-song (<! (random-song @params))
            new-title (<! (random-title))
          ]
            (reset! song new-song)
            (reset! song-title new-title)
            (reset! player (<! (song-player new-song)))
            (start-player @player)
            (reset! generating false)
          )
        )
      }]
    )
  ))
)

(defn download-button []
  (let [downloading (atom false)] (fn []
    (cond
      (nil? @song) [button "Download" {:disabled true}]
      @downloading [button "Rendering..." {:disabled true}]
      :else [button "Download" {:on-click #(go
        (reset! downloading true)
        (<! (download-song @song @song-title))
        (reset! downloading false)
      )}]
    )
  ))
)

(defn slider [min max initial on-change]
  (on-change initial)
  (fn [min max initial on-change]
    [:div
      [:input {:type "range" :min min :max max :default-value initial
        :on-change #(on-change (-> % .-target .-value))
      }]
    ]
  )
)

(defn song-controls []
  [:div.well
    "Duration: " (:duration @params)
    [slider 1 120 10 #(swap! params assoc :duration %)]
    "Note length: " (:max-note-duration @params)
    [slider -3 3 -2 #(swap! params assoc
      :min-note-duration (Math/pow 2 (dec %))
      :max-note-duration (Math/pow 2 %)
    )]
    [generate-button]
    [download-button]
  ]
)

(defn song-display []
  [:div.well
    [:p @song-title]
    (if (nil? @song)
      "No song."
      (pr-str @song)
    )
  ]
)

(defn page []
  [:div.container
    [:div.row
      [:div.col-xs-6
        [song-controls]
      ]
      [:div.col-xs-6
        [song-display]
      ]
    ]
  ]
)

(reagent/render-component [page] (.-body js/document))
