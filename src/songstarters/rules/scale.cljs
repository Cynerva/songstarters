(ns songstarters.rules.scale
  (:require
    [cljs.core.async :refer [chan >! <!]]
  )
  (:require-macros [cljs.core.async.macros :refer [go]])
)

(defn note->freq [d]
  (* (Math/pow 2 (/ (- d 69) 12)) 440)
)

(def scales [
  [0 2 4 5 7 9 11] ; c major
  [0 2 4 5 7 9 10] ; c mixolydian
  [0 2 3 5 7 9 10] ; c minor (dorian)
  [0 2 3 5 7 8 10] ; c minor (aeolian)
])

(defn random-scale []
  (let [
    key (rand-int 12)
    scale (map (partial + key) (rand-nth scales))
    subscale (into [(first scale)] (filter #(= (rand-int 2) 0) (rest scale)))
    full-scale (vec (flatten (for [i (range 9)]
      (map (partial + (* i 12)) subscale)
    )))
  ] full-scale)
)

(def rule {:scale {
  :allow? (fn [params]
    (not (contains? params :scale))
  )
  :apply (fn [params]
    ((:dispatch params)
      (assoc params :scale (random-scale))
    )
  )
}})
