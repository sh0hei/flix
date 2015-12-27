package ca.uwaterloo.flix.language.library

import ca.uwaterloo.flix.language.ast.Name
import ca.uwaterloo.flix.language.ast.TypedAst.Type
import ca.uwaterloo.flix.language.ast.TypedAst.Type._

import scala.collection.immutable

object FList {

  // TODO: Which of these should have special syntax?
  // TODO: Which should have multiple names?

  // TODO: Need empty and Cons.


  /**
    * All list operations.
    */
  val Ops: immutable.Map[Name.Resolved, ListOperator] = List(
    "List::null" -> nul,
    "List::head" -> head,
    "List::tail" -> tail,
    // TODO: length
    // TODO: init
    // TODO: last
    // TODO: mapPartial/collect.
    // TODO: partition, or better, splitWith
    // TODO: splitAt
    // TODO: span
    // TODO: sortBy
    // TODO: append
    // TODO: intersperse.
    // TODO: intercalate :: [a] -> [[a]] -> [a]
    // TODO: transpose :: [[a]] -> [[a]]
    // TODO: subsequences :: [a] -> [[a]]
    // TODO: permutations :: [a] -> [[a]]

    "List::find" -> find, // TODO: findLeft
    // TODO at(index)
    // TODO: indexOf
    // TODO: findIndex
    // TODO: replace

    // TODO: Slice
    // TODO: repeat
    // TODO: mapWithIndex.
    "List::memberOf" -> memberOf,
    "List::isPrefixOf" -> isPrefixOf, // TODO: or startsWith
    "List::isInfixOf" -> isInfixOf,
    "List::isSuffixOf" -> isSuffixOf, // TODO: endsWith
    // TODO: isSubsequenceOf
    "List::map" -> map,
    "List::flatMap" -> flatMap,
    "List::reverse" -> reverse,
    "List::foldLeft" -> foldLeft,
    "List::foldRight" -> foldRight,
    "List::concatenate" -> concatenate,

    "List::exists" -> exists,
    "List::forall" -> forall,
    "List::and" -> and,
    "List::or" -> or,
    "List::reduceLeft" -> reduceLeft,
    "List::reduceLeftOpt" -> reduceLeftOpt,
    "List::reduceRight" -> reduceRight,
    "List::reduceRightOpt" -> reduceRightOpt,


    "List::filter" -> filter,
    "List::take" -> take,
    "List::takeWhile" -> takeWhile,
    "List::drop" -> drop,
    "List::dropWhile" -> dropWhile,
    "List::zip" -> zip,
    // TODO: zipWith
    // TODO: unzip
    // TODO: zip
    // TODO: count

    // TODO: oneOf: List[Opt{A]] => Opt[A]

    // TODO: scanLeft, scanRight

    "List::toMap" -> toMap,
    "List::toSet" -> toSet,
    "List::groupBy" -> groupBy,
    // TODO: sum, product, minimum, maximum?
    // TODO: MaximumBy, minimumBy

    // TODO: partial order and lattice ops:
    // List:leq xs ys

    "List::isChain" -> isChain,
    "List::isAntiChain" -> isAntiChain,

    "List::join" -> join,
    "List::meet" -> meet

  ).map {
    case (name, op) => Name.Resolved.mk(name) -> op
  }.toMap

  /**
    * A common super-type for all list operations.
    */
  sealed trait ListOperator extends LibraryOperator

  /**
    * Generic type variables.
    */
  val A = Type.Var("A")
  val B = Type.Var("B")

  /////////////////////////////////////////////////////////////////////////////
  // Basic Operations                                                        //
  /////////////////////////////////////////////////////////////////////////////
  /**
    * The `null : List[A] => Bool` function.
    */
  object nul extends ListOperator {
    val tpe = Lst(A) ~> Bool
  }

  /**
    * The `head : List[A] => A` function.
    */
  object head extends ListOperator {
    val tpe = Lst(A) ~> A
  }

  /**
    * The `tail : List[A] => List[A]` function.
    */
  object tail extends ListOperator {
    val tpe = Lst(A) ~> Lst(A)
  }

