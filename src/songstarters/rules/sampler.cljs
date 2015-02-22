(ns songstarters.rules.sampler
  (:require
    [cljs.reader :refer [read-string]]
    [cljs.core.async :refer [chan >! <!]]
    [cljs-http.client :as http]
    [songstarters.audio :refer [load-buffer play-buffer]]
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

(def rule {:sampler {
  :allow? (fn [params]
    (<= (:duration params) (* (:min-note-duration params) 2))
  )
  :apply (fn [params]
    (go [:sampler (<! (random-sample-path))])
  )
  :player (fn [node params]
    (go (let [
      context (:context params)
      dest (:dest params)
      sample-path (first (rest node))
      buffer (<! (load-buffer context sample-path))
      player (fn [when] (play-buffer context buffer dest when))
    ] player))
  )
}})
