(ns rpn-calc.core-test
  (:require [clojure.test :refer :all]
            [rpn-calc.core :refer :all]))

(deftest test-tokenize
  (is (nil?                 (tokenize "")))
  (is (nil?                 (tokenize "   ")))
  (is (= '(5)               (tokenize "5")))
  (is (= '(+)               (tokenize "+")))
  (is (= '(2 3 +)           (tokenize "2 3 +")))
  (is (= '(4 5 + 4.5 3 *)   (tokenize "4 5 + 4.5 3 *")))

  ;; The '% is always parsed by the Clojure reader, so we need create
  ;; a special symbol for checking the results:
  (let [m (symbol "%")]
    (is (= (list 4 5 '+ 4.5 3 '* m) (tokenize "4 5 + 4.5 3 * %")))))

(deftest test-tuple-a-stack
  (is (= '(5)   (tuple-a-stack + [2 3])))
  (is (= '(4 3) (tuple-a-stack + [2 2 3])))
  (is (= '(5.5) (tuple-a-stack + [2.5 3])))
  (is (= '(2)   (tuple-a-stack + '(2))))
  (is (= '()    (tuple-a-stack + '()))))

(deftest test-dup-stack
  (is (= '(2 2)   (dup-stack '(2))))
  (is (= '(2 2 4) (dup-stack '(2 4))))
  (is (= '()      (dup-stack '()))))

(deftest test-process
  (let [m (symbol "%")]
    (is (= '(2)     (process [1 1] '+)))
    (is (= '(0)     (process [1 1] '-)))
    (is (= '(1)     (process [1 1] '*)))
    (is (= '(2)     (process [4 2] '/)))
    (is (= '(1)     (process [4 3]  m)))
    (is (= '(2 2)   (process [2]   'd)))
    (is (= '(2 2 2) (process [2 2] 'd)))
    (is (= '(2)     (process [1 2] 'p)))))

(deftest test-rpn
  (is  (= 5  (rpn "2 3 +")))
  (is  (= 2  (rpn "2")))
  (is  (= 8  (rpn "2 d d * *")))
  (is  (= '(4 2)  (rpn "2 2 2 +")))
  (is  (= '(4 2)  (rpn "2 + 2 2 +")))
  (is  (= -2      (rpn "2 2 2 - -")))
  (is (nil?  (rpn "")))
  (is (nil?  (rpn "   ")))
  (is (and (nil? (rpn "d"))
           (= (first @error-list) "expected an expression")
           (= (second @error-list) "Stack empty. Can't duplicate top entry.")))
  (is (and (nil? (rpn "p"))
           (= (first @error-list) "expected an expression")
           (= (second @error-list) "Stack empty. Can't print/remove top entry."))))

(deftest test-questions
  (is (= 13 (rpn "6 7 +")))
  (is (= -16 (rpn "4 -2 * 2 *")))
  (is (and (= 14 (rpn "2 3 4 + d p *"))
           (empty? @error-list)))

  (is (and (= '(10 2) (rpn "2 8 2 +"))
           (= (first @error-list) "ended with a stack of numbers")))

  (is (and (= 2 (rpn "2 +"))
           (= (first @error-list) "insufficient input for '+' operator")))

  (is (and (= '(2 2) (rpn "2 2 r"))
           (= (first @error-list) "ended with a stack of numbers")
           (= (second @error-list) "Couldn't parse 'r' as a number or operator")))

  (is (and (nil? (rpn "2.5"))
           (= (first @error-list) "expected an expression")
           (= (second @error-list) "Couldn't parse '2.5' as a integer or operator"))))
