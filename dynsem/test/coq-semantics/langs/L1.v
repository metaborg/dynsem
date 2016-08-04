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
| Exp:
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
| eval_Exp0 d e f2 h2 v h3 s2 f3 i f1 s1 t1 e1 e2 h1:
    eval i f1 e1 h1 (ClosV d e f2) h2 ->
    eval i f1 e2 h2 v h3 ->
    v <> TimeoutV ->
    scopeofFrame (h3, f2) s2 ->
    initFrame (s1, cons (P, cons (s2, f2) nil) nil, cons (d, v) nil) f3 ->
    eval (S i) f1 (Exp s1 t1 (App e1 e2)) h1 v' h4
| eval_Exp1 d e f2 h2 h3 i f1 s1 t1 e1 e2 h1:
    eval i f1 e1 h1 (ClosV d e f2) h2 ->
    eval i f1 e2 h2 TimeoutV h3 ->
    eval (S i) f1 (Exp s1 t1 (App e1 e2)) h1 TimeoutV h4
| eval_Exp2 h2 i f1 s1 t1 e1 e2 h1:
    eval i f1 e1 h1 TimeoutV h2 ->
    eval (S i) f1 (Exp s1 t1 (App e1 e2)) h1 TimeoutV h4
| eval_Exp3 i f1 s1 t1 d t2 e h1:
    eval (S i) f1 (Exp s1 t1 (Fun d t2 e)) h1 (ClosV d e f1) h1
| eval_Exp4 z1 h2 z2 h3 i f1 s1 t1 e1 e2 h1:
    eval i f1 e1 h1 (NumV z1) h2 ->
    eval i f1 e2 h2 (NumV z2) h3 ->
    eval (S i) f1 (Exp s1 t1 (Binop e1 e2)) h1 (NumV (plus z1 z2)) h3
| eval_Exp5 z1 h2 h3 i f1 s1 t1 e1 e2 h1:
    eval i f1 e1 h1 (NumV z1) h2 ->
    eval i f1 e2 h2 TimeoutV h3 ->
    eval (S i) f1 (Exp s1 t1 (Binop e1 e2)) h1 TimeoutV h3
| eval_Exp6 h2 i f1 s1 t1 e1 e2 h1:
    eval i f1 e1 h1 TimeoutV h2 ->
    eval (S i) f1 (Exp s1 t1 (Binop e1 e2)) h1 TimeoutV h3
| eval_Exp7 p f2 d i f1 s1 t1 r h1:
    pathofRef r p ->
    lookup (f1, h1, p) (Addr f2 d) ->
    eval (S i) f1 (Exp s1 t1 (Var r)) h1 v h1
| eval_Exp8 i f1 s1 t1 i h1:
    eval (S i) f1 (Exp s1 t1 (Num i)) h1 (NumV i) h1
.