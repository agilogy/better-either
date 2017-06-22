package com.agilogy.either

import com.agilogy.classis.monoid.Semigroup

import scala.collection.generic.CanBuildFrom
import scala.language.{higherKinds, implicitConversions}
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}

package object extras extends EitherExtraSyntax {

  def right[R](r: R): Either[Nothing, R] = Right(r)
  def left[E](e: E): Either[E, Nothing] = Left(e)

  // Sequence

  type Fold[M[_], a, b] = (M[a]) => (=> b) => (a => b) => b

  def sequence[M[_], E, R](m: M[Either[E, R]])(empty: M[R])(pure: R => M[R])(fold: Fold[M, Either[E, R], Either[E, M[R]]]): Either[E, M[R]] = {
    fold(m)(Right(empty))(_.right.map(pure))
  }

  def sequenceOpt[E, R](optV: Option[Either[E, R]]): Either[E, Option[R]] = {
    sequence(optV)(None)(Some.apply)(_.fold)
  }

  implicit class OptionOps[E, R](val self: Option[Either[E, R]]) extends AnyVal {
    def sequence: Either[E, Option[R]] = sequenceOpt(self)
  }

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf","org.wartremover.warts.NonUnitStatements"))
  def sequenceTraversable[E, R, C[_] <: TraversableOnce[_]](v: C[Either[E, R]])(implicit sg:Semigroup[E], cbf: CanBuildFrom[C[Either[E, R]], R, C[R]]): Either[E, C[R]] = {
    val builder = cbf()
    val fl: Either[E, Unit] = v.asInstanceOf[Traversable[Either[E, R]]].foldLeft[Either[E, Unit]](right(())) { (e1, e2) =>
      e1.combine[E, R, Unit](e2){(_, r) => builder += r;()}
    }
    fl.map[C[R]](_ => builder.result())
  }

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitConversion"))
  implicit def toTraversableOnceOps[E, R, C[_] <: TraversableOnce[_]](self: C[Either[E, R]])(implicit sg:Semigroup[E], cbf: CanBuildFrom[C[Either[E, R]], R, C[R]]): TraversableOnceOps[E, R, C] =
    new TraversableOnceOps(self)

  // Try

  def fromTry[R](t: Try[R]): Either[Throwable, R] = t match {
    case Success(v) => right(v)
    case Failure(th) => left(th)
  }

  implicit class TryOps[R](val self: Try[R]) extends AnyVal {
    def toEither: Either[Throwable, R] = fromTry(self)
  }

  // Exception hangling methods

  def catchNonFatal[R](f: => R):Either[Throwable,R] = try{
    right(f)
  }catch{
    case NonFatal(t) => left(t)
  }

  /**
    * Evaluates the specified block, catching exceptions of the specified type and returning them on the left side of
    * the resulting `Either`. Uncaught exceptions are propagated.
    *
    * For example:
    * {{{
    * scala> catchOnly[NumberFormatException] { "foo".toInt }
    * res0: Either[NumberFormatException, Int] = Left(java.lang.NumberFormatException: For input string: "foo")
    * }}}
    */
  def catchOnly[T >: Null <: Throwable]: CatchOnlyPartiallyApplied[T] = new CatchOnlyPartiallyApplied[T]

  // Validation functor

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitConversion"))
  implicit def lift[R1, R2](f: R1 => R2): ValidationFunctor[Nothing, R1, R2] = new ValidationFunctor(right(f))

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitConversion"))
  implicit def validationFunctor[E, R1, R2](f: Either[E, R1 => R2]): ValidationFunctor[E, R1, R2] = new ValidationFunctor(f)

}
