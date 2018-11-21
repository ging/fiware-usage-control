package org.fiware.cosmos.orion.flink.cep

import org.apache.flink.cep.EventComparator
import org.fiware.cosmos.orion.flink.connector.Entity

case class ComparableEntity(entity: Entity)   {
  def canEqual(a: Any) = a.isInstanceOf[ComparableEntity]
   override def equals(that : Any) : Boolean=
     that match {
       case that: Entity =>  that.canEqual(this) && this.hashCode == that.hashCode
       case _ => false
     }

  override def hashCode(): Int = {
    val entity = this.entity
    entity.id.hashCode() + entity.`type`.hashCode() + entity.attrs.hashCode()
  }
}
