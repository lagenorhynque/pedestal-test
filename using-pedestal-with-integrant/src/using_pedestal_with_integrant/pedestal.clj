(ns using-pedestal-with-integrant.pedestal
  (:require [integrant.core :as ig]
            [io.pedestal.http :as http]))

(defn test?
  [service-map]
  (= (:env service-map) :test))

(defmethod ig/init-key :default
  [_ x]
  x)

(defmethod ig/init-key :using-pedestal-with-integrant/pedestal
  [_ {:keys [service-map] :as pedestal}]
  (cond-> service-map
    true http/create-server
    (not (test? service-map)) http/start
    true ((partial assoc pedestal :service))))

(defmethod ig/halt-key! :using-pedestal-with-integrant/pedestal
  [_ {:keys [service] :as pedestal}]
  (http/stop service)
  (assoc pedestal :service nil))
