package com.agilogy.either.extras

import com.agilogy.classis.monoid.Semigroup

class ValidationFunctor[E, R1, R2](self: Either[E, R1 => R2]) {

  def <*>[EE >: E : Semigroup](other: Either[EE, R1]): Either[EE, R2] = self.combine(other)(_.apply(_))  // other.ap[EE,R2](self) //.mapLeft(_.reverse)

}


