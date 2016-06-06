Inductive A : Type :=
| a0:
    A
.

Inductive B : Type :=
| b0:
    B
.

Inductive C : Type :=
| c0:
    C
.

Inductive default : B -> C -> Prop :=
| default_a0 :
    default a0 c0
| default_b0 :
    default b0 c0
.