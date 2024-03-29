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

package de.awagen.kolibri.datatypes.io.json

import de.awagen.kolibri.datatypes.io.json.OrderedValuesJsonProtocol._
import de.awagen.kolibri.datatypes.multivalues
import de.awagen.kolibri.datatypes.multivalues.{GridOrderedMultiValues, GridOrderedMultiValuesBatch, OrderedMultiValues}
import de.awagen.kolibri.datatypes.values.OrderedValues
import spray.json.{DefaultJsonProtocol, DeserializationException, JsValue, JsonFormat, RootJsonFormat}


object OrderedMultiValuesJsonProtocol extends DefaultJsonProtocol {

  implicit object OrderedMultiValuesAnyFormat extends JsonFormat[OrderedMultiValues] {
    override def read(json: JsValue): OrderedMultiValues = json match {
      case spray.json.JsObject(fields) => fields("type").convertTo[String] match {
        case "GRID_FROM_VALUES_SEQ" =>
          val values: Seq[OrderedValues[_]] = fields("values").convertTo[Seq[OrderedValues[_]]]
          multivalues.GridOrderedMultiValues(values)
        case "GRID_BATCH_FROM_VALUES_SEQ" =>
          multivalues.GridOrderedMultiValuesBatch(multivalues.GridOrderedMultiValues(fields("multiValues").asJsObject.getFields("values").head.convertTo[Seq[OrderedValues[_]]]),
            fields("batchSize").convertTo[Int], fields("batchNr").convertTo[Int])
        case e =>  throw DeserializationException(s"Expected a valid type for OrderedMultiValues but got $e")
      }
      case e =>  throw DeserializationException(s"Expected a value for OrderedMultiValues but got $e")
    }

    override def write(obj: OrderedMultiValues): JsValue = obj match {
      case e: GridOrderedMultiValues => gridOrderedMultiValuesFormat.write(e)
      case e: GridOrderedMultiValuesBatch => gridOrderedMultiValuesBatchFormat.write(e)
    }
  }


  implicit def gridOrderedMultiValuesFormat: RootJsonFormat[GridOrderedMultiValues] =
    jsonFormat(GridOrderedMultiValues, "values")

  implicit def gridOrderedMultiValuesBatchFormat: RootJsonFormat[GridOrderedMultiValuesBatch] =
    jsonFormat(GridOrderedMultiValuesBatch, "multiValues", "batchSize", "batchNr")

}
