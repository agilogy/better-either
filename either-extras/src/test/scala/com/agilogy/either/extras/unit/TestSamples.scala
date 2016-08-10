package com.agilogy.either.extras.unit

trait TestSamples {

  trait Failure
  case class ErrorMessage(msg: String) extends Failure

  type EitherErrsInt = Either[List[ErrorMessage],Int]

  val okV = 23
  val ok: Either[List[ErrorMessage], Int] = Right(okV)
  val errMsg = "a is not an int"
  val errV0 = ErrorMessage(errMsg)
  val errV = List(errV0)
  val err: Either[List[ErrorMessage], Int] = Left(errV)

  val okV2 = 42
  val ok2 = Right(okV2)
  val errV2 = List(ErrorMessage("must not be odd"))
  val err2: Either[List[ErrorMessage], Int] = Left(errV2)

  def appendErrors(l: List[ErrorMessage]): String = l.map(_.msg).mkString(",")
  def append[T](l: List[T], r: List[T]): List[T] = l ++ r
  val sum: (Int, Int) => Int = _ + _

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def boom(exc:Throwable):Unit = {
    throw exc
  }

}
