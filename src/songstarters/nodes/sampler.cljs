(ns songstarters.nodes.sampler
  (:require
    [cljs.reader :refer [read-string]]
    [cljs.core.async :refer [chan >! <!]]
    [cljs-http.client :as http]
    [songstarters.audio :refer [load-buffer play-buffer]]
    [songstarters.nodes.node :as node]
  )
  (:require-macros [cljs.core.async.macros :refer [go]])
)

; TODO: memoize sample list
(defn random-sample-path []
  (go (->
    "sampleList"
    http/get <!
    :body
    read-string
    rand-nth
  ))
)

(defmethod node/allow? :sampler [_ params]
  (< (:duration params) 1.0)
)

(defmethod node/random :sampler [_ _]
  (go [:sampler (<! (random-sample-path))])
)

(defmethod node/player :sampler [node params]
  (go (let [
    context (:context params)
    dest (:dest params)
    sample-path (first (rest node))
    buffer (<! (load-buffer context sample-path))
    player (fn [when] (play-buffer context buffer dest when))
  ] player))
)
