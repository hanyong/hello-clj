; 一趟冒泡, 最大元素移动到最后
(declare bubble-up-do)
(defn bubble-up [coll] (do
  (println (list 'bubble-up coll)) ; debug
  ; 打散参数, 使能够按元素个数重载
  (apply bubble-up-do coll)
  ))
(defn bubble-up-do
  ([x] [x])
  ([x y & more]
    (->>
      ; 交换表头
      (if (< x y)
        (concat [x y] more)
        (concat [y x] more)
        )
      ; 分离表头, 递归对表尾进行冒泡
      ((fn [[x & more]]
         (concat [x] (bubble-up more))
         ))
      )
    )
  )

; 冒泡排序
(declare bubble-sort-do)
(defn bubble-sort [coll]
  ; 打散参数, 按元素个数重载
  (apply bubble-sort-do coll)
  )
(defn bubble-sort-do
  ([] [])
  ([x] [x])
  ([x & more] (->
    (concat [x] more)
    (bubble-up)
    ; 分离最后一个元素，递归排序前面的元素
    (#(concat
      (bubble-sort (drop-last %))
      (last %)
      ))
    ))
  )

; 单元测试
(let [xsort bubble-sort] (doseq
  [x [
    []
    [1]
    [2 1]
    [3 1 2]
    (take 10 (repeatedly #(rand-int 100)))
    ]]
  (println (xsort x))
  (assert (= (xsort x) (sort x)))
  ))
