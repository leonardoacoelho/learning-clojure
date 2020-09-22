(ns financeiro.filtros-aceitacao-test
    (:require [midje.sweet :refer :all]
              [cheshire.core :as json]
              [financeiro.auxiliares :refer :all]
              [clj-http.client :as htpp]
              [financeiro.db :as db]))

(against-background [(before :facts [(iniciar-servidor porta-padrao) (db/limpar)])
                     (after :facts (parar-servidor))]

    (fact "Nao existem receitas" :aceitacao
        (json/parse-string (conteudo "/receitas") true)
            => {:transacoes '()})

    (fact "Nao existem despesas" :aceitacao
        (json/parse-string (conteudo "/despesas") true)
            => {:transacoes '()})

    (fact "Nao existem transacoes" :aceitacao
        (json/parse-string (conteudo "/transacoes") true)
            => {:transacoes '()})
)

(def transacoes-aleatorias
    '({:valor 7M :tipo "despesa" :rotulos ["sorvete" "entretenimento"]}
        {:valor 88M :tipo "despesa" :rotulos ["livro" "educacao"]}
        {:valor 106M :tipo "despesa" :rotulos ["curso" "educacao"]}
        {:valor 8000M :tipo "receita" :rotulos ["salario"]}))

(against-background [(before :facts [
                      (iniciar-servidor porta-padrao)
                      (db/limpar)
                      (doseq [transacao transacoes-aleatorias]
                        (db/registrar transacao))])
                     (after :facts (parar-servidor))]

    (fact "Existem 3 despesas" :aceitacao
        (count (:transacoes (json/parse-string (conteudo "/despesas") true))) => 3)

    (fact "Existem 1 receita" :aceitacao
        (count (:transacoes (json/parse-string (conteudo "/receitas") true))) => 1)

    (fact "Existem 4 transacoes" :aceitacao
        (count (:transacoes (json/parse-string (conteudo "/transacoes") true))) => 4)
        
    (fact "Existe 1 receita com o rotulo 'salario'"
        (count (:transacoes (json/parse-string (conteudo "/transacoes?rotulos=salario") true))) => 1)

    (fact "Existe 2 receita com o rotulo 'livro' ou 'curso'"
        (count (:transacoes (json/parse-string (conteudo "/transacoes?rotulos=livro&rotulos=curso") true))) => 2)

    (fact "Existe 2 receita com o rotulo 'educacao'"
        (count (:transacoes (json/parse-string (conteudo "/transacoes?rotulos=educacao") true))) => 2)
)