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

import de.awagen.kolibri.datatypes.types.SerializableCallable
import de.awagen.kolibri.datatypes.types.SerializableCallable.SerializableFunction2
import de.awagen.kolibri.datatypes.utils.PermutationUtils


/**
  * Merge two generators applying a mergeFunction on the distinct types
  * to retrieve the respective element. Behavior is such that
  * the combinations of generator1 and generator2 are permutated and on
  * calculation of elements from both generators those are mapped to needed
  * type via the mergeFunc
  * @param generator1: IndexedGenerator[A]
  * @param generator2: IndexedGenerator[B]
  * @param mergeFunc: SerializableFunction2[A, B, C]
  * @tparam A: type of elements of generator1
  * @tparam B: type of elements of generator2
  * @tparam C: type of elements generated by this generator
  */
case class MergingIndexedGenerator[A, B, C](generator1: IndexedGenerator[A],
                                            generator2: IndexedGenerator[B],
                                            mergeFunc: SerializableFunction2[A, B, C]) extends IndexedGenerator[C] {
  override val nrOfElements: Int = generator1.size * generator2.size

  /**
    * create generator that only generates a part of the original generator.
    *
    * @param startIndex : startIndex (inclusive)
    * @param endIndex   : endIndex (exclusive)
    * @return generator generating the subpart of the generator as given by startIndex and endIndex
    */
  override def getPart(startIndex: Int, endIndex: Int): IndexedGenerator[C] = {
    assert(startIndex >= 0 && startIndex < nrOfElements)
    val end: Int = math.min(nrOfElements, endIndex)
    ByFunctionNrLimitedIndexedGenerator(end - startIndex, x => get(startIndex + x))
  }

  /**
    * Get the index-th element
    *
    * @param index
    * @return
    */
  override def get(index: Int): Option[C] = {
    val nthElementIndices: Option[Seq[Int]] = PermutationUtils.findNthElementForwardCalc(Seq(generator1.size, generator2.size),
      index)
    nthElementIndices.flatMap(x => {
      for {
        el1 <- generator1.get(x.head)
        el2 <- generator2.get(x(1))
      } yield mergeFunc.apply(el1, el2)
    })
  }

  /**
    * Provided a mapping function, create generator of new type where elements are created by current generator
    * and then mapped by the provided function
    *
    * @param f : mapping function
    * @tparam B : the type the original element type is mapped to
    * @return : new generator providing the new type
    */
  override def mapGen[D](f: SerializableCallable.SerializableFunction1[C, D]): IndexedGenerator[D] = {
    new ByFunctionNrLimitedIndexedGenerator[D](nrOfElements, x => get(x).map(f))
  }
}