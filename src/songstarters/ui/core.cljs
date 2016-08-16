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
(def generating (atom false))

(defn button [text params]
  [:button.btn.btn-primary (merge {:type "button"} params) text]
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

(defn play-button []
  (if (or @generating (nil? @song) (not (nil? @player)))
    [button "Play" {:disabled true}]
    [button "Play" {:on-click #(go
      (reset! generating true)
      (reset! player (<! (song-player @song)))
      (reset! generating false)
      (<! (start-player @player))
      (reset! player nil)
    )}]
  )
)

(defn stop-button []
  (if (nil? @player)
    [button "Stop" {:disabled true}]
    [button "Stop" {:on-click (fn []
      (stop-player @player)
      (reset! player nil)
    )}]
  )
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

(defn song-player-controls []
  [:div.well
    [:div.row
      [:div.col-xs-12
        [:p (if (nil? @song-title) "No song." @song-title)]
      ]
    ]
    [:div.row
      [:div.col-xs-12
        [play-button]
        [stop-button]
        [download-button]
      ]
    ]
  ]
)

(defn generate-button []
  (if @generating
    [button "Generate" {:disabled true}]
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
          (reset! generating false)
          (<! (start-player @player))
          (reset! player nil)
        )
      )
    }]
  )
)

(defn song-generator-controls []
  [:div.well
    "Duration: " (:duration @params)
    [slider 1 600 10 #(swap! params assoc :duration %)]
    "Max note length: " (:max-note-duration @params)
    [slider -3 4 -2 #(swap! params assoc
      :max-note-duration (Math/pow 2 %)
    )]
    "Min note length: " (:min-note-duration @params)
    [slider -4 3 -3 #(swap! params assoc
      :min-note-duration (Math/pow 2 %)
    )]
    [generate-button]
  ]
)

(defn song-display []
  (if-not (nil? @song)
    [:div.well
      (pr-str @song)
    ]
  )
)

(defn page []
  [:div.container
    [:div.row
      [:div.col-xs-6
        [song-player-controls]
      ]
      [:div.col-xs-6
        [song-generator-controls]
      ]
    ]
    [:div.row
      [:div.col-xs-12
        [song-display]
      ]
    ]
  ]
)

(reagent/render-component [page] (.-body js/document))
