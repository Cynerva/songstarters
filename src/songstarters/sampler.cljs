(ns songstarters.sampler
  (:require
    [cljs.reader :refer [read-string]]
    [cljs.core.async :refer [chan >! <!]]
    [cljs-http.client :as http]
    [songstarters.audio :refer [load-buffer play-buffer]]
  )
  (:require-macros [cljs.core.async.macros :refer [go]])
)

; TODO: memoize sample list
(defn get-random-sample-path []
  (go (->
    "sampleList"
    http/get <!
    :body
    read-string
    rand-nth
  ))
)

(defn create-sampler [context dest sample-path]
  (go (let [buffer (<! (load-buffer context sample-path))]
    (fn [when] (play-buffer context buffer dest when))
  ))
)

(go (let [
  context (js/AudioContext.)
  dest (.-destination context)
  sample-path (<! (get-random-sample-path))
  sampler (<! (create-sampler context dest sample-path))
  _ (do
    (sampler (.-currentTime context))
  )
]))
