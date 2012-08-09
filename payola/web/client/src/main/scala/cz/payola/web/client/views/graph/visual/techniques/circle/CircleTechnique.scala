package cz.payola.web.client.views.graph.visual.techniques.circle

import cz.payola.web.client.views.graph.visual.animation.Animation
import cz.payola.web.client.views.graph.visual.techniques.BaseTechnique
import cz.payola.web.client.views.graph.visual.settings.components.visualsetup.VisualSetup
import cz.payola.web.client.views.graph.visual.graph._
import collection.mutable.ListBuffer
import cz.payola.web.client.views.algebra.Point2D

/**
 * Visual plug-in technique that places the vertices into a circled tree structure.
 */
class CircleTechnique(settings: VisualSetup) extends BaseTechnique(settings, "Circle Visualization")
{
    protected def getTechniquePerformer(component: Component,
        animate: Boolean): Animation[ListBuffer[(VertexView, Point2D)]] = {
        if (animate) {
            basicTreeCircledStructure(component.vertexViews, None, redrawQuick, redraw, None)
        } else {
            basicTreeCircledStructure(component.vertexViews, None, redrawQuick, redraw, Some(0))
        }
    }

    override def destroy() {
        super.destroy()
    }
}
