(ns using-pedestal-with-integrant.system
  (:require [integrant.core :as ig]
            [integrant.repl :refer [go halt init prep reset]]
            [io.pedestal.http :as http]
            [using-pedestal-with-integrant.pedestal :as pedestal]
            [using-pedestal-with-integrant.routes :as routes]))

(defn system
  [env]
  {:using-pedestal-with-integrant/service-map
   {:env env
    ::http/routes routes/routes
    ::http/type :jetty
    ::http/port 8890
    ::http/join? false}

   :using-pedestal-with-integrant/pedestal
   {:service-map (ig/ref :using-pedestal-with-integrant/service-map)}})

(integrant.repl/set-prep! #(system :prod))
