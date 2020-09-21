(ns financeiro.handler-test
  (:require [midje.sweet :refer :all]
            [ring.mock.request :as mock]
            [cheshire.core :as json]
            [financeiro.handler :refer :all]
            [financeiro.db :as db]))

(facts "Da um 'Ola, mundo!' na rota raiz"
  (let [response (app (mock/request :get "/"))]
    (fact "o status da resposta e 200"
        (:status response) => 200)
    (fact "o texto do corpo e 'Ola, mundo!'"
        (:body response) => "Ola, mundo!")))

  (against-background [(before :facts (db/limpar))]
    (fact "o formato e 'application/json"
      (let [response (app (mock/request :get "/saldo"))]
        (get-in response [:headers "Content-Type"]) => "application/json; charset=utf-8"))

      (fact "o status da resposta e 200"
        (let [response (app (mock/request :get "/saldo"))]
          (:status response) => 200))

      (fact "o texto do corpo e um JSON cuja chave e saldo e o valor e 0"
        (let [response (app (mock/request :get "/saldo"))]
          (:body response) => "{\"saldo\":0}")))

(facts "Rota invalida nao existe"
  (let [response (app (mock/request :get "/invalid"))]
    (fact "o codigo de erro é 404"
        (:status response) => 404)

    (fact "o codigo de erro é 404"
        (:body response) => "Recurso nao encontrado")))

(facts "Registra uma receita no valor de 10"
  (against-background (db/registrar {:valor 10 :tipo "receita"}) => {:id 1 :valor 10 :tipo "receita"})
    (let [response (app (-> (mock/request :post "/transacoes")
                           (mock/json-body {:valor 10 :tipo "receita"})))]
      (fact "o status da resposta e 201"
        (:status response) => 201)
      
      (fact "o texto do corpo e um JSON com o conteudo enviado e um id"
        (:body response) => "{\"id\":1,\"valor\":10,\"tipo\":\"receita\"}")))