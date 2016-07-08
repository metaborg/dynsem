# Hitlist for optimizations
1. Eliminate interface calls for CONSTRUCTOR(), ARITY(), CHILDREN()
2. Eliminate FINDCONTEXT from all TermBuilds and keep only where necessary
3. Support for lists (to eliminate very deep inlining). The lack of lists, namely having Cons-Nil lists is currently causing the interpreter to have very deep stack. This impacts performance and performance of the JIT.
4. Replace Premise[] children with a dedicated node for the evaluation of multiple premises.
5. Introduce branch profiling in merge-point premise
6. Split up ReductionPremise into:
a. lookup part
b. argument array creation part
c. invocation part
d. result binding part
7. Add DynSem knowledge about types to the interpreter

