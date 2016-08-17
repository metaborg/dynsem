Inductive Exp : Type :=
| Num1:
    Int -> Exp
| Plus2:
    Exp -> Exp -> Exp
.

Inductive default : Exp -> Exp -> Prop :=
| default_Num0 n8:
    default (Num1 n8) (Num1 n8)
| default_Plus0 n16 n26 e113 e212:
    default e113 (Num1 n16) ->
    default e212 (Num1 n26) ->
    default (Plus2 e113 e212) (Num1 (plusI2 n16 n26))
.