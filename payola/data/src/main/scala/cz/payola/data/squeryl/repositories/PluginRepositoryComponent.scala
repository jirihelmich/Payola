package cz.payola.data.squeryl.repositories

import org.squeryl.PrimitiveTypeMode._
import cz.payola.data.squeryl.entities.User
import cz.payola.data.squeryl.entities.PluginDbRepresentation
import cz.payola.domain.entities.Plugin
import cz.payola.data.squeryl.entities.plugins.Parameter
import cz.payola.data.squeryl._
import org.squeryl.dsl.ast.LogicalBoolean
import cz.payola.data.squeryl.entities.plugins.parameters._
import cz.payola.data.{PaginationInfo, DataException}

trait PluginRepositoryComponent extends TableRepositoryComponent
{
    self: SquerylDataContextComponent =>

    lazy val pluginRepository = new PluginRepository
    {
        private type QueryType = (PluginDbRepresentation, Option[User], Option[BooleanParameter], Option[FloatParameter],
                            Option[IntParameter], Option[StringParameter])

        private val representationRepository =
            new TableRepository[PluginDbRepresentation, QueryType](schema.plugins,PluginDbRepresentation)
            {
                protected def getSelectQuery(entityFilter: (PluginDbRepresentation) => LogicalBoolean) = {
                    join(schema.plugins, schema.users.leftOuter, schema.booleanParameters.leftOuter,
                        schema.floatParameters.leftOuter, schema.intParameters.leftOuter,
                        schema.stringParameters.leftOuter)((p, o, bPar, fPar, iPar, sPar) =>
                            where(entityFilter(p))
                            select(p, o, bPar, fPar, iPar, sPar)
                            on(o.map(_.id) === p.ownerId,
                                bPar.map(_.pluginId) === Some(p.id),
                                fPar.map(_.pluginId) === Some(p.id),
                                iPar.map(_.pluginId) === Some(p.id),
                                sPar.map(_.pluginId) === Some(p.id))
                    )
                }

                protected def processSelectResults(results: Seq[QueryType]) = {
                    results.groupBy(_._1).map { r =>
                        val plugin =  r._1
                        plugin.owner = r._2.head._2
                        plugin.parameters = r._2.flatMap(c => Seq(c._3, c._4, c._5, c._6).flatten)

                        plugin
                    }(collection.breakOut)

                }
            }

        def getByIds(ids: Seq[String]): Seq[Plugin] = schema.wrapInTransaction {
            representationRepository.getByIds(ids).map(_.toPlugin)
        }

        def removeById(id: String): Boolean = schema.wrapInTransaction {
            representationRepository.removeById(id)
        }

        def getAll(pagination: Option[PaginationInfo] = None): Seq[Plugin] = schema.wrapInTransaction {
            representationRepository.getAll(pagination).map(_.toPlugin)
        }

        def getAllPublic: Seq[Plugin] = schema.wrapInTransaction {
            representationRepository.selectWhere(_.isPublic === true).map(_.toPlugin)
        }

        def getAllByOwnerId(ownerId: Option[String]): Seq[Plugin] = schema.wrapInTransaction {
            representationRepository.selectWhere(_.ownerId === ownerId).map(_.toPlugin)
        }

        def getByName(name: String): Option[Plugin] = schema.wrapInTransaction {
            representationRepository.selectOneWhere(_.name === name).map(_.toPlugin)
        }

        def persist(entity: AnyRef): Plugin = schema.wrapInTransaction {
            entity match {
                case plugin: Plugin => {
                    // Persist the plugin ...
                    val representation = representationRepository.persist(entity)

                    // ... then all its parameters
                    plugin.parameters.map(parameter => representation.associateParameter(Parameter(parameter)))

                    // Return persisted plugin
                    plugin
                }
                case _ => throw new DataException("Couldn't convert the entity to a plugin.")
            }
        }

        def getCount: Long = schema.wrapInTransaction {
            representationRepository.getCount
        }
    }
}
