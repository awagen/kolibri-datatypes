/**
  * Copyright 2021 Andreas Wagenmann
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */

package de.awagen.kolibri.datatypes.collections.generators

import de.awagen.kolibri.datatypes.io.KolibriSerializable
import de.awagen.kolibri.datatypes.types.SerializableCallable.SerializableFunction1

object ByFunctionNrLimitedIndexedGenerator {

  def createFromSeq[T](elem: Seq[T]): ByFunctionNrLimitedIndexedGenerator[T] = {
    ByFunctionNrLimitedIndexedGenerator(elem.size, {
      case e if e >= 0 && e < elem.size => Some(elem(e))
      case _ => None
    })
  }

}

case class ByFunctionNrLimitedIndexedGenerator[+T](nrOfElements: Int,
                                                   genFunc: SerializableFunction1[Int, Option[T]]) extends IndexedGenerator[T] with KolibriSerializable {

  override def getPart(startIndex: Int, endIndex: Int): IndexedGenerator[T] = {
    assert(startIndex >= 0)
    val end: Int = math.min(nrOfElements, endIndex)
    val newSize = end - startIndex
    val genFunc: SerializableFunction1[Int, Option[T]] = x => this.get(x + startIndex)
    ByFunctionNrLimitedIndexedGenerator(newSize, genFunc)
  }

  override def mapGen[B](f: SerializableFunction1[T, B]): IndexedGenerator[B] = {
    new ByFunctionNrLimitedIndexedGenerator[B](nrOfElements, x => genFunc(x).map(f))
  }


  override def get(index: Int): Option[T] = index match {
    case e if e < nrOfElements => genFunc.apply(e)
    case _ => None
  }

}
