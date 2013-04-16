(defn xsort [arg]
  (if (empty? arg)
    ; 空集合等于自身
    arg
    ; 非空集合进行拆分，递归处理
    (let
      [
        ; 取第一个元素作为分隔元素
        roll (first arg)
        ; 剩下的元素
        remain (rest arg)
        ; 与 roll 比较，拆分剩下的元素为较小集合和较大集合
        less #(< %1 roll)
        ]
      (concat
        ; 递归排序较小的元素集合
        (-> (filter less remain) (xsort))
        ; 分隔元素排在中间
        [roll]
        ; 递归排序较大的元素集合
        (-> (filter (complement less) remain) (xsort))
        )
      )
    )
  )

; 单元测试
(doseq
  [x [
    []
    [1]
    [3 1 2]
    (take 10 (repeatedly #(rand-int 100)))
    ]]
  (println (xsort x))
  (assert (= (xsort x) (sort x)))
  )
