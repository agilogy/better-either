package com.agilogy.either.extras

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.FunSpec

import scala.util.{Failure, Success}

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
      assert(sequenceTraversable(oks) === right(Seq(okV,okV2)))
      assert(oks.sequence === right(Seq(okV,okV2)))
      assert(sequenceTraversable(mixed) === left(errV))
      assert(mixed.sequence === left(errV))
      assert(sequenceTraversable(errs) === left(errV ++ errV2))
      assert(errs.sequence === left(errV ++ errV2))

      assert(Set(ok, ok2).sequence === right(Set(okV,okV2)))
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
      val res = intercept[NumberFormatException](catchOnly[NullPointerException]("foo".toInt))
      assert(res.getMessage === """For input string: "foo"""")
    }

    it("should lift a function to accumulate errors while applying validated arguments") {
      def f(a: Int, b: Int): Int = a + b
      val fc = (f _).curried

      assert((fc <*> ok <*> ok) === right(okV + okV))
      assert((fc <*> err <*> ok) === err)
      assert((fc <*> err <*> err2) === left(errV ++ errV2))

      def f2(a:Int, b:Int, c:Int):Int = a + b + c
      assert(((f2 _).curried <*> err <*> err <*> err2) === left(errV ++ errV ++ errV2))

      val okV3 = 67
      val simpleOk:Either[ErrorMessage,Int] = right(okV3)
      val errV3 = ErrorMessage("errMsg3")
      val simpleErr:Either[ErrorMessage,Int] = left(errV3)

      assert((fc <*> simpleOk.accList <*> ok) === right(okV3 + okV))
      assert((fc <*> ok <*> simpleOk.accList) === right(okV3 + okV))
      assert((fc <*> err <*> simpleOk.accList) === err)
      assert((fc <*> simpleOk.accList <*> err) === err)
      assert((fc <*> ok <*> simpleErr.accList) === left(List(errV3)))
    }

    it("should lift a function returning Either") {
      def f(a:Int, b:Int):Either[List[ErrorMessage], Int] = {
        right(())
          .ensure(_ => b != 0)(ErrorMessage("dividend must be non zero"))
          .ensure(_ => a % b == 0)(ErrorMessage("division is not exact"))
          .map(_ => a / b)
          .accList
      }

      val fc = (f _).curried
      val res: Either[List[ErrorMessage], Either[List[ErrorMessage], Int]] = fc <*> ok <*> ok
      assert(res.flatten === right(1))
    }
  }
}
