package com.agilogy.either.extras.unit

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.FunSpec

import scala.util.{Failure, Success}

class EitherExtrasSyntaxTest extends FunSpec with TestSamples with TypeCheckedTripleEquals {

  describe("com.agilogy.either.extras.syntax") {

    import com.agilogy.either.extras.syntax._

    it("should import EitherOps with a single import of com.agilogy.either.extras.syntax._") {
      assert(ok.map(_ + 1) === Right(okV + 1))
    }

    it("should implement mapBoth") {
      assert(ok.mapBoth(appendErrors, _ + 1) === Right(okV + 1))
      assert(err.mapBoth(appendErrors, _ + 1) === Left(errMsg))
    }

    it("should implement mapRight as an alias of map") {
      assert(ok.mapRight(_ + 1) === Right(okV + 1))
      assert(err.mapRight(_ + 1) === err)
    }

    it("should implement mapLeft") {
      assert(ok.mapLeft(appendErrors) === Right(okV))
      assert(err.mapLeft(appendErrors) === Left(errMsg))
    }

    it("should implement ap to accumulate errors") {
      val okF: Either[List[ErrorMessage], Int => Int] = Right(_ + 1)
      val errV1 = ErrorMessage("Not a valid int operand &&")
      val errF: Either[List[ErrorMessage], Int => Int] = Left(List(errV1))
      assert(ok.ap(okF) === Right(okV + 1))
      assert(err.ap(okF) === err)
      assert(ok.ap(errF) === Left(List(errV1)))
      assert(err.ap(errF) === Left(List(errV0, errV1)))
    }

    it("should implement excepting") {
      assert(ok.excepting { case i if i % 2 == 0 => List(ErrorMessage("must not be even")) } === ok)
      assert(ok.excepting { case i if i % 2 == 1 => errV2 } === Left(errV2))
      assert(err.excepting { case i if i % 2 == 1 => errV2 } === err)
    }

    it("should implement ensure") {
      assert(ok.ensure(_ % 2 == 0)(errV2) === Left(errV2))
      assert(Right(4).ensure(_ % 2 == 0)(errV2) === Right(4))
      assert(err.ensure(_ % 2 == 0)(errV2) === err)
    }

    it("should implement orElse") {
      assert(ok.orElse(Right(6)) === ok)
      assert(err.orElse(Right(6)) === Right(6))
      assert(err.orElse(Left(errV2)) === Left(errV2))
    }

    it("should implement getWith with a function") {
      assert(ok.getWith(_.size) === okV)
      assert(err.getWith(_.map(_.msg.size).sum) === errMsg.size)
    }

    it("should implement combine") {
      assert(ok.combine(ok2)(sum) === Right(okV + okV2))
      assert(ok.combine(err2)(sum) === err2)
      assert(err.combine(ok2)(sum) === err)
      assert(err.combine(err2)(sum) === Left(errV ++ errV2))
    }

    it("should implement product") {
      assert(ok.product(ok2) === Right((okV, okV2)))
      assert(ok.product(err2) === Left(errV2))
      assert(err.product(ok2) === Left(errV))
      assert(err.product(err2) === Left(errV ++ errV2))
    }

    it("should implement toTry with arbitrary left values (and a throwable constructor)") {
      case class ValidationException(errors: List[ErrorMessage]) extends Exception
      assert(ok.toTryAs(ValidationException) === Success(okV))
      assert(err.toTryAs(ValidationException) === Failure(ValidationException(errV)))
    }

    it("should implement toTry with throwable left values") {
      case class ValidationException(error: ErrorMessage) extends Exception
      val okTh:Either[ValidationException,Int] = Right(okV)
      val exc = new ValidationException(ErrorMessage(errMsg))
      val errTh:Either[ValidationException,Int] = Left(exc)
      assert(okTh.toTry === Success(okV))
      assert(errTh.toTry === Failure(ValidationException(ErrorMessage(errMsg))))
    }

    it("should implement showAs") {
      assert(ok.showAs(_.toString(), _.toString) === okV.toString)
      assert(err.showAs(_.toString(), _.toString) === errV.toString)
    }

    // Unsupported cats.Validated methods:
    //    toList
    //    traverse
    //    foldLeft
    //    foldRight

    //    withXor, toXor
    //    Bitraverse methods
    //    Eq methods (when E:Eq, R:Eq)
    //    Order methods (when E:Order, R:Order)
    //    PartialOrder methods (when E:PartialOrder, R:PartialOrder)

    //    Show methods (when E:Show, R:Show)
    //    Monoid methods (when E:Semigroup, R:Monoid)
    //    SemigroupK methods (when E:Semigroup)
    //    Semigroup methods (when E:Semigroup, R:Semigroup)
    //    Traverse methods (when E:Semigroup)
    //    ApplicativeError methods (when E:Semigroup)


    // Changed cats.Validated methods:
    //    bimap (called mapBoth)
    //    andThen (called flatMap)


    // Changed scalaz methods:
    //    valueOr (called getOrElse)
  }

}
