(ns using-pedestal-with-component.system-test
  (:require [clojure.test :as t]
            [com.stuartsierra.component :as component]
            [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.test :refer [response-for]]
            [using-pedestal-with-component.pedestal :as pedestal]
            [using-pedestal-with-component.routes :as routes]
            [using-pedestal-with-component.system :as system]))

(def url-for (route/url-for-routes
              (route/expand-routes routes/routes)))

(defn service-fn
  [system]
  (get-in system [:pedestal :service ::http/service-fn]))

(defmacro with-system
  [[bound-var binding-expr] & body]
  `(let [~bound-var (component/start ~binding-expr)]
     (try
       ~@body
       (finally
         (component/stop ~bound-var)))))

(t/deftest greeting-test
  (with-system [sut (system/system :test)]
    (let [service (service-fn sut)
          {:keys [status body]} (response-for service
                                              :get
                                              (url-for :greet))]
      (t/is (= 200 status))
      (t/is (= "Hello, world!" body)))))
