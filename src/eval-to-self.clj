(let [x
         (fn [] 
           (let [
                 prefix "(fn [] (let"
                 suffix "(str prefix ['prefix prefix 'suffix suffix] suffix)))"
                 ]
             (str prefix ['prefix prefix 'suffix suffix] suffix)
             ))
         ]
     (println (x))
     )