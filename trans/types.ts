module types

imports
  lib/types/-
  lib/relations/-
  
relations

  define transitive <:

type rules

  Var(x): ty
  where definition of x : ty

  VarRef(x) : ty
  where definition of x : ty
  
  Con(c, t*) : ty
  where definition of c : (ty*, ty)
    and t* : ty_t*
    and (ty_t* == ty* or ty_t* <: ty*) 
    else error "types of sub-terms do not match constructor definition" on c

  InjDecl(ty, p-ty, _) :-
  where store ty <: p-ty

type rules
  
  Int(i): SimpleSort("int")
  String(s): SimpleSort("string")
  
  eq@TermEq(l, r) :-
  where l : l-ty
    and r : r-ty
    and (r-ty == l-ty or r-ty <: l-ty)
      else error "unlikely to succeed" on eq
  
  neq@TermNeq(l, r) :-
  where l : l-ty
    and r : r-ty
    and (r-ty == l-ty or r-ty <: l-ty)
      else error "very likely to always succeed" on neq
  
relations

  Var(x) <: VarRef(y)
  where x == y