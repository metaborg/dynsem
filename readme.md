
# How to use DynSem in Spoofax

To use DynSem in Spoofax you'll first need a Spoofax language project. Once you have that you can create a **.ds** file anywhere you want.

Let's assume that the following is a fragment of the specification after explanation:

```
  module lang

  signature
    sorts
      Expr
      V

    semantic-components
      Env -> Map<String, Int>
      Sto -> Map<Int, V>

    constructors
      Plus: Expr * Expr -> Expr

    arrows
      Expr -default-> V

  rules

    Env nv |- Plus(e1, e2) :: Sto s -default-> NumV(i) :: Sto s''
    where
      Env nv |- e1 :: Sto s  -default-> NumV(i1) :: Sto s',
      Env nv |- e2 :: Sto s' -default-> NumV(i2) :: Sto s''

```

The evaluation of programs in this language starts on a term of sort **Expr**, evaluates over the arrow **default** and has two semantic components **Env** and **Sto**.

## Generate Java interpreter

Open the top-level file of the specification and invoke the ***All to Java*** transformation from the ***Semantics*** actions menu. This will generate a Java interpreter into the `editor/java/ds/generated/interpreter` directory.

## Invoke the interpreter from actions menu

The goal is to interpret a program in the language by:

1. Opening an editor with the program
2. Activating the ***Interpreter*** > ***Evaluate*** action
3. Observe the evaluated program and the evaluation results side-by-side

We set-up the project to achieve this as follows:

	1. Add a builder that invokes the interpreter (*LANG.str*):
```
  external dsevaluate(|)

  editor-evaluate:
    (_, _, ast, path, _) -> (filename, result)
    where
      filename := <guarantee-extension(|"evaluated.aterm")> path;
      result := <dsevaluate> ast
```
	2. Add an action for it in the language menus (*LANG-Menus.esv*):
```
  menu: "Interpreter"

    action: "Evaluate" = editor-evaluate (openeditor) (realtime) (source)
```
	3. Implement the native strategy `dsevaluate(|)` in the *LANG.strategies* package and register it in the `InteropRegisterer`:
```
  package LANG.strategies;

  public class dsevaluate_0_0 extends Strategy {

    public static dsevaluate_0_0 instance = new dsevaluate_0_0();

    @Override
    public IStrategoTerm invoke(Context context, IStrategoTerm program) {

      return new Generic_I_Expr(null, program).
        exec_default(
          new PersistentTreeMap<String, Integer>(),
          new PersistentTreeMap<Integer, I_V>()
        ).toStrategoTerm(context.getFactory());
    }

  }
```

Note the following replacements in the above fragment:
	
* *LANG* should be replaced by your language name
* `Expr` in `Generic_I_Expr` and in `I_Expr` correspond to the sort `Expr` of your language. For a sort `Foo` they would be `Generic_I_Foo` and `I_Foo`
* `default` in `exec_default` corresponds to the name of the arrow (the `-->` arrow is the same as `-default->`). For an arrow named `doit` the method would called `exec_doit`.
* The arguments of the method correspond to the semantic components of the arrow, first the read-only and then the read-write semantic components.

Once the project is built, an open program can be evaluated by invoking the ***Interpreter*** > ***Evaluate*** action. In the example above the evaluation will result in a term *R_Result_V(res, sto)* where *res* has sort *V* and *sto* is an ATerm representation of the *Sto* semantic component
