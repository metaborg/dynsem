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
| big_App0 id e r_1 v_1 v_2 r_2 expr_1 expr_2:
    big r_2 expr_1 (ClosV3 id e r_1) ->
    big r_2 expr_2 v_1 ->
    big (map_update r_1 id v_1) e v_2 ->
    big r_2 (App2 expr_1 expr_2) v_2
| big_Fun0 r_1 id e:
    big r_1 (Fun2 id e) (ClosV3 id e r_1)
| big_Num0 r_1 i:
    big r_1 (Num1 i) (NumV1 i)
| big_Plus0 i1 i2 r_1 expr_1 expr_2:
    big r_1 expr_1 (NumV1 i1) ->
    big r_1 expr_2 (NumV1 i2) ->
    big r_1 (Plus2 expr_1 expr_2) (NumV1 (plusI2 i1 i2))
| big_Var0 r_1 id:
    big r_1 (Var1 id) (map_lookup r_1 id)
.