
# How to use DynSem in Spoofax

To use DynSem in Spoofax you'll first need a Spoofax language project. Once you have that you can create a **.ds** file anywhere you want.

Let's assume that your language is called *LANG*. Let's assume that the following is a fragment of the specification after explanation:

```
  module LANG

  signature
    sorts
      Expr
      V

    aliases
      Env : Map<String, Int>
      Sto : Map<Int, V>

    constructors
      Plus: Expr * Expr -> Expr
      NumV: Int -> V

    arrows
      Expr -default-> V

  rules

    Env nv |- Plus(e1, e2) :: Sto s -default-> NumV(i1) :: Sto s''
    where
      Env nv |- e1 :: Sto s  -default-> NumV(i1) :: Sto s';
      Env nv |- e2 :: Sto s' -default-> NumV(i2) :: Sto s''.

```

The evaluation of programs in this language starts on a term of sort **Expr**, evaluates over the arrow **default** and has two semantic components **Env** and **Sto**.

## Generate Java interpreter

Open the top-level file of the specification and invoke the ***All to Java*** transformation from the ***Semantics*** actions menu. This will generate a Java interpreter into the `editor/java/ds/generated/interpreter` directory.


## Update the language project for running DynSem-derived interpreters

We'll update the project to correctly include dependencies to DynSem:

1. Add dependency via Maven to the interpreter framework (right click on project > ***Maven*** > ***Add Dependency***):
    - Group id: **org.metaborg**
    - Artifact id: **org.metaborg.meta.interpreter.framework**
    - Version: **1.5.0-SNAPSHOT**
2. Update Eclipse project using Maven
3. Add dependency to **org.metaborg.meta.interpreter.framework** plugin project to the project.
4. Configure the project builder to include DynSem-derived Java classes into the Jar
    - Add `<property name="javajar-includes" value="LANG/strategies/, ds/**" />` to **build.main.xml**

## Invoke the interpreter from actions menu

The goal is to interpret a program in the language by:

1. Opening an editor with the program
2. Activating the ***Interpreter*** > ***Evaluate*** action
3. Observe the evaluated program and the evaluation results side-by-side

We set-up the project to achieve this as follows:

1. Add a builder that invokes the interpreter (*trans/LANG.str*):

		 external dsevaluate(|)

		 editor-evaluate:
		   (_, _, ast, path, _) -> (filename, result)
		   where
		     filename := <guarantee-extension(|"evaluated.aterm")> path;
		     result := <dsevaluate> ast

2. Add an action for it in the language menus (*editor/LANG-Menus.esv*):

		menu: "Interpreter"

			action: "Evaluate" = editor-evaluate (openeditor) (realtime) (source)

3. Implement the native strategy `dsevaluate(|)` in the *editor/java/LANG.strategies* package and register it in the `InteropRegisterer`:

		package LANG.strategies;

		public class dsevaluate_0_0 extends Strategy {

			public static dsevaluate_0_0 instance = new dsevaluate_0_0();

			@Override
			public IStrategoTerm invoke(Context context, IStrategoTerm program) {

				return new Generic_A_Expr(null, program).
					exec_default(
						new PersistentTreeMap<String, Integer>(),
						new PersistentTreeMap<Integer, A_V>()
					).toStrategoTerm(context.getFactory());
			}
		}


Note the following replacements in the above fragment:

* *LANG* should be replaced by your language name
* `Expr` in `Generic_A_Expr` and in `A_Expr` correspond to the sort `Expr` of your language. For a sort `Foo` they would be `Generic_A_Foo` and `A_Foo`
* `default` in `exec_default` corresponds to the name of the arrow (the `-->` arrow is the same as `-default->`). For an arrow named `doit` the method would called `exec_doit`.
* The arguments of the method correspond to the semantic components of the arrow, first the read-only and then the read-write semantic components.

Once the project is built, an open program can be evaluated by invoking the ***Interpreter*** > ***Evaluate*** action. In the example above the evaluation will result in a term *R_Result_V(res, sto)* where *res* has sort *V* and *sto* is an ATerm representation of the *Sto* semantic component

### Changenotes 17/07/2015

- Eliminated `semantic-components` section
- Maps can be declared and used everywhere
- Implements a new signature sections `aliases` where sort aliases can be declared.

#### Updating specifications

Specification which have `semantic-components` sections to declare synonyms for maps will need to declare the maps differently. A specification section declaring `semantic-components` such as:

```
semantic-components
  Env -> Map<String, Value>
```

should be rewritten to:

```
aliases
  Env : Map<String, Value>
```

### Changenotes 26/05/2015

Dispatch on variables is now supported.

### Changenotes and specification update instructions 20/05/2015

#### 1. Syntax changes

A number of changes w.r.t. delimiters are implemented which reduce ambiguities:

  - rules must end with a full stop (`.`)
  - premises in rules must be separated by semi-colons (`;`) instead of commas
  - semantic components (read-only and read-write) must be separated by commas
  - read-write semantic components appearing on the target-side of relation premises or conclusions no longer have to end with a full-stop (`.`), unless a full-stop is required at the position because the rule ends there

For example, the following old rule:

    EnvA ea EnvB eb |- Foo(x) :: StoA sa StoB sb --> y :: StoA sa' StoB sb'.
    where
      EnvA ea |- Bar(x) :: StoA sa --> y :: StoA sa'.,
      EnvB eb |- Baz(z) :: StoB sb --> _ :: StoB sb'.


