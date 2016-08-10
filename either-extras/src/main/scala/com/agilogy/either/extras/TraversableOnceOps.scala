package com.agilogy.either.extras

import scala.collection.generic.CanBuildFrom
import scala.language.higherKinds

class TraversableOnceOps[E,R,C[_]<:TraversableOnce[_]](protected val self:C[Either[E,R]])(implicit cbf:CanBuildFrom[C[Either[E,R]],R,C[R]]) {
  def sequence(appendE:(E,E) => E): Either[E, C[R]] = sequenceTraversable(self)(appendE)
}


