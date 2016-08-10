package com.agilogy.either

import scala.language.implicitConversions

trait EitherSyntax{

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitConversion"))
  implicit def eitherSyntax[E,R](e:Either[E,R]):EitherOps[E,R] = new EitherOps(e)

}

object syntax extends EitherSyntax
