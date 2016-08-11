package com.agilogy.either.extras

import scala.reflect.ClassTag

final class CatchOnlyPartiallyApplied[T] private[extras] {
  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  def apply[R](f: => R)(implicit CT: ClassTag[T]): Either[T,R] =
    try {
      right(f)
    } catch {
      case t if CT.runtimeClass.isInstance(t) => left(t.asInstanceOf[T])
    }
}