  /**
    * The `find : (A => Bool, List[A]) => Opt[A]` function.
    */
  object find extends ListOperator {
    val tpe = (A ~> Bool, Lst(A)) ~> Opt(A)
  }

  /**
    * The `memberOf : (A, List[A]) => Bool` function.
    */
  object memberOf extends ListOperator {
    val tpe = (A, Lst(A)) ~> Bool
  }

  /**
    * The `isPrefixOf : (List[A], List[A]) => Bool` function.
    */
  object isPrefixOf extends ListOperator {
    val tpe = (Lst(A), Lst(A)) ~> Bool
  }

  /**
    * The `isInfixOf : (List[A], List[A]) => Bool` function.
    */
  object isInfixOf extends ListOperator {
    val tpe = (Lst(A), Lst(A)) ~> Bool
  }

  /**
    * The `isSuffixOf : (List[A], List[A]) => Bool` function.
    */
  object isSuffixOf extends ListOperator {
    val tpe = (Lst(A), Lst(A)) ~> Bool
  }

  /////////////////////////////////////////////////////////////////////////////
  // Transformations                                                         //
  /////////////////////////////////////////////////////////////////////////////
  /**
    * The `map : (A => B, List[A]) => List[B]` function.
    */
  object map extends ListOperator {
    val tpe = (A ~> B, Lst(A)) ~> Lst(B)
  }

  /**
    * The `flatMap : (A => List[B], List[A]) => List[B]` function.
    */
  object flatMap extends ListOperator {
    val tpe = (A ~> Lst(B), Lst(A)) ~> Lst(B)
  }

  /**
    * The `reverse : List[A] => List[A]` function.
    */
  object reverse extends ListOperator {
    val tpe = Lst(A) ~> Lst(A)
  }

  /////////////////////////////////////////////////////////////////////////////
  // Folds                                                                   //
  /////////////////////////////////////////////////////////////////////////////
  /**
    * The `foldLeft : ((B, A) => B, B, List[A]) => B` function.
    */
  object foldLeft extends ListOperator {
    val tpe = ((B, A) ~> B, B, Lst(A)) ~> B
  }

  /**
    * The `foldRight : ((A, B) => B, B, List[A]) => B` function.
    */
  object foldRight extends ListOperator {
    val tpe = ((A, B) ~> B, B, Lst(A)) ~> B
  }

  /////////////////////////////////////////////////////////////////////////////
  // Special Folds                                                           //
  /////////////////////////////////////////////////////////////////////////////
  /**
    * The `concatenate : List[List[A]] => List[A]` function.
    */
  object concatenate extends ListOperator {
    val tpe = Lst(Lst(A)) ~> Lst(A)
  }

  /**
    * The `exists : (A => Bool, List[A]) => Bool` function.
    */
  object exists extends ListOperator {
    val tpe = (A ~> Bool, Lst(A)) ~> Bool
  }

  /**
    * The `forall : (A => Bool, List[A]) => Bool` function.
    */
  object forall extends ListOperator {
    val tpe = (A ~> Bool, Lst(A)) ~> Bool
  }

  /**
    * The `and : List[Bool] => Bool` function.
    */
  object and extends ListOperator {
    val tpe = Lst(Bool) ~> Bool
  }

  /**
    * The `or : List[Bool] => Bool` function.
    */
  object or extends ListOperator {
    val tpe = Lst(Bool) ~> Bool
  }

  /**
    * The `reduceLeft : ((A, A) => A, List[A]) => A` function.
    */
  object reduceLeft extends ListOperator {
    val tpe = ((A, A) ~> A, Lst(A)) ~> A
  }

  /**
    * The `reduceLeft : ((A, A) => A, List[A]) => Opt[A]` function.
    *
    * Optionally returns the result of applying the binary operator going from left to right.
    *
    * Returns None if the list is empty.
    */
  object reduceLeftOpt extends ListOperator {
    val tpe = ((A, A) ~> A, Lst(A)) ~> Opt(A)
  }

