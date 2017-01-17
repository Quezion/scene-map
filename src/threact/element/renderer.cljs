(ns threact.element.renderer
  (:require [cljs.spec :as s]
            [threact.element.common :as c]))

(s/def ::size ::c/vec2)
(s/def ::clear-color number?)
(s/def ::auto-resize? boolean?)

(s/def ::renderer (s/keys :req-un [::size]
                          :opt [::clear-color ::auto-resize?]))
