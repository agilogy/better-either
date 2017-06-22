package com.agilogy.either

import scala.util.{Failure, Success, Try}

/**
  * Pimps Either with the right biased operations expected in scala 2.12
  * This is intended as an alternative to Cats and Scalaz alternatives to Either
  * @see https://github.com/scala/scala/pull/5135
  * @see https://github.com/typelevel/cats/issues/1192
  */
class EitherOps[+E, +R](val self: Either[E, R]) extends AnyVal {
  /**
    * Executes the given side-effecting function if this is a `Right`.
    *
    * {{{
    * Right(12).foreach(x => println(x)) // prints "12"
    * Left(12).foreach(x => println(x))  // doesn't print
    * }}}
    *
    * @param f The side-effecting function to execute.
    */
  @SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
  def foreach[U](f: R => U): Unit = self match {
    case Right(v) => f(v);()
    case Left(_) =>
  }

  /**
    * Returns the value from this `Right` or the given argument if this is a `Left`.
    *
    * {{{
    * Right(12).getOrElse(17) // 12
    * Left(12).getOrElse(17)  // 17
    * }}}
    */
  def getOrElse[RR >: R](or: => RR): RR = self match {
    case Right(v) => v
    case Left(_) => or
  }

  /** Returns `true` if self is a `Right` and its value is equal to `elem` (as determined by `==`),
    * returns `false` otherwise.
    *
    * {{{
    *  // Returns true because value of Right is "something" which equals "something".
    *  Right("something") contains "something"
    *
    *  // Returns false because value of Right is "something" which does not equal "anything".
    *  Right("something") contains "anything"
    *
    *  // Returns false because there is no value for Right.
    *  Left("something") contains "something"
    * }}}
    *
    * @param elem the element to test.
    * @return `true` if the option has an element that is equal (as determined by `==`) to `elem`, `false` otherwise.
    */
  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  final def contains[RR >: R](elem: RR): Boolean = self match {
    case Right(v) => v == elem
    case Left(_) => false
  }

  /**
    * Returns `true` if `Left` or returns the result of the application of
    * the given function to the `Right` value.
    *
    * {{{
    * Right(12).forall(_ > 10) // true
    * Right(7).forall(_ > 10)  // false
    * Left(12).forall(_ > 10)  // true
    * }}}
    */
  def forall(f: R => Boolean): Boolean = self match {
    case Right(r) => f(r)
    case Left(_) => true
  }

  /**
    * Returns `false` if `Left` or returns the result of the application of
    * the given function to the `Right` value.
    *
    * {{{
    * Right(12).exists(_ > 10) // true
    * Right(7).exists(_ > 10)  // false
    * Left(12).exists(_ > 10)  // false
    * }}}
    */
  def exists(p: R => Boolean): Boolean = self match {
    case Right(v) => p(v)
    case Left(_) => false
  }

  /**
    * Binds the given function across `Right`.
    *
    * @param f The function to bind across `Right`.
    */
  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  def flatMap[EE >: E, R2](f: R => Either[EE, R2]): Either[EE, R2] = self match {
    case Right(b) => f(b)
    case Left(a) => self.asInstanceOf[Either[EE, R2]]
  }

  /**
    * The given function is applied if this is a `Right`.
    *
    * {{{
    * Right(12).map(x => "flower") // Result: Right("flower")
    * Left(12).map(x => "flower")  // Result: Left(12)
    * }}}
    */
  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  def map[R2](f: R => R2): Either[E, R2] = self match {
    case Right(b) => Right(f(b))
    case Left(a) => self.asInstanceOf[Either[E, R2]]
  }

  /** Returns `Right` with the existing value of `Right` if this is a `Right` and the given predicate `p` holds for the right value,
    * returns `Left(zero)` if this is a `Right` and the given predicate `p` does not hold for the right value,
    * returns `Left` with the existing value of `Left` if this is a `Left`.
    *
    * {{{
    * Right(12).filterOrElse(_ > 10, -1) // Right(12)
    * Right(7).filterOrElse(_ > 10, -1)  // Left(-1)
    * Left(12).filterOrElse(_ > 10, -1)  // Left(12)
    * }}}
    */
  def filterOrElse[EE >: E](p: R => Boolean, zero: => EE): Either[EE, R] = self match {
    case Right(b) => if (p(b)) self else Left(zero)
    case Left(a) => self
  }

  /** Returns a `Seq` containing the `Right` value if
    * it exists or an empty `Seq` if this is a `Left`.
    *
    * {{{
    * Right(12).toSeq // Seq(12)
    * Left(12).toSeq  // Seq()
    * }}}
    */
  def toSeq: collection.immutable.Seq[R] = self match {
    case Right(b) => collection.immutable.Seq(b)
    case Left(_) => collection.immutable.Seq.empty
  }

  /** Returns a `Some` containing the `Right` value
    * if it exists or a `None` if this is a `Left`.
    *
    * {{{
    * Right(12).toOption // Some(12)
    * Left(12).toOption  // None
    * }}}
    */
  def toOption: Option[R] = self match {
    case Right(b) => Some(b)
    case Left(_) => None
  }

  /** Returns a `Success` containing the `Right` value
    * if it exists or a `Failure` if this is a `Left`.
    *
    * {{{
    * Right(12).toTry // Success(12)
    * Left(new RuntimeException("Foo")).toTry  // Failure(new RuntimeException("Foo"))
    * }}}
    */
  def toTry(implicit ev: E <:< Throwable): Try[R] = self match {
    case Right(b) => Success(b)
    case Left(a) => Failure(a)
  }
}

