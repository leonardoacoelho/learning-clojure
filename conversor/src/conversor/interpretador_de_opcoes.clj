(ns conversor.interpretador-de-opcoes
    (:require [clojure.tools.cli :refer [parse-opts]]))

;; configurar possiveis argumentos para "leitura"
(def opcoes-do-programa 
    [["-d" "--de moeda base" "moeda base para conversao" :default "eur"]
    ["-p" "--para moeda destino" "moeda a qual queremos saber o valor"]])

;; função que interpreta os argumentos passados
(defn interpretar-opcoes [argumentos]
    (:options (parse-opts argumentos opcoes-do-programa)))