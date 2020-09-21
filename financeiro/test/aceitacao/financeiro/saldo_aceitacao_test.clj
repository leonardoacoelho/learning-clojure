(ns financeiro.saldo-aceitacao-test
    (:require [midje.sweet :refer :all]
              [cheshire.core :as json]
              [clj-http.client :as http]
              [financeiro.db :refer :all]
              [financeiro.auxiliares :refer :all]))

(against-background [(before :facts [(limpar) (iniciar-servidor porta-padrao)]) 
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
                    (receita 2000))
        (http/post (endereco-para "/transacoes")
                   (receita 2000))
        (http/post (endereco-para "/transacoes")
                   (despesa 3000))
        (json/parse-string (conteudo "/saldo") true) => {:saldo 1000})
        
    (fact "Rejeita uma transacao sem valor" :aceitacao
        (let [resposta (http/post (endereco-para "/transacoes") (conteudo-como-json {:tipo "receita"}))]
            (:status resposta) => 422))
    
    (fact "Rejeita uma transacao com valor negativo" :aceitacao
       (let [resposta (http/post (endereco-para "/transacoes") (receita -100))]
           (:status resposta) => 422))
            
    (fact "Rejeita uma transacao com valor que nao e numero" :aceitacao
       (let [resposta (http/post (endereco-para "/transacoes") (receita "mil"))]
           (:status resposta) => 422))
    
    (fact "Rejeita uma transacao sem tipo" :aceitacao
        (let [resposta (http/post (endereco-para "/transacoes") (conteudo-como-json {:valor 70}))]
            (:status resposta) => 422))
            
    (fact "Rejeita uma transacao com tipo desconhecido" :aceitacao
        (let [resposta (http/post (endereco-para "/transacoes") (conteudo-como-json {:valor 70 :tipo "investimento"}))]
            (:status resposta) => 422))
)