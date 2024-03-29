
(ns asset-compressor.js-compressor
    (:require [asset-compressor.engine :as engine]
              [fruits.noop.api         :refer [none]]
              [fruits.string.api       :as string]
              [syntax-reader.api       :as syntax-reader]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn compress-js
  ; @description
  ; Returns the given 'n' JS string compressed.
  ;
  ; @param (string) n
  ;
  ; @usage
  ; (compress-js "function my-function ( my-param )\n  {\n    ...\n  }")
  ; =>
  ; "function my-function(my-param){...}"
  ;
  ; @return (string)
  [n]
  ; 0.)
  ; Single-line comments:
  ; - E.g., "function my-function () {} \\ My comment \n"
  ; - Commented parts can be removed.
  ; - Interpreter must be disabled while processing (to prevent misreading syntax).
  ;
  ; 1.)
  ; Multi-line comments:
  ; - E.g., "function my-function () {} /* My comment */"
  ; - Commented parts can be removed.
  ; - Interpreter must be disabled while processing (to prevent misreading syntax).
  ;
  ; 2.)
  ; Double quoted strings:
  ; - E.g., "function my-function () { "My string" }"
  ; - Surronding whitespaces can be removed.
  ; - Interpreter must be disabled while processing (quoted parts are not compressed).
  ;
  ; 3.)
  ; Single quoted strings:
  ; - E.g., "function my-function () { 'My string' }"
  ; - Surronding whitespaces can be removed.
  ; - Interpreter must be disabled while processing (quoted parts are not compressed).
  ;
  ; 4-9.)
  ; Opening / closing parenthesis, brace and bracket characters:
  ; - Surronding whitespaces can be removed.
  ;
  ; 10.)
  ; Semicolon:
  ; - Surronding whitespaces can be removed.
  ;
  ; 11-24.)
  ; Operators:
  ; - Surronding whitespaces can be removed.
  ;
  ; 25.)
  ; Multiple whitespaces:
  ; - Can be replaced with a single whitespace.
  (-> n string/trim
      (syntax-reader/update-tags [[:t0  #"\s*\/\/" #"\n\s*"   {:accepted-children [] :update-f none}]
                                  [:t1  #"\s*\/\*" #"\*\/\s*" {:accepted-children [] :update-f none}]
                                  [:t2  #"\s*\""   #"\"\s*"   {:accepted-children [] :update-f string/trim}]
                                  [:t3  #"\s*\'"   #"\'\s*"   {:accepted-children [] :update-f string/trim}]
                                  [:t4  #"[\s]*\([\s]*"                             {:update-f (fn [_] "(")}]
                                  [:t5  #"[\s]*\)[\s]*"                             {:update-f (fn [_] ")")}]
                                  [:t6  #"[\s]*\{[\s]*"                             {:update-f (fn [_] "{")}]
                                  [:t7  #"[\s]*\}[\s]*"                             {:update-f (fn [_] "}")}]
                                  [:t8  #"[\s]*\[[\s]*"                             {:update-f (fn [_] "[")}]
                                  [:t9  #"[\s]*\][\s]*"                             {:update-f (fn [_] "]")}]
                                  [:t10 #"[\s]*\;[\s]*"                             {:update-f (fn [_] ";")}]
                                  [:t11 #"[\s]*\=[\s]*"                             {:update-f (fn [_] "=")}]
                                  [:t12 #"[\s]*\+[\s]*"                             {:update-f (fn [_] "+")}]
                                  [:t13 #"[\s]*\-[\s]*"                             {:update-f (fn [_] "-")}]
                                  [:t14 #"[\s]*\/[\s]*"                             {:update-f (fn [_] "/")}]
                                  [:t15 #"[\s]*\%[\s]*"                             {:update-f (fn [_] "%")}]
                                  [:t16 #"[\s]*\*[\s]*"                             {:update-f (fn [_] "*")}]
                                  [:t17 #"[\s]*\>[\s]*"                             {:update-f (fn [_] ">")}]
                                  [:t18 #"[\s]*\<[\s]*"                             {:update-f (fn [_] "<")}]
                                  [:t19 #"[\s]*\&[\s]*"                             {:update-f (fn [_] "&")}]
                                  [:t20 #"[\s]*\^[\s]*"                             {:update-f (fn [_] "^")}]
                                  [:t21 #"[\s]*\|[\s]*"                             {:update-f (fn [_] "|")}]
                                  [:t22 #"[\s]*\?[\s]*"                             {:update-f (fn [_] "?")}]
                                  [:t23 #"[\s]*\![\s]*"                             {:update-f (fn [_] "!")}]
                                  [:t24 #"[\s]*\~[\s]*"                             {:update-f (fn [_] "~")}]
                                  [:t25 #"\s{2,}"                                   {:update-f (fn [_] " ")}]])))

(defn compress-js-files!
  ; @description
  ; - Compresses JS files (that match the given filename pattern) within the given source paths.
  ; - Returns the compressed output.
  ;
  ; @param (map) options
  ; {:compressor-f (function)(opt)
  ;   Default: compress-js
  ;  :filename-pattern (regex pattern)(opt)
  ;   Default: #".*\.js"
  ;  :output-path (string)
  ;  :source-paths (strings in vector)}
  ;
  ; @usage
  ; (compress-js-files {:output-path "my-script.min.css"
  ;                     :source-paths ["my-directory"]})
  [{:keys [compressor-f filename-pattern output-path source-paths] :or {compressor-f compress-js filename-pattern #".*\.js"}}]
  (engine/compress-assets! {:compressor-f compressor-f :filename-pattern filename-pattern :output-path output-path :source-paths source-paths}))
