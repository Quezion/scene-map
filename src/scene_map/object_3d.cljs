(ns scene-map.object-3d
  (:require-macros [swiss.arrows :refer [-<> -!<>]]) ; diamond threading macros
  (:require
    [THREE] ; WebGL rendering library
    [scene-map.jset :as jset]
    [util.coll :refer [mapply]]
    [scene-map.mesh :refer [meshmap-to-three]]
    ))

(defprotocol ^:export protocol
  "Wrapping THREE.js Object3D properties with convenience functions"
  (position [object position] "The position of the object in the scene (vec3)")
  (rotation [object rotation] "The rotation of the object in the scene (vec3)")
  (visible  [object bool]     "Whether the object should be rendered in the scene (bool)")
  (scale    [object bool]     "The scale of the object in the scene (vec3)"))

(extend-type THREE.Object3D protocol
  (position [object position] (jset/position object position))
  (rotation [object rotation] (jset/rotation object rotation))
  (visible  [object bool]     (jset/visible  object bool))
  (scale    [object scale]    (jset/scale   object scale)))

(def ^:export keyword-setters
  "Maps keywords to the appropriate jset function. Useful for THREE constructors."
  {:position #(position %1 %2)
   :rotation #(rotation %1 %2)
   :visible  #(visible  %1 %2)
   :scale    #(scale    %1 %2)})

(defn ^:private object3d-constructor
  "Constructs a default THREE Object3d and returns it."
  [] (THREE.Object3D.))

(defn ^:private object3d-applicator
  "Given a THREE Object3D and keyword/values passed as variadic args, sets the kvs on the Object3D.
  See keyword-setters for possible properties."
  [three-object3d & kvs]
  (let [props-kv    (partition 2 kvs)
        apply-props (fn [[k v] props]
                      (if-not (contains? keyword-setters k) (throw (js/Error. (str "Invalid keyword-property :" (name k) " specified in scene model."))))
                      (apply (k keyword-setters) (list three-object3d v)))]
    (dorun (cljs.core/map apply-props props-kv))
    three-object3d))


(defn ^:private modelmap-to-three
  "Given a model map (stored under a scene's :models key), instantiates a corresponding THREE Object3D."
  [{:keys [meshes] :as modelmap}]
  (let [object-3d      (object3d-constructor)
        three-meshmaps (into {} (for [[k v] meshes] [k (meshmap-to-three v)]))
        three-meshes   (vals three-meshmaps)]
    (dorun (map #(.add object-3d %) three-meshes))
    (mapply object3d-applicator object-3d (dissoc modelmap :meshes))
    object-3d))

