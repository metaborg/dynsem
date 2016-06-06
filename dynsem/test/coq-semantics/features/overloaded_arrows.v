Inductive A : Type :=
| a0:
    A
.

Inductive B : Type :=
| b0:
    B
.

Inductive AorB : Type :=
| ob1:
    B -> AorB
| oa1:
    A -> AorB
.

Inductive default : A -> A -> Prop :=
| default_a0 :
    default a0 a0
| default_a1 a10 b17:
    default a0 a10 ->
    default a10 b17 ->
    default a0 b17
| default_b0 :
    default b0 b0
.