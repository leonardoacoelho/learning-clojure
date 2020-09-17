(ns conversor.formatador)

;; função que formata a cotação retornada pela Api
(defn formatar [cotacao de para]
    (str "1 " de " equivale a " cotacao " em " para))