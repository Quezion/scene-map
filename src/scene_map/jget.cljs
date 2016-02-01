;; Contains utilily getter functions that're commonly used on the JavaScript THREE.js objects
;; Complementary to jset

(ns scene-map.jget
  (:require-macros [swiss.arrows :refer [-<> -!<>]]) ; diamond threading macro
  )

(defn rotation [object]
  (let [rot (.-rotation object)]
    (list (.-x rot) (.-y rot) (.-z rot))))