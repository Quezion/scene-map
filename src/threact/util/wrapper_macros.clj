(ns threact.util.wrapper-macros)

(defmacro make-getter
  "Creates a function that will get a property with the designated name from a JS object"
  [object property-name]
  '(fn [object property-name]
     (aget object property-name)))

(defmacro make-setter
  "Creates a function that takes two arguments, a JS object and a property name.
   The fn will set the named property on a JS object and return the JS object"
  [property-name]
  `(fn [object value]
     (aset object ~property-name value)
     object))

