Require Import scopes.

Require Import frames.

Inductive Typ : Type :=
| Tarrow:
    Typ -> Typ -> Typ
| Tint:
    Typ
.

Inductive PreExp : Type :=
| App:
    Exp -> Exp -> PreExp
| Fun:
    D -> Typ -> Exp -> PreExp
| Binop:
    Exp -> Exp -> PreExp
| Var:
    R -> PreExp
| Num:
    Int -> PreExp
with Exp : Type :=
| E:
    ScopeId -> Typ -> PreExp -> Exp
.

Inductive Val : Type :=
| ClosV:
    D -> Exp -> FrameId -> Val
| NumV:
    Int -> Val
| TimeoutV:
    Val
.

Inductive eval : nat -> FrameId -> Exp -> H -> Val -> H -> Prop :=
| eval_e10 f1 e1 h1:
    eval O f1 e1 h1 TimeoutV h1
| eval_E0 d e1 f2 h2 s2 t2 p1 v h3 s3 f3 i f1 s1 t1 e1 e2 h1:
    eval i f1 e1 h1 (ClosV d e1 f2) h2 ->
    e1 = E s2 t2 p1 ->
    eval i f1 e2 h2 v h3 ->
    v <> TimeoutV ->
    scopeofFrame (h3, f2) s3 ->
    initFrame (s2, cons (P, cons (s3, f2) nil) nil, cons (d, v) nil) f3 ->
    eval (S i) f1 (E s1 t1 (App e1 e2)) h1 v' h4
| eval_E1 d e1 f2 h2 s2 t2 p1 h3 i f1 s1 t1 e1 e2 h1:
    eval i f1 e1 h1 (ClosV d e1 f2) h2 ->
    e1 = E s2 t2 p1 ->
    eval i f1 e2 h2 TimeoutV h3 ->
    eval (S i) f1 (E s1 t1 (App e1 e2)) h1 TimeoutV h4
| eval_E2 h2 i f1 s1 t1 e1 e2 h1:
    eval i f1 e1 h1 TimeoutV h2 ->
    eval (S i) f1 (E s1 t1 (App e1 e2)) h1 TimeoutV h4
| eval_E3 i f1 s1 t1 d t2 e h1:
    eval (S i) f1 (E s1 t1 (Fun d t2 e)) h1 (ClosV d e f1) h1
| eval_E4 z1 h2 z2 h3 i f1 s1 t1 e1 e2 h1:
    eval i f1 e1 h1 (NumV z1) h2 ->
    eval i f1 e2 h2 (NumV z2) h3 ->
    eval (S i) f1 (E s1 t1 (Binop e1 e2)) h1 (NumV (plus z1 z2)) h3
| eval_E5 z1 h2 h3 i f1 s1 t1 e1 e2 h1:
    eval i f1 e1 h1 (NumV z1) h2 ->
    eval i f1 e2 h2 TimeoutV h3 ->
    eval (S i) f1 (E s1 t1 (Binop e1 e2)) h1 TimeoutV h3
| eval_E6 h2 i f1 s1 t1 e1 e2 h1:
    eval i f1 e1 h1 TimeoutV h2 ->
    eval (S i) f1 (E s1 t1 (Binop e1 e2)) h1 TimeoutV h3
| eval_E7 p f2 d i f1 s1 t1 r h1:
    pathofRef r p ->
    lookup (f1, h1, p) (Addr f2 d) ->
    eval (S i) f1 (E s1 t1 (Var r)) h1 v h1
| eval_E8 i f1 s1 t1 i h1:
    eval (S i) f1 (E s1 t1 (Num i)) h1 (NumV i) h1
.