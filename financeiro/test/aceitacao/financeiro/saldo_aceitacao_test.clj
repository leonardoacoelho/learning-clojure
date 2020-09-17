(ns financeiro.saldo-aceitacao-test
    (:require [midje.sweet :refer :all]
              [cheshire.core :as json]
              [clj-http.client :as http]
              [financeiro.db :refer :all]
              [financeiro.auxiliares :refer :all]))

(against-background [(before :facts [(iniciar-servidor porta-padrao) (limpar)])
                     (after :facts (parar-servidor))]
    
    (fact "O saldo inicial e 0"  :aceitacao
        (json/parse-string (conteudo "/saldo") true) => {:saldo 0})
    
    (fact "O saldo e 10 quando a unica transacao e uma receita de 10" :aceitacao

        (http/post (endereco-para "/transacoes")
            {:content-type :json
             :body (json/generate-string {:valor 10 :tipo "receita"})}) 
            
        (json/parse-string (conteudo "/saldo") true) => {:saldo 10})

    (fact "O saldo e 1000 quando criamos duas receitas de 2000 e uma despesa de 3000" :aceitacao
        (http/post (endereco-para "/transacoes")
                    {:content-type :json
                    :body (json/generate-string {:valor 2000 :tipo "receita"})})
        (http/post (endereco-para "/transacoes")
                        {:content-type :json
                        :body (json/generate-string {:valor 2000 :tipo "receita"})})
        (http/post (endereco-para "/transacoes")
                            {:content-type :json
                            :body (json/generate-string {:valor 3000 :tipo "despesa"})})
        (json/parse-string (conteudo "/saldo") true) => {:saldo 1000}))
        