package cz.payola.web.client.views.graph

import cz.payola.web.client.views._
import cz.payola.web.client.views.graph.visual.techniques.tree.TreeTechnique
import cz.payola.common.rdf._
import cz.payola.common.entities.settings.OntologyCustomization
import s2js.adapters.html.Element
import scala.collection.mutable.ArrayBuffer
import cz.payola.common.visual.Color
import s2js.runtime.client.scala.collection.mutable.HashMap
import cz.payola.web.client.models.VertexVariableNameGenerator
import cz.payola.common.rdf.Vertex
import cz.payola.web.client.events._
import scala.Some
import s2js.compiler.javascript

class SimpleGraphView(placeholder: ElementView[Element], verticesCount: Int) extends GraphView
{
    val technique = new TreeTechnique()

    technique.render(placeholder.htmlElement)

    val vertices = new ArrayBuffer[Vertex]()

    val refVertices = new ArrayBuffer[Vertex]()

    val edges = new ArrayBuffer[Edge]()

    var newPath = true

    val map = new HashMap[Vertex, String]()

    val patternUpdated = new UnitEvent[SimpleGraphView, EventArgs[SimpleGraphView]]()

    @javascript("""jQuery(".datacube-infobar .message").hide(); jQuery(".datacube-infobar .message").eq(i).show()""")
    def showMessage(i: Int) {}

    technique.vertexSelected += {
        e =>

            if (vertices.size == 0) {
                vertices += e.vertex
                newPath = false
                technique.updateVertexColor(e.vertex, Some(Color.Green))
            } else {
                val last = vertices.last
                if (newPath == true) {
                    if (e.vertex != last) {
                        currentGraph.map {
                            g =>
                                vertices.map {
                                    v =>
                                        g.getIncomingEdges(e.vertex).find(_.origin == v).map {
                                            edge =>
                                                vertices += e.vertex
                                                technique.updateVertexColor(e.vertex, Some(Color.Green))
                                                edges += edge
                                                newPath = false
                                        }
                                }
                        }
                    }
                } else {
                    if (e.vertex == last) {
                        refVertices += e.vertex
                        technique.updateVertexColor(e.vertex, Some(Color.Red))
                        newPath = true
                    } else {
                        currentGraph.map {
                            g =>
                                if (g.getIncomingEdges(e.vertex).exists(_.origin == last)) {
                                    vertices += e.vertex
                                    technique.updateVertexColor(e.vertex, Some(Color.Green))
                                }
                        }
                    }
                }
            }

            showMessage(refVertices.size)

            if (verticesCount == refVertices.size) {
                patternUpdated.trigger(new EventArgs[SimpleGraphView](this))
            }
    }

    private def getVertexName(map: HashMap[Vertex, String], vertex: Vertex,
        generator: VertexVariableNameGenerator): String = {
        if (!map.isDefinedAt(vertex)) {
            map.put(vertex, generator.nextName)
        }

        "?" + map.get(vertex).get
    }

    def getSignificantVertices = {
        getPattern
        refVertices.map("?"+map.get(_).get)
    }

    def getPattern: String = {
        val generator = new VertexVariableNameGenerator()

        edges.map {
            e =>
                val originVar = getVertexName(map, e.origin, generator)
                val destinationVar = getVertexName(map, e.destination, generator)

                originVar + " <" + e.uri + "> " + destinationVar + " ."
        }.mkString("\n")
    }

    override def update(graph: Option[Graph], customization: Option[OntologyCustomization]) {
        super.update(graph, customization)
        technique.update(graph, customization)

        showMessage(0)
    }

    override def updateGraph(graph: Option[Graph], contractLiterals: Boolean) {
        super.updateGraph(graph, false)
        technique.updateGraph(graph, false)

        showMessage(0)
    }

    override def updateOntologyCustomization(customization: Option[OntologyCustomization]) {
        super.updateOntologyCustomization(customization)
        technique.updateOntologyCustomization(customization)

        showMessage(0)
    }

    override def updateVertexColor(vertex: Vertex, color: Option[Color]) {
        technique.updateVertexColor(vertex, color)
    }
}
