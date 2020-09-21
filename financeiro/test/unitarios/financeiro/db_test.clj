(ns financeiro.db-test
    (:require [midje.sweet :refer :all]
              [financeiro.db :refer :all]))

(facts "Guarda uma transacao num atomo"
    (against-background [(before :facts (limpar))]
        (fact "a colecao de transacoes inicia vazia"
            (count (transacoes)) => 0)
        
            (fact "a transacao e o primeiro registro"
                (registrar {:valor 7 :tipo "receita"}) => {:id 1 :valor 7 :tipo "receita"}
                (count (transacoes)) => 1)))

(facts "Calcula o saldo dada uma colecao de transacoes"
    (against-background [(before :facts (limpar))]
        (fact "saldo e positivo quando so tem receita"
            (registrar {:valor 1 :tipo "receita"})
            (registrar {:valor 10 :tipo "receita"})
            (registrar {:valor 100 :tipo "receita"})
            (registrar {:valor 1000 :tipo "receita"})
            
            (saldo) => 1111)
            
        (fact "saldo e negativo quando so tem despesas"
            (registrar {:valor 2 :tipo "despesa"})
            (registrar {:valor 20 :tipo "despesa"})
            (registrar {:valor 200 :tipo "despesa"})
            (registrar {:valor 2000 :tipo "despesa"})
            
            (saldo) => -2222)
            
        (fact "saldo e a soma das receitas menos a soma das despesas"
            (registrar {:valor 2 :tipo "despesa"})
            (registrar {:valor 10 :tipo "receita"})
            (registrar {:valor 200 :tipo "despesa"})
            (registrar {:valor 1000 :tipo "receita"})
            (saldo) => 808)))