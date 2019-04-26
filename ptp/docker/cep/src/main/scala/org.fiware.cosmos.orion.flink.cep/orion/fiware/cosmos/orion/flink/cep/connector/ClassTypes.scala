package org.fiware.cosmos.orion.flink.cep.connector
/**
  * NgsiEvent
  * @param creationTime Time the event was received (milliseconds since 1970)
  * @param fiwareService Fiware service header
  * @param fiwareServicePath Fiware service path header
  * @param entities List of entities
  */
case class NgsiEvent(creationTime: Long, fiwareService: String ,fiwareServicePath: String, entities: Seq[Entity], subscriptionId: String ) extends Serializable

/**
  * Entity
  * @param id Identification of the entity
  * @param `type` Entity type
  * @param attrs List of attributes
  */
case class Entity(id: String,  `type`: String, attrs: Map[String, Attribute]) extends Serializable

/**
  * HttpBody
  * @param data Data array
  * @param subscriptionId Subscription id
  */
case class HttpBody( data: Seq[Map[String,Any]], subscriptionId: String ) extends Serializable

/**
  * Attribute of an entity
  * @param `type` Type of attribute
  * @param value Value of the attribute
  * @param metadata Metadata map
  */
case class Attribute(`type`: Any, value: Any, metadata:Any ) extends Serializable

/**
  * Helper for converting a map to an attribute object
  */
object MapToAttributeConverter{

  /**
    * Helper for converting a map to an attribute object
    * @param values Map in which the keys are the attribute names and the values are maps with the type, the metadata and the actual value of the attribute
    * @return Attribute object constructed from the map values
    */
  def unapply(values: Map[String,Any]) : Attribute =  {
     Attribute(
       values.get("type").orNull,
       values.get("value").orNull,
       values.get("metadata").orNull)
  }
}

/**
  * Log4j JSON Log
  */
case class Log(timestamp: Float, level: String, host: String, message: String) extends Serializable
case class ExecutionGraph(msg: String)