  /**
    * The `reduceRight : ((A, A) => A, List[A]) => A` function.
    */
  object reduceRight extends ListOperator {
    val tpe = ((A, A) ~> A, Lst(A)) ~> A
  }

  /**
    * The `reduceRight : ((A, A) => A, List[A]) => Opt[A]` function.
    *
    * Optionally returns the result of applying the binary operator going from right to left.
    *
    * Returns None if the list is empty.
    */
  object reduceRightOpt extends ListOperator {
    val tpe = ((A, A) ~> A, Lst(A)) ~> Opt(A)
  }

  /////////////////////////////////////////////////////////////////////////////
  // Sub Lists                                                               //
  /////////////////////////////////////////////////////////////////////////////
  /**
    * The `filter : (A => Bool, List[A]) => List[A]` function.
    */
  object filter extends ListOperator {
    val tpe = (A ~> Bool, Lst(A)) ~> Lst(A)
  }

  /**
    * The `take : (Int, List[A]) => List[A]` function.
    */
  object take extends ListOperator {
    val tpe = (Int, Lst(A)) ~> Lst(A)
  }

  /**
    * The `takeWhile : (A => Bool, List[A]) => List[A]` function.
    */
  object takeWhile extends ListOperator {
    val tpe = (A ~> Bool, Lst(A)) ~> Lst(A)
  }

  /**
    * The `drop : (Int, List[A]) => List[A]` function.
    */
  object drop extends ListOperator {
    val tpe = (Int, Lst(A)) ~> Lst(A)
  }

  /**
    * The `dropWhile : (A => Bool, List[A]) => List[A]` function.
    */
  object dropWhile extends ListOperator {
    val tpe = (A ~> Bool, Lst(A)) ~> Lst(A)
  }

  /////////////////////////////////////////////////////////////////////////////
  // Zipping                                                                 //
  /////////////////////////////////////////////////////////////////////////////
  /**
    * The `zip : (List[A], List[B]) => List[(A, B)]` function.
    */
  object zip extends ListOperator {
    val tpe = (Lst(A), Lst(B)) ~> Lst((A, B))
  }

  /////////////////////////////////////////////////////////////////////////////
  // Conversions                                                             //
  /////////////////////////////////////////////////////////////////////////////
  /**
    * The `toMap : List[(A, B)] => Map[A, B]` function.
    */
  object toMap extends ListOperator {
    val tpe = Lst((A, B)) ~> Type.Map(A, B)
  }

  /**
    * The `toSet : List[A] => Set[A]` function.
    */
  object toSet extends ListOperator {
    val tpe = Lst(A) ~> Set(A)
  }

  /////////////////////////////////////////////////////////////////////////////
  // Grouping                                                                //
  /////////////////////////////////////////////////////////////////////////////
  /**
    * The `groupBy : ((A, A) => Bool, List[A]) => List[List[A]]` function.
    */
  object groupBy extends ListOperator {
    val tpe = ((A, A) ~> Bool, Lst(A)) ~> Lst(Lst(A))
  }

  /////////////////////////////////////////////////////////////////////////////
  // Order and Lattice Operations                                            //
  /////////////////////////////////////////////////////////////////////////////
  /**
    * Returns `true` iff the list is a chain according to the partial order.
    *
    * The function has type `chain: List[A] => Bool`.
    */
  object isChain extends ListOperator {
    val tpe = Lst(A) ~> Bool
  }

  /**
    * Returns `true` iff the list is an anti-chain according to the partial order.
    *
    * The function has type `antiChain: List[A] => Bool`.
    */
  object isAntiChain extends ListOperator {
    val tpe = Lst(A) ~> Bool
  }

  /**
    * Returns the least upper bound of all elements in the given list. Returns bottom if the list is empty.
    *
    * The function has type: `join: List[A] => A`.
    */
  object join extends ListOperator {
    val tpe = Lst(A) ~> A
  }

  /**
    * Returns the greatest lower bound of all elements in the given list. Returns top if the list is empty.
    *
    * The function has type `meet: List[A] => A`.
    */
  object meet extends ListOperator {
    val tpe = Lst(A) ~> A
  }

}
