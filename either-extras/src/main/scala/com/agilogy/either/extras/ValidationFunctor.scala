package com.agilogy.either.extras

class ValidationFunctor[E, R1, R2](self: Either[List[E], R1 => R2]) {

  def <**>[EE >: E](other: Either[List[EE], R1]): Either[List[EE], R2] = other.ap[List[EE],R2](self,(e1,e2) => e2 ++ e1)

  def <*>[EE >: E](other: Either[EE, R1]): Either[List[EE], R2] = <**>(other.mapLeft(ee => List(ee)))

}


