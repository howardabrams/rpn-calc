(defproject rpn-calc "0.1.0-SNAPSHOT"
  :description "A streaming calculator that computes in reverse polish notation."
  :url "https://github.com/howardabrams/rpn-calc"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]]
  :main ^:skip-aot rpn-calc.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
