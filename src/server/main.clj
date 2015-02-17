(ns server.main
  (:require
    [org.httpkit.server :refer :all]
    [clojure.string :refer [split]]
    [compojure.core :refer :all]
    [compojure.route :as route]
  )
)

(defn get-sample-paths []
  (let [
    root (clojure.java.io/file "public/samples")
    root-path (.getAbsolutePath root)
    files (filter #(.isFile %1) (file-seq root))
    file-paths (for [file files] (.getAbsolutePath file))
    sound-paths (filter #(= (last (split %1 #"\.")) "wav") file-paths)
    result (for [path sound-paths]
      (str "samples/" (subs path (+ (count root-path) 1)))
    )
  ] result)
)

(defn sample-list [req] {
  :status 200
  :headers {"Content-Type" "text/plain"}
  :body (pr-str (get-sample-paths))
})

(defroutes my-routes
  (GET "/sampleList" [] sample-list)
  (route/files "/")
)

(defn -main []
  (run-server my-routes {:port 8000})
  (println "songstarterj started on port 8000")
)
