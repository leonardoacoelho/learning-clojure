(ns conversor.cambista
    (:require [clj-http.client :as http-client]
              [cheshire.core :refer [parse-string]]))

;; definir Api Key para requisições de conversão
(def chave (System/getenv "CHAVE_API"))

;; definir a url da Api de conversão
(def api-url "https://free.currconv.com/api/v7/convert")

;; função que concatena as opções para conversao
(defn parametrizar-moedas [de para]
  (str de "_" para))

;; função que obtem a cotação chamando a Api
(defn obter-cotacao [de para]
    (let [moedas (parametrizar-moedas de para)]
        (-> (:body (http-client/get api-url
                {:query-params { "q" moedas
                                "apiKey" chave}}))
        (parse-string) (get-in ["results" moedas "val"]))))