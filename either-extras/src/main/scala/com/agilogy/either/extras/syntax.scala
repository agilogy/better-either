package com.agilogy.either.extras

import scala.language.implicitConversions

trait EitherExtraSyntax extends HasEitherSyntax{

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitConversion"))
  implicit def eitherExtraSyntax[E,R](e:Either[E,R]):EitherExtraOps[E,R] = new EitherExtraOps[E,R](e)

}

object syntax extends EitherExtraSyntax
