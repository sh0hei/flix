package ca.uwaterloo.flix.language.backend.phase

import ca.uwaterloo.flix.Flix.FlixError
import ca.uwaterloo.flix.language.ast.Ast.Annotation
import ca.uwaterloo.flix.language.ast.{SourceLocation, TypedAst}
import ca.uwaterloo.flix.runtime.Value

object Verifier {

  sealed trait Property

  object Property {

    /**
      * Properties of Unary Operators.
      */
    object UnaryOperator {

      /**
        * Strictness of a unary function.
        */
      case class Strictness(op: TypedAst.Expression.Lambda, lat: TypedAst.Definition.BoundedLattice) extends Property {
        val (f, x) = (op.lam, 'x.ofType(op.args.head.tpe))

        val property = f(lat.bot.value) ≡ ???
      }

      // TODO: Monotone
    }

    /**
      * Properties of Binary Operators.
      */
    object BinaryOperator {

      /**
        * Associativity.
        */
      case class Associativity(op: TypedAst.Expression) extends Property {
        val (f, x, y, z) = (op.lam, 'x.ofType(op.tpe), 'y.ofType(op.tpe), 'z.ofType(op.tpe))

        val property = ∀(x, y, z)(f(f(x, y), z) ≡ f(x, f(y, z)))
      }

      /**
        * Commutativity.
        */
      case class Commutativity(op: TypedAst.Expression) extends Property {
        val (f, x, y) = (op.lam, 'x.ofType(op.tpe), 'y.ofType(op.tpe))

        val property = ∀(x, y)(f(x, y) ≡ f(y, x))
      }

      // TODO: Strictness.
      //
      //  /**
      //   * Monotone: ?x1, x2. x1 ? x2 ? f(x1) ? f(x2).
      //   */
      //  def monotone1(f: Term.Abs, leq: Term.Abs): Term.Abs =
      //    Term.Abs('x1, leq.typ, Term.Abs('x2, leq.typ,
      //      leq.call('x1, 'x2) ==> leq.call(f.call('x1), f.call('x2))))
      //
      //  /**
      //   * Monotone: ?x1, x2, y1, y2. x1 ? x2 ? y1 ? y2 ? f(x1, y1) ? f(x2, y2).
      //   */
      //  def monotone2(f: Term.Abs, leq: Term.Abs): Term.Abs =
      //    Term.Abs('x1, leq.typ, Term.Abs('x2, leq.typ, Term.Abs('y1, leq.typ, Term.Abs('y2, leq.typ,
      //      (leq.call('x1, 'x2) && leq.call('y1, 'y2)) ==> leq.call(f.call('x1, 'y1), f.call('x2, 'y2))))))

    }

    /**
      * Properties of Partial Orders.
      */
    object PartialOrder {

      /**
        * Reflexivity.
        */
      case class Reflexivity(lattice: TypedAst.Definition.BoundedLattice) extends Property {
        val x = 'x.ofType(lattice.tpe)

        val property = ∀(x)(x ⊑ x)
      }

      /**
        * Anti-symmetry.
        */
      case class AntiSymmetry(lattice: TypedAst.Definition.BoundedLattice) extends Property {
        val (x, y) = ('x.ofType(lattice.tpe), 'y.ofType(lattice.tpe))

        val property = ∀(x, y)((x ⊑ y) ∧ (y ⊑ x)) → (x ≡ y)
      }

      /**
        * Transitivity.
        */
      case class Transitivity(lattice: TypedAst.Definition.BoundedLattice) extends Property {
        val (x, y, z) = ('x.ofType(lattice.tpe), 'y.ofType(lattice.tpe), 'z.ofType(lattice.tpe))

        val property = ∀(x, y, z)(((x ⊑ y) ∧ (y ⊑ z)) → (x ⊑ z))
      }

      /**
        * Ascending Chain Condition
        */
      //  /**
      //   * Non-Negative: ?x. f(x) > 0.
      //   */
      //  def nonNegative(h: Term.Abs): Term.Abs =
      //    Term.Abs('x, h.typ,
      //      Term.BinaryOp(BinaryOperator.Greater, h.call('x), Term.Int(0)))
      //
      //  /**
      //   * Stricly-Decreasing: ?x, y. x ? y ? x != y ? f(x) > f(y).
      //   */
      //  def strictlyDecreasing(h: Term.Abs, leq: Term.Abs): Term.Abs =
      //    Term.Abs('x, h.typ, Term.Abs('y, h.typ,
      //      (leq.call('x, 'y) && (Term.Var('x) !== Term.Var('y))) ==>
      //        Term.BinaryOp(BinaryOperator.Greater, h.call('x), h.call('y))))


    }

    /**
      * Properties of Join Semi Lattices.
      */
    object JoinSemiLattice {

      /**
        * The least element must be bottom.
        */
      case class LeastElement(lattice: TypedAst.Definition.BoundedLattice) extends Property {
        val x = 'x.ofType(lattice.tpe)

        val property = ∀(x)(lattice.bot ⊑ x)
      }

