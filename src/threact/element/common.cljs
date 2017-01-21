 (ns threact.element.common
   (:require [cljs.spec :as s]))

(s/def ::vec2 (s/coll-of number? :count 2))
(s/def ::vec3 (s/coll-of number? :count 3))

(s/def ::dom-node true)

(s/def ::html-canvas #(= (.-className %) "HTMLCanvasElement"))
