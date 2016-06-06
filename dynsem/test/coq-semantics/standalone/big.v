Inductive Expr : Type :=
| Var1:
    String -> Expr
| App2:
    Expr -> Expr -> Expr
| Fun2:
    String -> Expr -> Expr
| Plus2:
    Expr -> Expr -> Expr
| Num1:
    Int -> Expr
.

Inductive Val : Type :=
| NumV1:
    Int -> Val
| ClosV3:
    String -> Expr -> map String Val -> Val
.

Inductive big : map String Val -> Expr -> Val -> Prop :=
| big_App0 id23 e32 _lifted_92 _lifted_160 _lifted_111 l_string_val_18 _lifted_71 _lifted_82:
    big l_string_val_18 _lifted_71 (ClosV3 id23 e32 _lifted_92) ->
    big l_string_val_18 _lifted_82 _lifted_160 ->
    big (map_update _lifted_92 id23 _lifted_160) e32 _lifted_111 ->
    big l_string_val_18 (App2 _lifted_71 _lifted_82) _lifted_111
| big_Fun0 l_string_val_17 id22 e31:
    big l_string_val_17 (Fun2 id22 e31) (ClosV3 id22 e31 l_string_val_17)
| big_Num0 l_string_val_16 i28:
    big l_string_val_16 (Num1 i28) (NumV1 i28)
| big_Plus0 i17 i27 l_string_val_15 _lifted_18 _lifted_21:
    big l_string_val_15 _lifted_18 (NumV1 i17) ->
    big l_string_val_15 _lifted_21 (NumV1 i27) ->
    big l_string_val_15 (Plus2 _lifted_18 _lifted_21) (NumV1 (plusI2 i17 i27))
| big_Var0 l_string_val_10 id18:
    big l_string_val_10 (Var1 id18) (map_lookup l_string_val_10 id18)
.