module types

type rules

  VarRef(x) : ty
  where definition of x : ty
  
  Con(c, t*) : ty
  where definition of c : (ty*, ty)
    and t* : ty_t*
    and ty* == ty_t* 
        else error "types of sub-terms do not match constructor definition" on c
