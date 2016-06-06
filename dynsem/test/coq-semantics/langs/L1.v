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
    Decl -> Typ -> Exp -> PreExp
| Binop2:
    Exp -> Exp -> PreExp
| Var1:
    Ref -> PreExp
| Num1:
    Int -> PreExp
with Exp : Type :=
| E_3:
    ScopeId -> Typ -> PreExp -> Exp
with ScopeId : Type :=with Decl : Type :=with Ref : Type :=.

Inductive FrameId : Type :=.

Inductive H : Type :=.

Inductive Label : Type :=
| I0:
    Label
| P0:
    Label
.

Inductive Path : Type :=
| D1:
    Decl -> Path
| E3:
    Label -> ScopeId -> Path -> Path
.

Inductive Addr : Type :=
| Addr2:
    FrameId -> Decl -> Addr
.

Inductive lookup : Type :=
| lookup3:
    FrameId -> H -> Path -> lookup
.

Inductive get : Type :=
| get3:
    FrameId -> H -> Decl -> get
.

Inductive pathofRef : Type :=
| pathofRef1:
    Ref -> pathofRef
.

Inductive slookup : Type :=
| slookup2:
    Path -> ScopeId -> slookup
.

Inductive scopeofExp : Type :=
| scopeofExp1:
    Exp -> scopeofExp
.

Inductive scopeofFrame : Type :=
| scopeofFrame2:
    H -> FrameId -> scopeofFrame
.

Inductive initFrame : Type :=
| initFrame3:
    ScopeId -> map Label (map ScopeId FrameId) -> map Decl Val -> initFrame
with Val : Type :=
| ClosV3:
    Decl -> Exp -> FrameId -> Val
| NumV1:
    Int -> Val
.

Inductive SD : Type :=
| SD2:
    ScopeId -> Decl -> SD
.

Inductive pathofRef' : pathofRef -> Path -> Prop :=.

Inductive slookup' : slookup -> SD -> Prop :=.

Inductive lookup' : lookup -> Addr -> Prop :=.

Inductive get' : get -> Val -> Prop :=.

Inductive scopeofFrame' : scopeofFrame -> ScopeId -> Prop :=.

Inductive initFrame' : initFrame -> FrameId -> Prop :=.

Inductive eval : FrameId -> Exp -> H -> Val -> H -> Prop :=
| eval_E_0 d26 e40 _lifted_321 h_22 v19 h_32 _lifted_291 _lifted_311 v'7 h_40 frameid_17 _lifted_251 _lifted_92 e120 e218 h_18:
    eval frameid_17 e120 h_18 (ClosV3 d26 e40 _lifted_321) h_22 ->
    eval frameid_17 e218 h_22 v19 h_32 ->
    scopeofFrame' (scopeofFrame2 h_32 _lifted_321) _lifted_291 ->
    initFrame' (initFrame3 _lifted_251 (cons (P0, cons (_lifted_291, _lifted_321) nil) nil) (cons (d26, v19) nil)) _lifted_311 ->
    eval _lifted_311 e40 h_32 v'7 h_40 ->
    eval frameid_17 (E_3 _lifted_251 _lifted_92 (App2 e120 e218)) h_18 v'7 h_40
| eval_E_1 frameid_15 _lifted_61 _lifted_71 d25 _lifted_82 e39 h_16:
    eval frameid_15 (E_3 _lifted_61 _lifted_71 (Fun3 d25 _lifted_82 e39)) h_16 (ClosV3 d25 e39 frameid_15) h_16
| eval_E_2 z18 h_20 z28 h_30 frameid_14 _lifted_42 _lifted_51 e119 e217 h_12:
    eval frameid_14 e119 h_12 (NumV1 z18) h_20 ->
    eval frameid_14 e217 h_20 (NumV1 z28) h_30 ->
    eval frameid_14 (E_3 _lifted_42 _lifted_51 (Binop2 e119 e217)) h_12 (NumV1 (plusI2 z18 z28)) h_30
| eval_E_3 p8 _lifted_171 d24 v18 frameid_11 _lifted_212 _lifted_38 r12 h_11:
    pathofRef' (pathofRef1 r12) p8 ->
    lookup' (lookup3 frameid_11 h_11 p8) (Addr2 _lifted_171 d24) ->
    get' (get3 _lifted_171 h_11 d24) v18 ->
    eval frameid_11 (E_3 _lifted_212 _lifted_38 (Var1 r12)) h_11 v18 h_11
| eval_E_4 frameid_10 _lifted_01 _lifted_112 i14 h_10:
    eval frameid_10 (E_3 _lifted_01 _lifted_112 (Num1 i14)) h_10 (NumV1 i14) h_10
.