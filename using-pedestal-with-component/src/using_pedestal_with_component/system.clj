(ns using-pedestal-with-component.system
  (:require [com.stuartsierra.component :as component]
            [io.pedestal.http :as http]
            [reloaded.repl :refer [go init reset start stop]]
            [using-pedestal-with-component.pedestal :as pedestal]
            [using-pedestal-with-component.routes :as routes]))

(defn system
  [env]
  (component/system-map
   :service-map
   {:env env
    ::http/routes routes/routes
    ::http/type :jetty
    ::http/port 8890
    ::http/join? false}

   :pedestal
   (component/using
    (pedestal/new-pedestal)
    [:service-map])))

(reloaded.repl/set-init! #(system :prod))
