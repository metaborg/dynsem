module types

imports
  lib/types/-

type rules

  Var(x): ty
  where definition of x : ty

  VarRef(x) : ty
  where definition of x : ty
  
  TypedVar(x, _) : ty
  where definition of x : ty
  
  Con(c, t*) : ty
  where definition of c : (ty*, ty)
    and t* : ty_t*
    and ty* == ty_t*
        else error "types of sub-terms do not match constructor definition" on c


