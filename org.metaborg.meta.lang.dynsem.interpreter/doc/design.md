# Design of the DynSem interpreter

The goal of a DynSem interpreter is to allow interpretation of a program in a language according to the dynamic semantics specified as reduction rules in DynSem.

## Interpreter input and output

Given a language L, the input to an interpreter consists of:

- a signature *S(L)* of terms in *L*
- a signature *V(L)* of value terms
- a set of reduction rules *R(L)* reducing terms in L
- a program *P(L)* to interpret

## DynSem sub-languages and their sub-interpreters

Distinguish the following DynSem sub-languages:
- signatures: the language of DynSem signatures
- rules: the language of DynSem reduction rules

### Language of signatures

The language of signatures is used to define the structure that terms in an interpreter must obey. A signature definition derives a (1) term construction evaluation strategy and (2) term de-construction evaluation strategy, i.e. term building and pattern matching.

Question: do we interpret the signatures or compile language-specific logic?

#### Interpretation of the signatures (as ATerms)

The language of signatures mainly consists of constructor declarations. Each constructor defines two functions: the construction and deconstruction functions. 

Building an application term is evaluation of a series of term constructions for the children and one construction for the application term itself.

Matching a pattern is the evaluation of one check for the current term followed by matching for the children.

Maps are also parts of terms. They do not have an equivalent in ATerms.

#### Generation of signatures with building and matching logic

Using this method we no longer waste time and space with fake generic terms such as ATerms which we have to instantiate. And we also get rid of boxing and unboxing.

The idea is that any Java type can be a term. The signatures describe how the terms get constructed. There are a number of builtin types: Int, Long, Float, Double, Bool, String, Map<K,V>, List<K>, Set<K>, Array<K>.

The signature section defines the structure of language-specific classes to be generated. We derive from signatures a class hierarchy specific for representing the program and data of a specific language. The generated class hierarchy is just dumb data.

### Language of reduction rules

This language consists of the vocabulary that make up reduction rules. This vocabulary includes reduction rules, premises, term construction and pattern matching.

Instead of compiling reduction rules we interpret them as functions. The functions dispatch on the type of term that is reduced. A rule performs any combination of the following operations:
- term building
- pattern matching
- map update
- reduction rule application
- variable binding
- variable reading

#### Example execution scenario

We are evaluating an application term for constructor Plus/2 of sort Expr. We lookup the reduction rule for “RULE/2”. If no such rule is found we lookup a reduction rule for “SORT/EXPR”. For the chosen rule