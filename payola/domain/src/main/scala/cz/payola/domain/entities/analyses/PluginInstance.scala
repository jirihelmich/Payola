package cz.payola.domain.entities.analyses

import scala.collection.immutable
import cz.payola.domain.rdf.Graph
import cz.payola.domain.entities.Entity
import cz.payola.domain.entities.analyses.parameters._
import scala.actors.Actor

class PluginInstance(protected val _plugin: Plugin,
    protected val _parameterValues: immutable.Seq[ParameterValue[_]])
    extends Entity with cz.payola.common.entities.analyses.PluginInstance
{
    // TODO cannot create DB Schema with this check
    // require(plugin != null, "Cannot create a plugin instance of a null plugin!")
    /*require(parameterValues.map(_.parameter).sortBy(_.name) == plugin.parameters.sortBy(_.name),
        "The instance doesn't contain parameter instances corresponding to the plugin.") */

    type PluginType = Plugin

    /**
      * Returns value of a parameter with the specified name or [[scala.None.]] if such doesn't exist.
      */
    def getParameter(parameterName: String): Option[Any] = {
        getParameterValue(parameterName).map(_.value)
    }

    /**
      * Returns value of a boolean parameter with the specified name or [[scala.None.]] if such doesn't exist.
      */
    def getBooleanParameter(parameterName: String): Option[Boolean] = {
        getParameter(parameterName).map {
            case value: Boolean => value
        }
    }

    /**
      * Returns value of a float parameter with the specified name or [[scala.None.]] if such doesn't exist.
      */
    def getFloatParameter(parameterName: String): Option[Float] = {
        getParameter(parameterName).map {
            case value: Float => value
        }
    }

    /**
      * Returns value of an integer parameter with the specified name or [[scala.None.]] if such doesn't exist.
      */
    def getIntParameter(parameterName: String): Option[Int] = {
        getParameter(parameterName).map {
            case value: Int => value
        }
    }

    /**
      * Returns value of a string parameter with the specified name or [[scala.None.]] if such doesn't exist.
      */
    def getStringParameter(parameterName: String): Option[String] = {
        getParameter(parameterName).map {
            case value: String => value
        }
    }

    /**
      * Sets value of a parameter with the specified name.
      */
    def setParameter(parameterName: String, value: Any) {
        getParameterValue(parameterName).foreach(i => setParameter(i, value))
    }

    /**
      * Sets value of the specified parameter.
      */
    def setParameter(parameter: Parameter[_], value: Any) {
        getParameterValue(parameter).foreach(i => setParameter(i, value))
    }

    /**
      * Updates the specified parameter value.
      */
    def setParameter(parameterValue: ParameterValue[_], value: Any) {
        parameterValue match {
            case instance: BooleanParameterValue => instance.value = value.asInstanceOf[Boolean]
            case instance: FloatParameterValue => instance.value = value.asInstanceOf[Float]
            case instance: IntParameterValue => instance.value = value.asInstanceOf[Int]
            case instance: StringParameterValue => instance.value = value.asInstanceOf[String]
            case _ =>
        }
    }

    /**
      * Starts evaluation of the plugin instance.
      * @param invoker The invoker of the evaluation.
      * @param inputGraph The input graph.
      * @return An instance of the [[cz.payola.domain.entities.analyses.PluginEvaluation]].
      */
    /*private[entities] def evaluate(invoker: Actor, inputGraph: Graph): PluginEvaluation = {
        val evaluation = new PluginEvaluation(invoker, this, inputGraph)
        evaluation.start()
        evaluation
    }*/

    private def getParameterValue(parameter: Parameter[_]): Option[ParameterValue[_]] = {
        parameterValues.find(_.parameter == parameter)
    }

    private def getParameterValue(parameterName: String): Option[ParameterValue[_]] = {
        plugin.getParameter(parameterName).flatMap(p => getParameterValue(p))
    }
}
