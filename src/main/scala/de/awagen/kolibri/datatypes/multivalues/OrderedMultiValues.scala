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

package de.awagen.kolibri.datatypes.multivalues

import de.awagen.kolibri.datatypes.io.KolibriSerializable
import de.awagen.kolibri.datatypes.values.OrderedValues


trait OrderedMultiValues extends KolibriSerializable {

  val values: Seq[OrderedValues[Any]]

  /**
    * Remove value with given name.
    *
    * @param valueName
    * @return Tuple, first element being a new instance of OrderedMultiValues with Parameter removed, second being Boolean
    *         with value true if any parameter was removed false if the parameter was not found and thus the
    *         new OrderedMultiValues is just a copy of this here
    */
  def removeValue(valueName: String): (OrderedMultiValues, Boolean)

  def originalValueIndexOf(n: Int): Int = n

  /**
    *
    * @param values  - OrderedValues to add
    * @param prepend - if true, prepend to the list of already contained sequence of OrderedValues, otherwise append
    * @return new OrderedMultiValues object
    */
  def addValue(values: OrderedValues[Any], prepend: Boolean): OrderedMultiValues

  /**
    *
    * @param values  - sequence of OrderedValues to add
    * @param prepend - if true, prepend to the list of already contained sequence of OrderedValues, otherwise append
    * @return
    */
  def addValues(values: Seq[OrderedValues[Any]], prepend: Boolean): OrderedMultiValues

  def addValues(values: OrderedMultiValues, prepend: Boolean): OrderedMultiValues

  def stepsForNthElementStartingFromFirstParam(n: Int): List[(Int, Int)]

  /**
    * provide series of parameter names
    *
    * @return
    */
  def getParameterNameSequence: Seq[String]

  /**
    * Total number of parameter combinations in experiment
    *
    * @return
    */
  def numberOfCombinations: Int

  /**
    * find the nth element in the sequence generated by parameterSeries() call
    *
    * @param n
    */
  def findNthElement(n: Int): Option[Seq[Any]]


  /**
    * Find nrOfElements next elements starting from startElement (in case there are less elements remaining,
    * just return those)
    *
    * @param startElement
    * @param nrOfElements
    */
  def findNNextElementsFromPosition(startElement: Int, nrOfElements: Int): Seq[Seq[Any]]
}
