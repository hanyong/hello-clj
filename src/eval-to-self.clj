;; http://www.4clojure.com/problem/125

(let
  [x
   '(fn []
      (let
        [
         prefix "(fn [] (let "
         suffix "))"
         body '(str prefix ['prefix prefix 'suffix suffix 'body (list 'quote body)] " " body suffix)
         ]
        ; (eval body) ; CompilerException java.lang.RuntimeException: Unable to resolve symbol: prefix in this context, WHY?
        (str prefix ['prefix prefix 'suffix suffix 'body (list 'quote body)] " " body suffix)
        ))
   ;x (read-string (str x))
   ]
  (println (str x))
  (println ((eval x)))
  (= (str x) ((eval x)))
  )
