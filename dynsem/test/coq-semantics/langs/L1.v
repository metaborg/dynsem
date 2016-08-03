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
| Timeout:
    Val
| ClosV:
    D -> Exp -> FrameId -> Val
| NumV:
    Int -> Val
with FrameId : Type :=.

Inductive lookup__Arrow : Type :=
| lookup:
    FrameId -> H -> Path -> lookup__Arrow
with H : Type :=with Path : Type :=
| D:
    D -> Path
| E:
    Label -> ScopeId -> Path -> Path
with Label : Type :=
| I:
    Label
| P:
    Label
.

Inductive get__Arrow : Type :=
| get:
    FrameId -> H -> D -> get__Arrow
.

Inductive pathofRef__Arrow : Type :=
| pathofRef:
    R -> pathofRef__Arrow
.

Inductive slookup__Arrow : Type :=
| slookup:
    Path -> ScopeId -> slookup__Arrow
.

Inductive scopeofFrame__Arrow : Type :=
| scopeofFrame:
    H -> FrameId -> scopeofFrame__Arrow
.

Inductive initFrame__Arrow : Type :=
| initFrame:
    ScopeId -> map Label (map ScopeId FrameId) -> map D Val -> initFrame__Arrow
.

Inductive Addr : Type :=
| Addr:
    FrameId -> D -> Addr
.

Inductive SD : Type :=
| SD:
    ScopeId -> D -> SD
.

Inductive eval : nat -> FrameId -> Exp -> H -> Val -> H -> Prop :=
| eval_exp10 f1 exp1 h1:
    eval O f1 exp1 h1 TimeoutV h1
| eval_Exp0 d e f2 h2 v h3 s2 f3 i f1 s1 typ1 e1 e2 h1:
    eval i f1 e1 h1 (ClosV d e f2) h2 ->
    eval i f1 e2 h2 v h3 ->
    v <> TimeoutV ->
    scopeofFrame (scopeofFrame h3 f2) s2 ->
    initFrame (initFrame s1 (cons (P, cons (s2, f2) nil) nil) (cons (d, v) nil)) f3 ->
    eval (S i) f1 (Exp s1 typ1 (App e1 e2)) h1 v' h4
| eval_Exp1 d e f2 h2 h3 i f1 s1 typ1 e1 e2 h1:
    eval i f1 e1 h1 (ClosV d e f2) h2 ->
    eval i f1 e2 h2 TimeoutV h3 ->
    eval (S i) f1 (Exp s1 typ1 (App e1 e2)) h1 TimeoutV h4
| eval_Exp2 h2 i f1 s1 typ1 e1 e2 h1:
    eval i f1 e1 h1 TimeoutV h2 ->
    eval (S i) f1 (Exp s1 typ1 (App e1 e2)) h1 TimeoutV h4
| eval_Exp3 i f1 s1 typ1 d typ2 e h1:
    eval (S i) f1 (Exp s1 typ1 (Fun d typ2 e)) h1 (ClosV d e f1) h1
| eval_Exp4 z1 h2 z2 h3 i f1 s1 typ1 e1 e2 h1:
    eval i f1 e1 h1 (NumV z1) h2 ->
    eval i f1 e2 h2 (NumV z2) h3 ->
    eval (S i) f1 (Exp s1 typ1 (Binop e1 e2)) h1 (NumV (plusI z1 z2)) h3
| eval_Exp5 z1 h2 h3 i f1 s1 typ1 e1 e2 h1:
    eval i f1 e1 h1 (NumV z1) h2 ->
    eval i f1 e2 h2 TimeoutV h3 ->
    eval (S i) f1 (Exp s1 typ1 (Binop e1 e2)) h1 TimeoutV h3
| eval_Exp6 h2 i f1 s1 typ1 e1 e2 h1:
    eval i f1 e1 h1 TimeoutV h2 ->
    eval (S i) f1 (Exp s1 typ1 (Binop e1 e2)) h1 TimeoutV h3
| eval_Exp7 p f2 d i f1 s1 typ1 r h1:
    pathofRef (pathofRef r) p ->
    lookup (lookup f1 h1 p) (Addr f2 d) ->
    eval (S i) f1 (Exp s1 typ1 (Var r)) h1 v h1
| eval_Exp8 i f1 s1 typ1 i h1:
    eval (S i) f1 (Exp s1 typ1 (Num i)) h1 (NumV i) h1
.