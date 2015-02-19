(ns songstarters.nodes.node)

; return type: boolean
(defmulti allow? (fn [node-type params] node-type))

; return type: <! node
(defmulti random (fn [node-type params] node-type))

; return type: <! fn [when] (...)
(defmulti player (fn [node params] (first node)))
