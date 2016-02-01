(defproject questy/scene_map "0.0.1"
            :clojurescript? true
            :description "ClojureScript WebGL Rendering"
            :url "https://github.com/questy/scene_map"
            :license {:name "Eclipse Public License"
                      :url "http://www.eclipse.org/legal/epl-v10.html"}
            :dependencies [[org.clojure/clojure "1.7.0" :scope "provided"]
                           [org.clojure/clojurescript "1.7.48" :scope "provided"]

                           ; Precise threading macros
                           [swiss-arrows "1.0.0"]

                           ; CLJS DOM Manipulation Library
                           [prismatic/dommy "1.1.0"]

                           ; diff functions for clj data structures
                           [differ "0.3.0"]
                           ]

            :plugins [[lein-cljsbuild "1.0.6"]
                      [michaelblume/lein-marginalia "0.9.0"]]

            :hooks [leiningen.cljsbuild]

            :profiles {:dev {:dependencies [[com.cemerick/clojurescript.test "0.3.2"]]
                             :plugins [[com.cemerick/clojurescript.test "0.3.2"]]
                             :cljsbuild
                                           {:builds
                                                           {:test {:source-paths ["src" "test"]
                                                                   :incremental? true
                                                                   :compiler {:output-to "target/unit-test.js"
                                                                              :optimizations :whitespace
                                                                              :pretty-print true}}}
                                            :test-commands {"unit" ["phantomjs" :runner
                                                                    "window.literal_js_was_evaluated=true"
                                                                    "target/unit-test.js"]}}}}

            :lein-release {:deploy-via :shell
                           :shell ["lein" "deploy" "clojars"]}

            :cljsbuild {:builds {}})