(ns using-pedestal-with-integrant.system-test
  (:require [clojure.test :as t]
            [integrant.core :as ig]
            [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.test :refer [response-for]]
            [using-pedestal-with-integrant.pedestal :as pedestal]
            [using-pedestal-with-integrant.routes :as routes]
            [using-pedestal-with-integrant.system :as system]))

(def url-for (route/url-for-routes
              (route/expand-routes routes/routes)))

(defn service-fn
  [system]
  (get-in system [:using-pedestal-with-integrant/pedestal :service ::http/service-fn]))

(defmacro with-system
  [[bound-var binding-expr] & body]
  `(let [~bound-var (ig/init ~binding-expr)]
     (try
       ~@body
       (finally
         (ig/halt! ~bound-var)))))

(t/deftest greeting-test
  (with-system [sut (system/system :test)]
    (let [service (service-fn sut)
          {:keys [status body]} (response-for service
                                              :get
                                              (url-for :greet))]
      (t/is (= 200 status))
      (t/is (= "Hello, world!" body)))))
