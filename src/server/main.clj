(ns server.main
    (:require
        [org.httpkit.server :refer :all]
        [compojure.core :refer :all]
        [compojure.route :as route]
        [compojure.handler :refer [site]]
    )
)

(defn sampleList [req]
    {
        :status 200
        :headers {"Content-Type" "text/plain"}
        :body (pr-str (map #(subs (.getPath %1) 7)
            (file-seq (clojure.java.io/file "public"))
        ))
    }
)

(defroutes myRoutes
    (GET "/sampleList" [] sampleList) 
    (route/files "/")
)

(defn -main []
    (run-server (site #'myRoutes) {:port 8000})
    (println "songstarterj started on port 8000")
)
