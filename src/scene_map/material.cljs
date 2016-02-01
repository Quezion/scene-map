(ns scene-map.material
  (:refer-clojure :exclude [map])
  (:require-macros [swiss.arrows :refer [-<> -!<>]]) ; diamond threading macros
  (:require
    [THREE] ; WebGL rendering library
    [scene-map.jset :as jset]
    [util.coll :refer [mapply]]
    ))

(defprotocol ^:export protocol
  "Wrapping THREE.js material properties with convenience functions"
  (wireframe [object bool] "Whether the material should render as a wireframe")
  (color-hex [object hex] "The color that the material should render as in hex")
  (color-rgb [object rgb] [object r g b] "The color that the material should render as in RGB")
  (color-hsl [object hsl] [object h s l] "The color that the material should render as in HSL")
  (map [object mapped] "The texture mapping of the material"))

(extend-type THREE.MeshBasicMaterial protocol
  (wireframe [object bool]   (jset/wireframe object bool))
  (color-hex [object hex]    (jset/color-hex object hex))
  (color-rgb ([object rgb]   (jset/color-rgb object rgb))
                 ([object r g b] (jset/color-rgb object r g b)))
  (color-hsl ([object hsl]   (jset/color-hsl object hsl))
                 ([object h s l] (jset/color-hsl object h s l)))
  (map        [object mapped] (jset/map object mapped)))

(def ^:export keyword-setters
  "Maps keywords to the appropriate jset function. Useful for THREE constructors."
  {:color-rgb     #(color-rgb %1 %2)
   :color-hsl     #(color-hsl %1 %2)
   :color-hex     #(color-hex %1 %2)
   :wireframe     #(wireframe %1 %2)
   :map           #(map %1 %2)})

(defn ^:private three-sprite-material
  "Given a THREE texture object, constructs a sprite material."
  [texture]
  (-<> (js/Object.)
       (jset/map <> texture)
       (jset/transparent <> true)
       (THREE.SpriteMaterial. <>)))

(defn ^:private three-basic-material
  "Given a javascript object with material properties, returns a basic mesh material."
  [& rest]
  (let [material    (THREE.MeshBasicMaterial.)
        props-kv    (partition 2 rest)
        apply-props (fn [[k v] props]
                      (if-not (contains? keyword-setters k) (throw (js/Error. (str "Invalid keyword-property :" (name k) " specified in scene materials"))))
                      (apply (k keyword-setters) (list material v)))]
    (dorun (cljs.core/map apply-props props-kv))
    material))

;
(defn ^:private three-wireframe-material
  "Returns a basic material set to render as a red wireframe."
  [] (three-basic-material :color-rgb [(rand-int 2) (rand-int 2) (rand-int 2)] :wireframe true))

(def ^:export material-types
  "A set of material formats that may be passed to make-three-texture.
  The keys map to the corresponding Clojure functions that construct that type of material (given a material-map)"
  #{:basic
    :wireframe})

; TODO: this should be a map that takes a material type and returns a function that accepts a material-map and constructs the appropriate THREE material
(def ^:private material-constructors
  "A map of material types to the appropriate constructor functions"
  {:basic three-basic-material})

(defn ^:private three-material
  "Given key values representing a material, returns a corresponding THREE material.
  The type argument is a key that must match a value in material-types."
  [type & props]
  {:pre [(contains? material-types type)]}
  (apply (type material-constructors) props))

; TODO: Should this support automatic mapping of a texture onto the material?
(defn ^:private matmap-to-three
  "Given a map containing key values of a material from the scene-map, returns a corresponding THREE material.
  If a texmap exists within the matmap, creates it into a THREE texture and maps it onto the material.
  Otherwise, a three-texture may be passed along with the material-map to have it mapped onto the THREE materiak."
  ([{:keys [type] :as material-map}]
  {:pre [(some? type)]}
    ; TODO: fix the below flatten. It's flattening the colls that're arguments to basic-material function eg :rgb (255 0 0)
  (mapply three-material type (seq (dissoc material-map :type))))
  ([material-map three-texture]
    (map (matmap-to-three material-map) three-texture)))