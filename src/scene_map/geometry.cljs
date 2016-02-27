(ns scene-map.geometry
  (:require-macros [swiss.arrows :refer [-<> -!<>]]) ; diamond threading macro, non-updating diamond macro
  (:require
    [THREE]  ; WebGL rendering library
    [scene-map.collections :as collections]
    [util.coll :refer [into-js-array]]
    ))

(defn three-geometry-old
  "Given a set of vertices and indices, creates a matching THREE.js geometry"
  [vertices faces]
  (let [geometry    (THREE.Geometry.)
        push-vertex (fn [a b c] (.vertices.push geometry (THREE.Vector3. a b c)))
        push-face  (fn [a b c] (.faces.push geometry (THREE.Face3. a b c)))]
    (dorun (map #(apply push-vertex (take 3 %)) vertices))
    (dorun (map #(apply push-face (take 3 %)) faces))
    (.computeFaceNormals geometry)
    geometry))

(defn seq-to-three-face3
  "Given a seq that returns a seq of three elements per take,
  returns a JavaScript array of corresponding THREE vector3s"
  [seq]
  (into-js-array (map #(THREE.Face3. (nth % 0) (nth % 1) (nth % 2)) seq)))

(defn seq-to-three-vector3
  "Given a seq that returns a seq of three elements per take,
  returns a JavaScript array of corresponding THREE vector3s"
  [seq]
  (into-js-array (map #(THREE.Vector3. (nth % 0) (nth % 1) (nth % 2)) seq)))

(defn seq-to-three-vector2
  "Given a seq that returns a seq of two elements per take
  returns a JavaScript array of corresponding THREE vector3s"
  [seq]
  (into-js-array (map #(THREE.Vector2. (first %) (second %)) seq)))

(defn- vertmap-to-seq
  "Given a map representing the vertices, handles the data type and returns a seq that returns
  a three element collection per take"
  [{:keys [type data] :as vertmap}]
  {:pre [(some? type) (some? data)]}
  ; Note that type-handlers returns a partition function that works on that collection type
  ((type collections/type-handlers) data 3))

(defn- facemap-to-seq
  "Given a map representing the face indices, handles the data type and returns a seq that returns
  a three element collection per take"
  [{:keys [type data] :as vertmap}]
  {:pre [(some? type) (some? data)]}
  ; Note that type-handlers returns a partition function that works on that collection type
  ((type collections/type-handlers) data 3))

(defn three-geometry
  "Constructs and returns a THREE geometry. If a JS array containing Vector3 objects is supplied for the
  vertices and faces, they will be set on the Geometry."
  ([] (THREE.Geometry))
  ([vertices-js-array faces-js-array]
   (-<> (THREE.Geometry.)
        (-!<> (aset <> "vertices" vertices-js-array))
        (-!<> (aset <> "faces"   faces-js-array)))))

(defn- geomap-to-three
  "Given a geometry-map, creates a matching THREE geometry object"
  [{:keys [vertices faces] :as geomap}]
  ; Handle keys :vertices, :indices
  (let [verts-seq      (vertmap-to-seq vertices)
        faces-seq      (facemap-to-seq faces)
        three-geometry (three-geometry (seq-to-three-vector3 verts-seq) (seq-to-three-face3 faces-seq))
        ]
    (.computeFaceNormals three-geometry)
    (.computeVertexNormals three-geometry)
    three-geometry
    ; TODO: handle UV coordinates (not yet supported)
    ))


