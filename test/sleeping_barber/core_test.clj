(ns sleeping-barber.core-test
  (:require [clojure.test :refer :all]
            [sleeping-barber.core :refer :all]))

(deftest a-barber-shop-take-customers
  (testing "A barber-shop take customers"
    (let [_ (reset! barber-shop [])]
      (is (= 0 (count @barber-shop))))
    (let [_ (new-customer-arrives)]
      (is (= 1 (count @barber-shop))))
    (let [_ (new-customer-arrives)]
      (is (= 2 (count @barber-shop))))
    (let [_ (new-customer-arrives)]
      (is (= 3 (count @barber-shop))))))


