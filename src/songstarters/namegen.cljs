(ns songstarters.namegen
  (:require
    [cljs.core.async :refer [<!]]
    [clojure.string :refer [split-lines capitalize]]
    [cljs-http.client :as http]
  )
  (:require-macros [cljs.core.async.macros :refer [go]])
)

(defn random-word-from-url [url]
  (go (-> url http/get <! :body split-lines rand-nth capitalize))
)

(defn random-adjective []
  (random-word-from-url "dictionary/adjectives/28K adjectives.txt")
)

(defn random-noun []
  (random-word-from-url "dictionary/nouns/91K nouns.txt")
)

(defn random-title []
  (go (str (<! (random-adjective)) " " (<! (random-noun))))
)
