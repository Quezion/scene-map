(ns scene-map.wrappers.material
  (:refer-clojure :exclude [map])
  (:require-macros [swiss.arrows :refer [-<> -!<>]]) ; diamond threading macros
  (:require
    [THREE] ; WebGL rendering library
    [scene-map.jset :as jset]
    [util.coll :refer [mapply apply-kv-map]]
    ))

(defprotocol protocol
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

(def applicables
  "Maps keywords to the appropriate jset function. Useful for THREE constructors."
  {:color-rgb     #(color-rgb %1 %2)
   :color-hsl     #(color-hsl %1 %2)
   :color-hex     #(color-hex %1 %2)
   :wireframe     #(wireframe %1 %2)
   :map           #(map %1 %2)})

(def keyword-properties
  "A set of the possible keywords that can be set on a Mesh in the scene-map"
  (into #{} (keys applicables)))

(defn- basic-material-constructor
  "Constructs a default THREE Basic Material and returns it."
  [] (THREE.MeshBasicMaterial.))

; TODO: uncomment and test. since it uses a reduce instead of a dorun map, could not work
(comment (defn apply!
  "Applies one or more kvs to a material3. The kvs should be passed as variadic arguments."
  (partial apply-kv-map applicables)))

(defn apply!
  "Given a THREE Material and keyword/values as variadic args, sets the kvs on the material.
  See keyword-setters for possible properties."
  [three-material & kvs]
  (let [props-kv    (partition 2 kvs)
        apply-props (fn [[k v] props]
                      (if-not (contains? applicables k) (throw (js/Error. (str "Invalid keyword-property :" (name k) " specified in scene materials"))))
                      (apply (k applicables) (list three-material v)))]
    (dorun (cljs.core/map apply-props props-kv))
    three-material))

(defn three-sprite-material
  "Given a THREE texture object, constructs a sprite material."
  [texture]
  (-<> (js/Object.)
       (jset/map <> texture)
       (jset/transparent <> true)
       (THREE.SpriteMaterial. <>)))

(defn three-basic-material
  "Given a javascript object with material properties, returns a basic mesh material."
  [& kvs]
  (apply apply! (basic-material-constructor) kvs))

(def material-types
  "A set of material formats that may be passed to make-three-texture.
  The keys map to the corresponding Clojure functions that construct that type of material (given a material-map)"
  #{:basic
    ;:sprite
    })

; TODO: this should be a map that takes a material type and returns a function that accepts a material-map and constructs the appropriate THREE material
(def ^:private material-constructors
  "A map of material types to the appropriate constructor functions"
  {:basic three-basic-material})

(defn three-material
  "Given key values representing a material, returns a corresponding THREE material.
  The type argument is a key that must match a value in material-types."
  [type & props]
  {:pre [(contains? material-types type)]}
  (apply (type material-constructors) props))

; TODO: Should this support automatic mapping of a texture onto the material?
(defn matmap-to-three
  "Given a map containing key values of a material from the scene-map, returns a corresponding THREE material.
  If a texmap exists within the matmap, creates it into a THREE texture and maps it onto the material.
  Otherwise, a three-texture may be passed along with the material-map to have it mapped onto the THREE materiak."
  ([{:keys [type] :as material-map}]
  {:pre [(some? type)]}
  (mapply three-material type (seq (dissoc material-map :type))))
  ([material-map three-texture]
    (map (matmap-to-three material-map) three-texture)))

(defn valid-update-keywords
  "Given a map containing keyword properties representing an object3d, returns a new map containing only
  the valid keyword properties that can be set on an initialized object3d"
  [model-updates]
  (select-keys model-updates keyword-properties))
