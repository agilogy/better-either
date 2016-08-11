package com.agilogy.either

import org.scalatest.FunSpec

import scala.collection.mutable
import scala.util.{Failure, Success}

@SuppressWarnings(Array("org.wartremover.warts.Equals"))
class EitherSyntaxTest extends FunSpec {

  describe("com.agilogy.either.syntax") {

    import com.agilogy.either.syntax._

    trait Failure
    case class SomeFailure(msg:String) extends Failure

    val okv = 23
    val ok: Either[Failure, Int] = Right(okv)
    val errV = SomeFailure("a is not an int")
    val err: Either[Failure, Int] = Left(errV)

    it("should implement foreach") {
      val b = new mutable.ListBuffer[Int]
      ok.foreach(b += _)
      err.foreach(b += _)
      assert(b === Seq(okv))
    }

    it("should implement getOrElse") {
      assert(ok.getOrElse(42) === okv)
      assert(err.getOrElse(42) === 42)
    }

    it("should implement contains") {
      assert(ok.contains(okv) === true)
      assert(ok.contains(42) === false)
      assert(err.contains[Any](errV) === false)
    }

    it("should implement forall") {
      assert(ok.forall(_ == okv) === true)
      assert(ok.forall(_ == 42) === false)
      assert(err.forall(_ > 1000) === true)
    }

    it("should implement exists") {
      assert(ok.exists(_ == okv) === true)
      assert(ok.exists(_ == 42) === false)
      assert(err.exists(_ => true) === false)
    }

    it("should implement flatMap") {
      case class NotEven(i: Int) extends Failure
      def incEven(i: Int): Either[NotEven, Int] = if (i % 2 == 0) Right(i + 1) else Left(NotEven(i))
      val res: Either[NotEven, Int] = Right(2).flatMap(incEven)
      assert(res === Right(3)) // Either[Failure,Int]
      assert(Right(3).flatMap(incEven) === Left(NotEven(3))) // Either[Failure,Int]
      assert(err.flatMap(incEven) === err)  // Either[Failure,Int]
    }

    it("should implement map"){
      assert(ok.map(_ + 1) === Right(okv + 1))
      assert(err.map(_ + 1) === err)
    }

    it("should implement filterOrElse"){
      case object NotDivisibleBy3
      assert(Right(1).filterOrElse(_ % 3 == 0, NotDivisibleBy3) === Left(NotDivisibleBy3))
      assert(Right(3).filterOrElse(_ % 3 == 0, NotDivisibleBy3) === Right(3))
      assert(err.filterOrElse(_ % 3 == 0, NotDivisibleBy3) === err)
    }

    it("should implement toSeq"){
      assert(ok.toSeq === Seq(okv))
      assert(err.toSeq === Seq.empty)
    }

    it("should implement toOption"){
      assert(ok.toOption === Some(okv))
      assert(err.toOption === None)
    }

    it("should *not* implement toTry if Left is not a Throwable"){
      assertDoesNotCompile("ok.toTry")
    }

    it("should implement toTry if Left is a Throwable"){
      case class FailureException(msg:String) extends Throwable(msg)
      val okTh:Either[FailureException,Int] = Right(23)
      val f = new FailureException("a is not an int")
      val errTh:Either[FailureException,Int] = Left(f)
      assert(okTh.toTry === Success(23))
      assert(errTh.toTry === Failure(f))
    }

  }

}
