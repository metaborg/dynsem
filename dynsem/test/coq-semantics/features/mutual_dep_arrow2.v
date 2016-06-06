Inductive A : Type :=
| a0:
    A
.

Inductive B : Type :=
| b0:
    B
.

Inductive x : A -> A -> Prop :=
| x_a1 :
    y b0 b0 ->
    x a0 a0
with y : B -> B -> Prop :=
| y_b1 :
    y b0 b0
.