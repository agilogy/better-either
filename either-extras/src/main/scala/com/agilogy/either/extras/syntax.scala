package com.agilogy.either.extras

import com.agilogy.either.EitherSyntax

import scala.language.implicitConversions

trait EitherExtraSyntax extends EitherSyntax{

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitConversion"))
  implicit def eitherExtraSyntax[E,R](e:Either[E,R]):EitherExtraOps[E,R] = new EitherExtraOps[E,R](e)

}

object syntax extends EitherExtraSyntax
