;; Contains utilily setter functions that're commonly used on the JavaScript THREE.js objects
;; By default, all setters return the object they act on
;; Complementary to jget
(ns scene-map.jset
  (:require-macros [swiss.arrows :refer [-<> -!<>]]) ; diamond threading macro
  (:refer-clojure :exclude [map])
  )

; NOTE the difference between .set here and aset
; THREE.js supports the set() method on some of objects and it triggers an update of the scene

(defn position
  "Given x/y/z sets the position of a THREE.js object"
  ([object x y z]         (.set (.-position object) x y z) object)
  ([object position-xyz]  (apply position object position-xyz)))

(defn rotation
  "Given x/y/z sets the rotation of a THREE.js object"
  ([object x y z]         (.set (.-rotation object) x y z) object)
  ([object rotation-xyz]  (apply rotation object rotation-xyz)))

(defn scale
  "Given x/y/z sets the scale of a THREE.js object"
  ([object x y z]     (.set (.-scale object) x y z) object)
  ([object scale-xyz] (apply scale object scale-xyz)))


(defn size
  "Given width and height sets the size of a THREE.js object"
  ([object w h]     (.setSize object w h) object)
  ([object size-wh] (apply size object size-wh) object))

(defn color-rgb
  "Given r/g/b sets it on a THREE.js object under the color property"
  ([object r g b] (.setRGB (.-color object) r g b) object)
  ([object rgb]   (apply color-rgb object rgb) object))

(defn color-hsl
  "Given h/s/l sets it on a THREE.js object under the color property"
  ([object h s l] (.setHSL (.-color object) h s l) object)
  ([object hsl]   (apply color-hsl object hsl) object))

(defn color-hex
  "Given hex sets it on a THREE.js object under the color property"
  ([object hex] (.setHex (.-color object) hex) object))

(defn transparent [object bool]
  (aset object "transparent" bool) object)

(defn clear-color [object clear-color]
  "Sets the clear-color of a THREE.js renderer"
  (.setClearColor object clear-color) object)

(defn map [object mapped]
  (aset object "map" mapped) object)

(defn wireframe
  "Given a boolean sets a THREE.js object to be wireframe"
  [object bool]
  (aset object "wireframe" bool) object)

(defn needs-update [object bool]
  (aset object "needsUpdate" bool) object)

(defn visible [object bool]
  (aset object "visible" bool) object)

(def keyword-setters
  "Maps keywords to the appropriate jset function. Useful for THREE constructors."
  {:position      position
   :rotation      rotation
   :scale         scale
   :size          size
   :color-rgb     color-rgb
   :color-hsl     color-rgb
   :color-hex     color-hex
   :transparent   transparent
   :clear-color   clear-color
   :map           map
   :wireframe     wireframe
   :needs-update  needs-update
   :visible       visible
   })

