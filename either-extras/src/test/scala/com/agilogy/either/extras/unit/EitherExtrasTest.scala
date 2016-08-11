package com.agilogy.either.extras.unit

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.FunSpec

import scala.util.{Failure, Success, Try}

class EitherExtrasTest extends FunSpec with TypeCheckedTripleEquals with TestSamples{

  describe("com.agilogy.either.extras") {

    import com.agilogy.either.extras._

    it("should implement sequence for Option[Either[E,R]] and sequenceOpt") {
      assert(sequenceOpt(Option(ok)) === Right(Option(okV)))
      assert(Option(ok).sequence === Right(Option(okV)))
      assert(sequenceOpt(Option(err)) === Left(errV))
      assert(Option(err).sequence === Left(errV))
      val empty = Option.empty[Either[List[ErrorMessage], Int]]
      assert(sequenceOpt(empty) === Right(None))
      assert(empty.sequence === Right(None))
    }

    it("should implement sequence for TraversableOnce[Either[E,R]] and sequenceOpt") {
      val oks = Seq(ok,ok2)
      val mixed = Seq(ok,err)
      val errs = Seq(err,err2)
      assert(sequenceTraversable(oks)(_ ++ _) === right(Seq(okV,okV2)))
      assert(oks.sequence(_ ++ _) === right(Seq(okV,okV2)))
      assert(sequenceTraversable(mixed)(_ ++ _) === left(errV))
      assert(mixed.sequence(_ ++ _) === left(errV))
      assert(sequenceTraversable(errs)(_ ++ _) === left(errV ++ errV2))
      assert(errs.sequence(_ ++ _) === left(errV ++ errV2))

      assert(Set(ok, ok2).sequence(_ ++ _) === right(Set(okV,okV2)))
    }

    it("should provide right and left constructors returning supertype Either"){
      assertDoesNotCompile("val r:Right[Nothing,Int] = right(23)")
      assertCompiles("val r:Either[Nothing,Int] = right(23)")
      assertDoesNotCompile("""val l:Left[String,Nothing] = left("boom!")""")
      assertCompiles("""val l:Either[String,Nothing] = left("boom!")""")
    }

    it("should implement conversion from Try"){
      assert(fromTry(Success(23)) === right(23))
      assert(Success(23).toEither === right(23))
      val exc = new IllegalArgumentException("Boom!")
      assert(fromTry(Failure[Int](exc)) === left(exc))
      assert(Failure[Int](exc).toEither === left(exc))
    }

    it("should catch non fatal") {
      val exc = new IllegalArgumentException("boom!")
      assert(catchNonFatal(boom(exc)) === left(exc))
    }

    it("should cachOnly"){
      val exc = new IllegalArgumentException("boom")
      assert(catchOnly[IllegalArgumentException](boom(exc)) === left(exc))
    }

    it("should lift a function to accumulate errors while applying validated arguments") {
      def f(a: Int, b: Int): Int = a + b
      val fc = (f _).curried

      assert((lift(fc) <**> ok <**> ok) === right(okV + okV))
      assert((lift(fc) <**> err <**> ok) === err)
      assert((lift(fc) <**> err <**> err2) === left(errV ++ errV2))

      val okV3 = 67
      val simpleOk:Either[ErrorMessage,Int] = right(okV3)
      val errV3 = ErrorMessage("errMsg3")
      val simpleErr:Either[ErrorMessage,Int] = left(errV3)

      assert((lift(fc) <*> simpleOk <**> ok) === right(okV3 + okV))
      assert((lift(fc) <**> ok <*> simpleOk) === right(okV3 + okV))
      assert((lift(fc) <**> err <*> simpleOk) === err)
      assert((lift(fc) <*> simpleOk <**> err) === err)
      assert((lift(fc) <**> ok <*> simpleErr) === left(List(errV3)))
    }
  }
}
