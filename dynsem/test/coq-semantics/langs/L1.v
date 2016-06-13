Require Import scopes.

Require Import frames.

Inductive Typ : Type :=
| Tarrow2:
    Typ -> Typ -> Typ
| Tint0:
    Typ
.

Inductive PreExp : Type :=
| App2:
    Exp -> Exp -> PreExp
| Fun3:
    D -> Typ -> Exp -> PreExp
| Binop2:
    Exp -> Exp -> PreExp
| Var1:
    R -> PreExp
| Num1:
    Int -> PreExp
with Exp : Type :=
| E_3:
    ScopeId -> Typ -> PreExp -> Exp
with ScopeId : Type :=.

Inductive lookup__Arrow : Type :=
| lookup3:
    FrameId -> H -> Path -> lookup__Arrow
with FrameId : Type :=with H : Type :=with Path : Type :=
| D1:
    D -> Path
| E3:
    Label -> ScopeId -> Path -> Path
with Label : Type :=
| I0:
    Label
| P0:
    Label
.

Inductive get__Arrow : Type :=
| get3:
    FrameId -> H -> D -> get__Arrow
.

Inductive pathofRef__Arrow : Type :=
| pathofRef1:
    R -> pathofRef__Arrow
.

Inductive slookup__Arrow : Type :=
| slookup2:
    Path -> ScopeId -> slookup__Arrow
.

Inductive scopeofFrame__Arrow : Type :=
| scopeofFrame2:
    H -> FrameId -> scopeofFrame__Arrow
.

Inductive initFrame__Arrow : Type :=
| initFrame3:
    ScopeId -> map Label (map ScopeId FrameId) -> map D Val -> initFrame__Arrow
with Val : Type :=
| ClosV3:
    D -> Exp -> FrameId -> Val
| NumV1:
    Int -> Val
.

Inductive Addr : Type :=
| Addr2:
    FrameId -> D -> Addr
.

Inductive SD : Type :=
| SD2:
    ScopeId -> D -> SD
.

Inductive default_lookup__Arrow : lookup__Arrow -> Addr -> Prop :=.

Inductive default_get__Arrow : get__Arrow -> Val -> Prop :=.

Inductive default_pathofRef__Arrow : pathofRef__Arrow -> Path -> Prop :=.

Inductive default_slookup__Arrow : slookup__Arrow -> SD -> Prop :=.

Inductive default_initFrame__Arrow : initFrame__Arrow -> FrameId -> Prop :=.

Inductive eval : FrameId -> Exp -> H -> Val -> H -> Prop :=
| eval_E_0 d e f_1 h_1 v h_2 s_1 f_2 v' h_3 f_3 s_2 typ_1 e1 e2 h_4:
    eval f_3 e1 h_4 (ClosV3 d e f_1) h_1 ->
    eval f_3 e2 h_1 v h_2 ->
    scopeofFrame (scopeofFrame2 h_2 f_1) s_1 ->
    default_initFrame__Arrow (initFrame3 s_2 (cons (P0, cons (s_1, f_1) nil) nil) (cons (d, v) nil)) f_2 ->
    eval f_2 e h_2 v' h_3 ->
    eval f_3 (E_3 s_2 typ_1 (App2 e1 e2)) h_4 v' h_3
| eval_E_1 f_1 s_1 typ_1 d typ_2 e h_1:
    eval f_1 (E_3 s_1 typ_1 (Fun3 d typ_2 e)) h_1 (ClosV3 d e f_1) h_1
| eval_E_2 z1 h_1 z2 h_2 f_1 s_1 typ_1 e1 e2 h_3:
    eval f_1 e1 h_3 (NumV1 z1) h_1 ->
    eval f_1 e2 h_1 (NumV1 z2) h_2 ->
    eval f_1 (E_3 s_1 typ_1 (Binop2 e1 e2)) h_3 (NumV1 (plusI2 z1 z2)) h_2
| eval_E_3 p f_1 d v f_2 s_1 typ_1 r h_1:
    default_pathofRef__Arrow (pathofRef1 r) p ->
    default_lookup__Arrow (lookup3 f_2 h_1 p) (Addr2 f_1 d) ->
    default_get__Arrow (get3 f_1 h_1 d) v ->
    eval f_2 (E_3 s_1 typ_1 (Var1 r)) h_1 v h_1
| eval_E_4 f_1 s_1 typ_1 i h_1:
    eval f_1 (E_3 s_1 typ_1 (Num1 i)) h_1 (NumV1 i) h_1
.