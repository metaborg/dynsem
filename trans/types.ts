module types

imports
  lib/runtime-libraries/org.spoofax.meta.runtime.libraries/types/-
  lib/runtime-libraries/org.spoofax.meta.runtime.libraries/relations/-
  lib/runtime-libraries/org.spoofax.meta.runtime.libraries/task/-
  names
  include/ds
  ds
  
relations

  define transitive <:
  
  define <compat:

type rules

  Var(x) + VarRef(x) + MatchedVar(x): ty
  where definition of x : ty
  
  Con(c, t*) : ty
  where definition of c : (ty*, ty)
    and t* : ty_t*
    and (ty_t* == ty* or ty_t* <: ty*)
    else error "types of sub-terms do not match constructor definition" on c

  l@List([]) : l-ty
  where
    l has expected-type l-ty
    or ListSort(SimpleSort("Term")) => l-ty
  
  ListTail([x], _) : ListSort(x-ty)
  where x : x-ty
  
  SortFunCall(f, parent-ref, aparam*): ty
  where definition of f : (fparam_ty*, ty)
    and aparam* : aparam_ty*
    and (aparam_ty* == fparam_ty* or aparam_ty* <: fparam_ty*)
    else error "actual parameter types are incompatible with formal parameter types" on f

  rel@Relation(_, Source(s, _), NamedDynamic(arrow),  Target(t, _)) :-
  where
    definition of arrow : (l-ty, r-ty)
    and s : s-ty
    and t : t-ty
    and s-ty <compat: l-ty
    and t-ty <compat: r-ty 
    else error "source and target types are incompatible with arrow definition types" on rel

  MapSelect(map, key) : SimpleSort("Term")

  InjDecl(ty, p-ty) :-
  where store ty <: p-ty
  
  NativeSubTypeDecl(_, ty, sup-ty, _) :-
  where store ty <: sup-ty

type rules
  
  Int(i): SimpleSort("Int")
  String(s): SimpleSort("String")
  
  // m@Match(l, r) :-
  // where l : l-ty
  //   and r : r-ty
  //   and (r-ty == l-ty or r-ty <: l-ty or l-ty <: r-ty)
  //     else error "unlikely to succeed" on m
  //  
  // eq@TermEq(l, r) :-
  // where l : l-ty
  //   and r : r-ty
  //   and (r-ty == l-ty or r-ty <: l-ty or l-ty <: r-ty)
  //     else error "unlikely to succeed" on eq
  // 
  // neq@TermNeq(l, r) :-
  // where l : l-ty
  //   and r : r-ty
  //   and (r-ty == l-ty or r-ty <: l-ty or l-ty <: r-ty)
  //     else error "very likely to always succeed" on neq
  
relations

  // this makes no sense but it's here for some generator bug reason
  Var(x) <: VarRef(y)
  where x == y
  
  ListSort(x-ty) <: ListSort(y-ty)
  where x-ty => Any()
    or x-ty <: y-ty
  
  s-ty <compat: l-ty
  where s-ty == l-ty
    or s-ty <: l-ty

