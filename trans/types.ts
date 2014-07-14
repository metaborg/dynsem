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

  Fresh() : SimpleSort("Int")

  Var(x) + VarRef(x) + MatchedVar(x): ty
  where definition of x : ty
  
  Con(c, t*) : ty
  where definition of c : (ty*, ty)
    and t* : ty_t*
    and ty_t* <compat: ty*
    else error "types of sub-terms do not match constructor definition" on c

  l@List([]) : l-ty
  where
    l has expected-type l-ty
    or ListSort(SimpleSort("Term")) => l-ty
  
  lt@ListTail([x], _) : lt-ty
  where
    lt has expected-type lt-ty
    or (x : x-ty and ListSort(x-ty) => lt-ty)
  
  LabelComp(lc, e) :-
  where
    definition of lc : lc-ty
    and e : e-ty
    and e-ty <compat: lc-ty
    else error "expression type is incompatible with component type" on e 
  
  SortFunCall(f, parent-ref, aparam*): ty
  where definition of f : (fparam_ty*, ty)
    and aparam* : aparam_ty*
    and aparam_ty* <compat: fparam_ty*
    else error "actual parameter types are incompatible with formal parameter types" on f

  rel@Relation(_, Source(s, _), NamedDynamicEmitted(_, arrow),  Target(t, _)) :-
  where
    definition of arrow : (l-ty, r-ty)
    and s : s-ty
    and t : t-ty
    and s-ty <compat: l-ty
    and t-ty <compat: r-ty 
    else error "source and target types are incompatible with arrow definition types" on rel

  Map([]) : MapSort(SimpleSort("Term"), SimpleSort("Term"))
  
  Map([Bind(key, val)]) : MapSort(key-ty, val-ty)
  where
    key : key-ty
    and val : val-ty
  
  Map([Binds(key, val)]) : MapSort(key-ty, val-ty)
  where
    key : keys-ty
    and val : vals-ty
    and keys-ty => ListSort(key-ty)
    and vals-ty => ListSort(val-ty)

  MapUnbind(map1, map2) : map2-ty
  where
    map1 : map1-ty
    and map2 : map2-ty

  // TODO here we need to take LUB(key1-ty, key2-ty) and LUB(val1-ty, val2-ty)
  MapExtend(map1, map2) : map2-ty
  where
    map1 : map1-ty
    and map2 : map2-ty
    
  ms@MapSelect(map, key) : def-val-ty
  where
    definition of map : map-ty
    and definition of map-ty has stored-tys key-val-tys
    and key-val-tys => (def-key-ty, def-val-ty)
    and key : use-key-ty
    and use-key-ty <compat: def-key-ty
    else error "incompatible map access" on ms

  // SemanticComponent(ty, map-type) :-
  // where store ty <: map-type

  InjDecl(ty, p-ty) :-
  where store ty <: p-ty
  
  NativeSubTypeDecl(_, ty, sup-ty, _) :-
  where store ty <: sup-ty

type rules
  
  Int(i): SimpleSort("Int")
  String(s): SimpleSort("String")
  True() : SimpleSort("Bool")
  False() : SimpleSort("Bool")
  
relations

  // this makes no sense but it's here for some generator bug reason
  Var(x) <: VarRef(y)
  where x == y
  
  s-ty <compat: l-ty
  where
    s-ty == l-ty
    or s-ty <: l-ty
