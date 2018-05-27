(ns what-is-an-interceptor.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.interceptor.chain :as chain]
            [io.pedestal.interceptor.error :as err]))

(def say-hello
  {:name ::say-hello
   :enter (fn [context]
            (assoc context
                   :response
                   {:body "Hello, world!"
                    :status 200}))})

(def odds
  {:name ::odds
   :enter (fn [context]
            (assoc context
                   :response
                   {:body "I handle odd numbers\n"
                    :status 200}))})

(def evens
  {:name ::evens
   :enter (fn [context]
            (assoc context
                   :response
                   {:body "Even numbers are my bag\n"
                    :status 200}))})

(def chooser
  {:name ::chooser
   :enter (fn [context]
            (try
              (let [param (get-in context [:request :query-params :n])
                    n (Integer/parseInt param)
                    nxt (if (even? n) evens odds)]
                (chain/enqueue context [nxt]))
              (catch NumberFormatException e
                (assoc context
                       :response
                       {:body "Not a number!\n"
                        :status 400}))))})

(def chooser2
  {:name ::chooser2
   :enter (fn [context]
            (let [n (-> context :request :query-params :n Integer/parseInt)
                  nxt (if (even? n) evens odds)]
              (chain/enqueue context [nxt])))})

(def number-format-handler
  {:name ::number-format-handler
   :error (fn [context exc]
            (if (= (-> exc ex-data :exception-type)
                   :java.lang.NumberFormatException)
              (assoc context :response {:body "Not a number!\n" :status 400})
              (assoc context :io.pedestal.interceptor.chain/errro exc)))})

(def errors
  (err/error-dispatch
   [ctx ex]
   [{:exception-type :java.lang.NumberFormatException}]
   (assoc ctx :response {:body "Not a number!\n" :status 400})))

(def routes
  #{["/hello" :get say-hello]
    ["/data-science" :get chooser]
    ["/data-science2" :get [number-format-handler chooser2]]
    ["/data-science3" :get [errors chooser2] :route-name ::chooser3]})

(defn start
  []
  (-> {::http/port 8822
       ::http/join? false
       ::http/type :jetty
       ::http/routes routes}
      http/create-server
      http/start))
