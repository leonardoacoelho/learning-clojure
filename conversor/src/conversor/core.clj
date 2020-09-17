(ns conversor.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clj-http.client :as http-client])
  (:gen-class))

;; configurar possiveis argumentos para "leitura"
(def opcoes-do-programa 
  [["-d" "--de moeda base" "moeda base para conversao" :default "eur"]
  ["-p" "--para moeda destino" "moeda a qual queremos saber o valor"]])

;; definir ApiKey para requisições de conversão
(def chave "24ef924471e2c799b82e")

;; definir a url da Api de conversão
(def api-url "https://free.currconv.com/api/v7/convert")

;; criar função que concatena as opções para conversao
(defn parametrizar-moedas [de para]
  (str de "_" para))

(defn -main
  [& args]
  ;; obter os argumentos inseridos
  (let [{:keys [de para]} (:options (parse-opts args opcoes-do-programa))] 
    ;; obter a cotação e exibir
    (prn "Cotacao: " (http-client/get api-url 
      {:query-params { "q" (parametrizar-moedas de para)
                       "apiKey" chave}}))))
