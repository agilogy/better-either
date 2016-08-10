package com.agilogy.either

import scala.collection.generic.CanBuildFrom
import scala.language.{higherKinds, implicitConversions}
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}

package object extras {

  type Fold[M[_], a, b] = (M[a]) => (=> b) => (a => b) => b

  def sequenceOpt[E, R](optV: Option[Either[E, R]]): Either[E, Option[R]] = {
    sequence(optV)(None)(Some.apply)(_.fold)
    //      optV.fold[Either[E,Option[R]]](Right(None))(_.map(Some.apply))
  }

  // Sequence

  def sequence[M[_], E, R](m: M[Either[E, R]])(empty: M[R])(pure: R => M[R])(fold: Fold[M, Either[E, R], Either[E, M[R]]]): Either[E, M[R]] = {
    fold(m)(Right(empty))(_.right.map(pure))
  }

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf","org.wartremover.warts.NonUnitStatements"))
  def sequenceTraversable[E, R, C[_] <: TraversableOnce[_]](v: C[Either[E, R]])(appendE: (E, E) => E)(implicit cbf: CanBuildFrom[C[Either[E, R]], R, C[R]]): Either[E, C[R]] = {
    import com.agilogy.either.extras.syntax._
    val builder = cbf()
    val fl: Either[E, Unit] = v.asInstanceOf[Traversable[Either[E, R]]].foldLeft[Either[E, Unit]](right(())) { (e1, e2) =>
      e1.combine[E, R, Unit](e2, appendE, (_, r) => builder += r)
    }
    fl.map[C[R]](_ => builder.result())
    //
    //
    //    v.asInstanceOf[Traversable[Either[E,R]]].foldLeft[Either[E,C[R]]](right(cbf.apply().result())) { (e1, e2) =>
    //      e1.combine[E,R,Seq[R]](e2,appendE,_. :+ _)
    //    }
  }

  def right[R](r: R): Either[Nothing, R] = Right(r)

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitConversion"))
  implicit def toTraversableOnceOps[E, R, C[_] <: TraversableOnce[_]](self: C[Either[E, R]])(implicit cbf: CanBuildFrom[C[Either[E, R]], R, C[R]]): TraversableOnceOps[E, R, C] =
    new TraversableOnceOps(self)

  //  def sequenceTraversable[E,R](v:TraversableOnce[Either[E,R]])(appendE:(E,E) => E):Either[E,Seq[R]] = {
  //    import com.agilogy.either.extras.syntax._
  //    v.foldLeft[Either[E,Seq[R]]](right(Seq.empty)) { (e1, e2) =>
  //      e1.combine[E,R,Seq[R]](e2,appendE,_ :+ _)
  //    }
  //  }

  def fromTry[R](t: Try[R]): Either[Throwable, R] = t match {
    case Success(v) => right(v)
    case Failure(th) => left(th)
  }

  def left[E](e: E): Either[E, Nothing] = Left(e)

  //  def sequenceTL[E,R,C[_], That](v:TraversableLike[Either[E,R],C[Either[E,R]]])(implicit cbf:CanBuildFrom[C[Either[E,R]],Either[E,R],C[R]]):That = {
  //    v.fold[Either[E,C[R]]](right(cbf().result()))()
  //  }

  implicit class OptionOps[E, R](val self: Option[Either[E, R]]) extends AnyVal {
    def sequence: Either[E, Option[R]] = sequenceOpt(self)
  }

  implicit class TryOps[R](val self: Try[R]) extends AnyVal {
    def toEither: Either[Throwable, R] = fromTry(self)
  }

  def catchNonFatal[R](f: => R):Either[Throwable,R] = try{
    right(f)
  }catch{
    case NonFatal(t) => left(t)
  }


}
