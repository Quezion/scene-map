(ns scene-map.canvas-resizer
  (:require
  [scene-map.wrappers.renderer :as renderer]
  ))

(defn resize-canvas
  "A function that resizes the canvas given the three-renderer and three-camera.
  dimension-callback should return a two element list containing the width and height to set on the renderer."
  [renderer camera dimension-callback]
  (let [dimension (dimension-callback)]
    (aset camera "aspect" (first dimension) (second dimension))
    (.updateProjectionMatrix camera)
    (.setSize renderer (first dimension) (second dimension))))

(defn initialize!
  "Given the renderer, camera, and a callback that returns the width and height as a two element list,
  Adds a handler to the window resize event to resize the canvas. Returns the dimension-callback that was passed in."
  [three-renderer three-camera dimension-callback]
  (let [resize-callback #(resize-canvas three-renderer three-camera dimension-callback)]
    (.addEventListener js/window "resize" resize-callback)
    resize-callback))

(defn destroy!
  "Given the EventListener callback that was attached to the window resize event, removes it."
  [resize-listener-callback]
  (.removeEventListener js/window "resize" resize-listener-callback false))

(defn- to-dimension-callback
  "Given the size width or height as number or keyword, returns a callback that when invoked returns the width or height."
  [size-dimension]
  (if (keyword? size-dimension)
    (renderer/process-size-keyword size-dimension)
    #(constantly size-dimension)))

(defn process-autoresize!
  "Given the old size and new size, whether auto-resize? is set in the new state,
  and the resize-callback from last statemap (may be nil), compares the old and new size.
  If the auto-resize keyword has changed, adds or removes the resize-callback.
  If the size has changed, updates the dimension-callback in the resize-callback.
  Returns the resize-callback that was added to the window resize event, or nil if auto-resize was false."
  [old-size new-size old-auto-resize? new-auto-resize? old-resize-callback three-renderer three-camera]
  ;(print "old-size = " old-size ", new-size = " new-size ", old-auto-resize = " old-auto-resize? ", new-auto-resize? = " new-auto-resize? ", old-resize-callback = " old-resize-callback)
  (let [old-width           (first  old-size)
        old-height          (second old-size)
        new-width           (first  new-size)
        new-height          (second new-size)]
    (cond
      ; no change, return new-rendmap untouched
      (and (= old-width new-width) (= old-height new-height) (= old-auto-resize? new-auto-resize?))
      old-resize-callback

      ; remove old callback if it exists, then add new one if new auto-resize is true
      :default
      (do (if old-resize-callback (destroy! old-resize-callback))
          (if old-resize-callback (print "old-resize-callback destroyed"))
          (if new-auto-resize?
            (let [width-callback  (to-dimension-callback new-width)
                  height-callback (to-dimension-callback new-height)]
            (initialize! three-renderer three-camera #(list (width-callback) (height-callback))))
            nil)))))
