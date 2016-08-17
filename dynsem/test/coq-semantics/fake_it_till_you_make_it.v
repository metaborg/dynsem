Definition String := nat.
Definition Int := nat.
Definition map X Y := list (X * Y).
Definition plusI2 := plus.

Axiom map_update : forall {X Y}, map X Y -> X -> Y -> map X Y.

Axiom map_lookup : forall {X Y}, map X Y -> X -> Y.
