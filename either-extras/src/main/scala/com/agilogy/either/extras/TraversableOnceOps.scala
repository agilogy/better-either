package com.agilogy.either.extras

import com.agilogy.classis.monoid.Semigroup

import scala.collection.generic.CanBuildFrom
import scala.language.higherKinds

class TraversableOnceOps[E,R,C[_]<:TraversableOnce[_]](protected val self:C[Either[E,R]])(implicit sg:Semigroup[E], cbf:CanBuildFrom[C[Either[E,R]],R,C[R]]) {
  def sequence: Either[E, C[R]] = sequenceTraversable(self)
}


