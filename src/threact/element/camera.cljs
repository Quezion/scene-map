(ns threact.element.camera
  (:require [cljs.spec :as s]
            [threact.element.common :as c]))

(s/def ::position ::c/vec3)
(s/def ::rotation ::c/vec3)
(s/def ::fov number?)

(s/def ::camera (s/keys :req-un [::position ::rotation ::fov]))