Becomes:

    EnvA ea, EnvB eb |- Foo(x) :: StoA sa, StoB sb --> y :: StoA sa', StoB sb'
    where
      EnvA ea |- Bar(x) :: StoA sa --> y :: StoA sa';
      EnvB eb |- Baz(z) :: StoB sb --> _ :: StoB sb'.


#### 2. Implicit constructors

Support for injections has been dropped because the design did not allow for a sort being injected in multiple target sorts. Offering a wider set of features, injections are replaced by implicit constructors.

For example, the following old specification using injections:

    signature
      sorts
        Expr
        NumLit -> Expr
        BinExpr -> Expr
        BoolLit -> Expr
        BoolExpr -> Expr

      constructors
        Num: Int -> NumLit
        Plus: Expr * Expr -> BinExpr
        True: BoolLit
        False: BoolLit
        And: Expr * Expr -> BoolExpr

      sorts
        V
        BoolV -> V
        NumV -> V

      constructors
        IntV: Int -> NumV
        BoolV: Int -> BoolV

      arrows
        Expr --> V

    rules

      Num(i) --> i

      Plus(e1, e2) --> plusI(i1, i2)
      where
        e1 --> IntV(i1),
        e2 --> IntV(i2)

      And(e1, e2) --> andB(b1, b2)
      where
        e1 --> BoolV(b1),
        e2 --> BoolV(b2)

      True() --> BoolV(1)
      False() --> BoolV(0)


Drops the injections and received implicit constructor declarations instead:

    signature
      sorts
        Expr
        NumLit
        BinExpr
        BoolLit
        BoolExpr

      constructors
        __NumLit2Expr__: NumLit -> Expr {implicit}
        __BinExpr2Expr__: BinExpr -> Expr {implicit}
        __BoolLit2Expr__: BoolLit -> BinExpr {implicit}
        __BoolExpr2Expr__: BoolExpr -> Expr {implicit}

      constructors
        Num: Int -> NumLit
        Plus: Expr * Expr -> BinExpr
        True: BoolLit
        False: BoolLit
        And: Expr * Expr -> BoolExpr

      sorts
        V
        BoolV
        NumV

      constructors
        __BoolV2V__: BoolV -> V {implicit}
        __NumV2V__: NumV -> V {implicit}

      constructors
        IntV: Int -> NumV
        BoolV: Int -> BoolV

      arrows
        Expr --> V

    rules

      Num(i) --> IntV(i).

      Plus(e1, e2) --> IntV(plusI(i1, i2))
      where
        e1 --> IntV(i1);
        e2 --> IntV(i2).

      And(e1, e2) --> BoolV(andB(b1, b2))
      where
        e1 --> BoolV(b1);
        e2 --> BoolV(b2).

      True() --> BoolV(1).
      False() --> BoolV(0).

The boxing and unboxing of the implicit constructors is automatic (hence the *implicit*-ness).


#### 3. Chained implicit coercions and implicit reductions

DynSem now provides full support for chains of implicit reductions and implicit coercions. The limitation that implicit reductions were restricted to the arrow in local context has been lifted.

The above specification can be written as follows taking full advantage of implicit chains:

    signature
      sorts
        Expr
        NumLit
        BinExpr
        BoolLit
        BoolExpr

      constructors
        __NumLit2Expr__: NumLit -> Expr {implicit}
        __BinExpr2Expr__: BinExpr -> Expr {implicit}
        __BoolLit2Expr__: BoolLit -> BinExpr {implicit}
        __BoolExpr2Expr__: BoolExpr -> Expr {implicit}

      constructors
        Num: Int -> NumLit
        Plus: Expr * Expr -> BinExpr
        True: BoolLit
        False: BoolLit
        And: Expr * Expr -> BoolExpr

      sorts
        V
        BoolV
        NumV

      constructors
        __BoolV2V__: BoolV -> V {implicit}
        __NumV2V__: NumV -> V {implicit}

      constructors
        IntV: Int -> NumV
        BoolV: Int -> BoolV

      arrows
        Expr --> V

    rules

      Num(i) --> IntV(i).

      Plus(IntV(i1), IntV(i2)) --> IntV(plusI(i1, i2)).

      And(BoolV(b1), BoolV(b2)) --> BoolV(andB(b1, b2)).

      True() --> BoolV(1).
      False() --> BoolV(0).

DynSem frontend explicates the implicit transformations in the rules such that they become:


    rules

      __NumLit2Expr__(Num(i)) --> __NumV2V__(IntV(i)).

      __BinExpr2Expr__(Plus(e1, e2)) --> __NumV2V__(IntV(plusI(i1, i2)))
      where
        e1 --> __NumV2V__(IntV(i1));
        e2 --> __NumV2V__(IntV(i2)).

      __BoolExpr2Expr__(And(e1, e2)) --> BoolV(andB(b1, b2))
      where
        e1 --> __BoolV2V__(BoolV(b1));
        e2 --> __BoolV2V__(BoolV(b2)).

      __BoolLit2Expr__(True()) --> __BoolV2V__(BoolV(1)).
      __BoolLit2Expr__(False()) --> __BoolV2V__(BoolV(0)).
