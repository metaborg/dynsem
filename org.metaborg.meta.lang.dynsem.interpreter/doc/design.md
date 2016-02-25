# Hitlist for optimizations

1. Replace Premise[] children with a dedicated node for the evaluation of multiple premises.
2. Introduce branch profiling in merge-point premise
3. Split up ReductionPremise into:
a. lookup part
b. argument array creation part
c. invocation part
d. result binding part
4. Add DynSem knowledge about types to the interpreter

