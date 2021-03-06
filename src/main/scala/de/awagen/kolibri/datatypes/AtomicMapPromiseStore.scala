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

package de.awagen.kolibri.datatypes

import java.util.concurrent.atomic.AtomicReference

import scala.concurrent.{ExecutionContext, Future, Promise}

/**
  * Implementation ensuring thread safety of the value storage and also ensuring
  * that no more than one request leads to the creation of the stored resource which
  * could potentially be expensive (e.g in case multiple experiment batch processing
  * actors on a single node try to request the ressource at once)
  * E.g used to load some data expensive to load within an object to have only one data-instance per node
 *
  * @tparam U - the key used to identify a value
  * @tparam V - the corresponding value
  */
trait AtomicMapPromiseStore[U,V]  {

  private[this] val valueMap: AtomicReference[Map[U, Promise[V]]] = new AtomicReference(Map.empty)

  private[this] def loadAndStoreValues(key: U)(implicit ec: ExecutionContext): Promise[V] = {
    var currentMap: Map[U, Promise[V]] = valueMap.get()
    var didSetPromise: Boolean = false
    while (!currentMap.keySet.contains(key)){
      didSetPromise = valueMap.compareAndSet(currentMap, currentMap + (key -> Promise[V]()))
      if (didSetPromise) valueMap.get()(key).completeWith(Future{calculateValue(key)})
      currentMap = valueMap.get()
    }
    valueMap.get()(key)
  }

  def retrieveValue(key: U)(implicit ec: ExecutionContext): Promise[V] = {
    if (!valueMap.get().contains(key)) loadAndStoreValues(key)
    else valueMap.get()(key)
  }

  def remove(key: U): Unit = {
    var removedMapEntry: Boolean = false
    var currentMap = valueMap.get()
    while (currentMap.contains(key)){
      removedMapEntry = valueMap.compareAndSet(currentMap, currentMap - key)
      currentMap = valueMap.get()
    }
  }

  def clearAll(): Unit = {
    var currentMap: Map[U, Promise[V]] = valueMap.get()
    var didRemoveAll: Boolean = false
    while (currentMap.nonEmpty && !didRemoveAll){
      didRemoveAll = valueMap.compareAndSet(currentMap, Map.empty)
      currentMap = valueMap.get()
    }

  }

  def contains(key: U): Boolean = {
    valueMap.get().contains(key)
  }

  def calculateValue(key: U): V
}
