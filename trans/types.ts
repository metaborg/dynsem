module types

imports
  lib/runtime-libraries/org.spoofax.meta.runtime.libraries/types/-
  lib/runtime-libraries/org.spoofax.meta.runtime.libraries/relations/-
  
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

  MapSelect(map, key) : SimpleSort("Term")

  InjDecl(ty, p-ty) :-
  where store ty <: p-ty

type rules
  
  Int(i): SimpleSort("Int")
  String(s): SimpleSort("String")
  
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

  // this makes no sense but it's here for some generator bug reason
  Var(x) <: VarRef(y)
  where x == y