      /**
        * The lub must be an upper bound.
        */
      case class UpperBound(lattice: TypedAst.Definition.BoundedLattice) extends Property {
        val (x, y) = ('x.ofType(lattice.tpe), 'y.ofType(lattice.tpe))

        val property = ∀(x, y)((x ⊑ (x ⊔ y)) ∧ (y ⊑ (x ⊔ y)))
      }


      //  /**
      //   * Least Upper Bound: ?x, y, z. x ? z ? y ? z ? x ? y ? z.
      //   */
      //  def leastUpperBound(leq: Term.Abs, lub: Term.Abs): Term.Abs =
      //    Term.Abs('x, leq.typ, Term.Abs('y, leq.typ, Term.Abs('z, leq.typ,
      //      (leq.call('x, 'z) && leq.call('y, 'z)) ==> leq.call(lub.call('x, 'y), 'z))))
      //
      //

    }

  }

  sealed trait VerifierError extends FlixError

  object VerifierError {

    /**
      * An error raised to indicate that a partial order is not reflexive.
      *
      * @param prop the violated property.
      * @param elm the element that violates the property.
      * @param loc the location of the partial order `leq`.
      */
    case class NonReflexivity(lat: TypedAst.Definition.BoundedLattice, prop: Property, elm: Value, loc: SourceLocation) extends VerifierError {
      val format = "" // TODO
      //      val format =
      //        s"""${consoleCtx.blue(s"-- VERIFIER ERROR -------------------------------------------------- ${loc.source.format}")}
      //           |
      //            |${consoleCtx.red(s">> Duplicate definition of the variable '$name'.")}
      //           |
      //            |First definition was here:
      //           |${loc1.underline}
      //           |Second definition was here:
      //           |${loc2.underline}
      //           |Tip: Consider renaming or removing one of the aliases.
      //         """.stripMargin

      val format2 = List(
        Header("VERIFICATION ERROR", loc.source.format),
        BlankLine,
        Line(Red(s">> Reflexivity violated for $lat.")),
        BlankLine,
        Line(s"The element $elm does not satisfy x <= x."), // TODO: Consider custom formatter? How to color elm red?
        BlankLine,
        Line("The partial order was defined here:"),
        Location(loc)
      )
    }

  }


  sealed trait Formula {
    // TODO: override toString

  }

  object Formula {

    // TODO: probably need to carry a type.

    case class BinOp(name: String) extends Formula {
      def apply(f1: Formula, f2: Formula): Formula = ???

    }

    case class Var(name: String) extends Formula

    case class Lambda(e: TypedAst.Expression) extends Formula {
      def apply(that: Formula): Formula = ???

      def apply(e1: Formula, e2: Formula): Formula = ???
    }

    case class Implication(f1: Formula, f2: Formula) extends Formula

    case class Leq(f1: Formula, f2: Formula) extends Formula

    case class Lub(f1: Formula, f2: Formula) extends Formula

  }

  def ∀(x: Formula.Var*)(f: Formula): Formula = ???

  implicit class RichSymbol(val thiz: Symbol) {
    def ofType(tpe: TypedAst.Type): Formula.Var = ???
  }

  implicit class RichExp(val thiz: TypedAst.Expression) {
    def ⊑(that: Formula): Formula = ???

    def lam: Formula.Lambda = ???

    def value: Formula = ???
  }

  implicit class RichFormula(val thiz: Formula) {

    def ∧(that: Formula): Formula = ???

    def →(that: Formula): Formula = Formula.Implication(thiz, that)

    def ⊑(that: Formula): Formula = Formula.Leq(thiz, that)

    def ⊔(that: Formula): Formula = Formula.Lub(thiz, that)

    def ≡(that: Formula): Formula = ???

  }


  // TODO
  case class Header(s: String, s2: String)

  case object BlankLine

  case class Line(s: Any)

  case class Red(s: String)

  case class Location(s: Any)

  def doStuff(root: TypedAst.Root): Unit = {

    val partialOrderProperties = lattices(root) flatMap {
      case l => List(
        Property.PartialOrder.Reflexivity(l),
        Property.PartialOrder.AntiSymmetry(l),
        Property.PartialOrder.Transitivity(l)
      )
    }

    val latticeProperties = lattices(root) flatMap {
      case l => List(
        Property.JoinSemiLattice.LeastElement(l)
      )
    }

    val functionProperties = lambdas(root) flatMap {
      case f if f.annotations.isUnchecked => Nil
      case f => f.annotations.annotations.collect {
        case Annotation.Associative(loc) => Property.BinaryOperator.Associativity(f)
        case Annotation.Commutative(loc) => Property.BinaryOperator.Commutativity(f)
        case Annotation.Strict(loc) =>
          if (f.args.length == 1)
            Property.UnaryOperator.Strictness(f, ???)
          else ???
      }
    }

    val properties = partialOrderProperties ++ functionProperties

    properties.foreach(p => Console.println(p.toString))
  }

  def lattices(root: TypedAst.Root): List[TypedAst.Definition.BoundedLattice] = ???

  def lambdas(root: TypedAst.Root): List[TypedAst.Expression.Lambda] = ???


}