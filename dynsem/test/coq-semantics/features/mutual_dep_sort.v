Inductive A : Type :=
| a1:
    B -> A
with B : Type :=
| b1:
    A -> B
.