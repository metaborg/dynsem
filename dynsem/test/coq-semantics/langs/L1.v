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
| eval_exp11 f1 exp1 h1:
    eval O f1 exp1 h1 TimeoutV h1
| eval_Exp9 d e f2 h2 v h3 s2 f3 i f1 s1 typ1 e1 e2 h1:
    eval i f1 e1 h1 (ClosV d e f2) h2 ->
    eval i f1 e2 h2 v h3 ->
    v <> TimeoutV ->
    scopeofFrame (h3, f2) s2 ->
    initFrame (s1, cons (P, cons (s2, f2) nil) nil, cons (d, v) nil) f3 ->
    eval (S i) f1 (Exp s1 typ1 (App e1 e2)) h1 v' h4
| eval_Exp10 d e f2 h2 h3 i f1 s1 typ1 e1 e2 h1:
    eval i f1 e1 h1 (ClosV d e f2) h2 ->
    eval i f1 e2 h2 TimeoutV h3 ->
    eval (S i) f1 (Exp s1 typ1 (App e1 e2)) h1 TimeoutV h4
| eval_Exp11 h2 i f1 s1 typ1 e1 e2 h1:
    eval i f1 e1 h1 TimeoutV h2 ->
    eval (S i) f1 (Exp s1 typ1 (App e1 e2)) h1 TimeoutV h4
| eval_Exp12 i f1 s1 typ1 d typ2 e h1:
    eval (S i) f1 (Exp s1 typ1 (Fun d typ2 e)) h1 (ClosV d e f1) h1
| eval_Exp13 z1 h2 z2 h3 i f1 s1 typ1 e1 e2 h1:
    eval i f1 e1 h1 (NumV z1) h2 ->
    eval i f1 e2 h2 (NumV z2) h3 ->
    eval (S i) f1 (Exp s1 typ1 (Binop e1 e2)) h1 (NumV (plusI z1 z2)) h3
| eval_Exp14 z1 h2 h3 i f1 s1 typ1 e1 e2 h1:
    eval i f1 e1 h1 (NumV z1) h2 ->
    eval i f1 e2 h2 TimeoutV h3 ->
    eval (S i) f1 (Exp s1 typ1 (Binop e1 e2)) h1 TimeoutV h3
| eval_Exp15 h2 i f1 s1 typ1 e1 e2 h1:
    eval i f1 e1 h1 TimeoutV h2 ->
    eval (S i) f1 (Exp s1 typ1 (Binop e1 e2)) h1 TimeoutV h3
| eval_Exp16 p f2 d i f1 s1 typ1 r h1:
    pathofRef r p ->
    lookup (f1, h1, p) (Addr f2 d) ->
    eval (S i) f1 (Exp s1 typ1 (Var r)) h1 v h1
| eval_Exp17 i f1 s1 typ1 i h1:
    eval (S i) f1 (Exp s1 typ1 (Num i)) h1 (NumV i) h1
.