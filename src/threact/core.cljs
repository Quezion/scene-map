(ns threact.core
  (:require [cljs.spec :as spec]))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(defn x-plus-y
  [x y]
  (+ x y))

(defn x-times-y
  [x y]
  (* x y))

(defn x-divideby-y
  [x y]
  (/ x y))

(defn x-p2
  [x]
  (+ x 2))

(defn x-m2
  [x]
  (- x 2))
