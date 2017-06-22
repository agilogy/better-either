package com.agilogy.either.extras

import scala.util.{Failure, Success, Try}
import com.agilogy.classis.monoid._

class EitherExtraOps[+E,+R](val self:Either[E,R]) extends AnyVal{

  def flatten[EE >: E, R2](implicit ev: R <:< Either[EE,R2]):Either[EE,R2] = self.flatMap(ev)

  def mapBoth[E2, R2](fe: E => E2, fr: R => R2): Either[E2, R2] = self match {
    case Left(e) => Left(fe(e))
    case Right(r) => Right(fr(r))
  }

  def mapRight[R2](f: R => R2): Either[E, R2] = self.right.map(f)

  def mapLeft[E2](f: E => E2): Either[E2, R] = self.left.map(f)

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  def ap[EE >: E :Semigroup, R2](vf: Either[EE, R => R2]): Either[EE, R2] = (self, vf) match {
    case (Right(v), Right(f)) => Right(f(v))
    case (e@Left(_), Right(_)) => e.asInstanceOf[Either[EE,R2]]
    case (Right(_), e@Left(_)) => e.asInstanceOf[Either[EE,R2]]
    case (Left(e1), Left(e2)) => Left(Semigroup[EE].append(e1,e2))
  }

//  def fold[R2](z: => R2)(f: (R, => R2) => R2): R2 = self.fold[R2](_ => z, r => f(r, z))

  def excepting[EE >: E](pf: PartialFunction[R, EE]): Either[EE, R] = self match {
    case Right(v) => pf.lift(v).fold[Either[EE, R]](Right(v))(e => Left(e))
    case _ => self
  }

  def ensure[EE >: E](f: R => Boolean)(onFailure: => EE): Either[EE, R] = self match {
    case Right(r) if f(r) => right(r)
    case Right(_) => left(onFailure)
    case l@Left(_) => l
  }

  def orElse[EE >: E, RR >: R](v: => Either[EE, RR]): Either[EE, RR] = self match {
    case Left(e) => v
    case r@Right(_) => r
  }

  /**
    * Returns the value from this `Right` or the result of applying `f to the contents of the `Left`.
    *
    * {{{
    * (Right("12"):Either[List[String],String]).getWith(_.mkString) // "12"
    * (Left(List("error1","error2")):Either[List[String],String]).getWith(_.mkString)  // "error1error2"
    * }}}
    */
  def getWith[RR >: R](fe: E => RR):RR = self match{
    case Right(r) => r
    case Left(e) => fe(e)
  }

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  def combine[EE >: E : Semigroup, R2, R3](other: Either[EE, R2])(appendR: (R, R2) => R3): Either[EE, R3] = (self, other) match {
    case (Right(r1), Right(r2)) => Right(appendR(r1, r2))
    case (e@Left(_), Right(_)) => e.asInstanceOf[Either[EE,R3]]
    case (Right(_), e@Left(_)) => e.asInstanceOf[Either[EE,R3]]
    case (Left(e1), Left(e2)) => Left(Semigroup[EE].append(e1,e2))
  }

  def product[EE >: E : Semigroup, R2](other: Either[EE, R2]): Either[EE, (R,R2)] = combine[EE,R2,(R,R2)](other)(_ -> _)

  def toTryAs(f:E => Throwable): Try[R] = self match {
    case Left(e) => Failure(f(e))
    case Right(r) => Success(r)
  }

  def showAs(eToString: E => String, rToString: R => String): String = self match {
    case Left(e) => eToString(e)
    case Right(r) => rToString(r)
  }

  def accList[EE>:E]:Either[List[EE],R] = mapLeft(e => List(e))

}
