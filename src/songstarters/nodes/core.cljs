(ns songstarters.nodes.core
  (:require
    [songstarters.nodes.node :as node]
    [songstarters.nodes.sampler]
    [songstarters.nodes.looper]
    [songstarters.nodes.splitter]
    [songstarters.nodes.reverb]
  )
)

(def node-types [
  :sampler
  :looper
  :splitter
  :reverb
])

(defmethod node/random :any [_ params]
  (let [
    node-types (filter #(node/allow? % params) node-types)
    node-type (rand-nth node-types)
  ] (node/random node-type params))
)
