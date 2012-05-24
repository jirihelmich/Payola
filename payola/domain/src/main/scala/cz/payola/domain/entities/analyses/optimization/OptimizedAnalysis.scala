package cz.payola.domain.entities.analyses.optimization

import cz.payola.domain.entities.Analysis
import cz.payola.domain.entities.analyses.PluginInstance
import scala.collection._

class OptimizedAnalysis(analysis: Analysis) extends Analysis(analysis.name, analysis.owner)
{
    /**
      * A map that for each plugin instance I contains a sequence of plugin instances that the instance I replaced
      * during the analysis optimization. If the instance I didn't replace any plugin, then the sequence contains only
      * the instance I.
      */
    val originalInstances: mutable.Map[PluginInstanceType, Seq[PluginInstanceType]] = analysis match {
        case optimizedAnalysis: OptimizedAnalysis => optimizedAnalysis.originalInstances.clone()
        case _ => mutable.Map.empty
    }

    // Clone the plugin instances and bindings.
    analysis.pluginInstances.foreach(instance => addPluginInstance(instance))
    analysis.pluginInstanceBindings.foreach(binding => addBinding(binding))

    def allOriginalInstances: immutable.Seq[PluginInstanceType] = {
        originalInstances.values.flatten.toList
    }

    /**
      * Replaces the specified old instances with the new instance, but doesn't take any care of the bindings.
      * It just removes the old instances from the analysis and adds the new one.
      * @param newInstance The new instance that should replace the old plugin instances.
      * @param oldInstances The plugin instances to replace.
      */
    def replaceInstances(newInstance: PluginInstanceType, oldInstances: PluginInstanceType*) {
        // Store the original instances before removing.
        val oldOriginalInstances = oldInstances.flatMap(i => originalInstances.getOrElse(i, Nil))

        // Update the analysis.
        removePluginInstances(oldInstances: _*)
        addPluginInstance(newInstance)

        // Update the original instances of the new instance.
        originalInstances.update(newInstance, oldOriginalInstances)
    }

    override def addPluginInstance(instance: PluginInstanceType) {
        super.addPluginInstance(instance)
        originalInstances += (instance -> List(instance))
    }

    override def removePluginInstance(instance: PluginInstanceType): Option[PluginInstance] = {
        originalInstances -= instance
        super.removePluginInstance(instance)
    }

    override def collapseBinding(binding: PluginInstanceBindingType, instance: PluginInstance) {
        // Store the original instances before they get removed within the collapse binding super call.
        val sourceOriginalInstances = originalInstances.getOrElse(binding.sourcePluginInstance, Nil)
        val targetOriginalInstances = originalInstances.getOrElse(binding.targetPluginInstance, Nil)

        super.collapseBinding(binding, instance)

        // Update the original instances of the collapsed instance.
        originalInstances.update(instance, sourceOriginalInstances ++ targetOriginalInstances)
    }
}
