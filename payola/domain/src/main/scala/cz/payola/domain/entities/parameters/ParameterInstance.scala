package cz.payola.domain.entities.parameters

import cz.payola._
import domain.entities.generic.{ConcreteNamedEntity, ConcreteEntity}
import sun.reflect.generics.reflectiveObjects.NotImplementedException

/** A protected class that represents the parameter instance with an actual value. As this,
  * it is abstract, see subclasses.
  *
  * @param id Entity ID.
  * @param _parameter Parameter of which this is an instance.
  * @param _value Value of the parameter.
  * @tparam A Type of the value.
  */
abstract class ParameterInstance[A](
        id: String  = java.util.UUID.randomUUID.toString,
        protected val _parameter: Parameter[A],
        protected var _value: A)
    extends ConcreteEntity(id)
    with common.entities.ParameterInstance[A]
{
    type ParameterType = Parameter[A]

    /** Gets a boolean value of the parameter.
      *
      * @return Boolean value, or false if the value is null.
      */
    def booleanValue: Boolean = {
        throw new NotImplementedException()
    }

    /** Gets a float value of the parameter.
      *
      * @return Float value, or 0.0 if the value is null.
      */
    def floatValue: Float = {
        throw new NotImplementedException()
    }

    /** Gets an int value of the parameter.
      *
      * @return Int value, or 0 if the value is null.
      */
    def intValue: Int = {
        throw new NotImplementedException()
    }

    /** Boolean value setter.
      *
      * @param bval Boolean value.
      */
    def setBooleanValue(bval: Boolean): Unit = {
        throw new NotImplementedException()
    }

    /** Int value setter.
      *
      * @param ival Int value.
      */
    def setIntValue(ival: Int): Unit = {
        throw new NotImplementedException()
    }

    /** Float value setter.
      *
      * @param fval Float value.
      */
    def setFloatValue(fval: Float): Unit = {
        throw new NotImplementedException()
    }

    /** String value setter.
      *
      * @param strval String value.
      */
    def setStringValue(strval: String): Unit = {
        throw new NotImplementedException()
    }

    /** Gets a string value of the parameter.
      *
      * @return String value, or "" if the value is null.
      */
    def stringValue: String = {
        throw new NotImplementedException()
    }

    /** Only a convenience method that calls value_=().
      *
      * @param newVal The new value.
      */
    def setValue(newVal: A) = _value = newVal
}
