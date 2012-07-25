package cz.payola.web.client.views.graph.visual.settings.components.visualsetup

import cz.payola.web.client.views.elements.Div
import cz.payola.web.client.views.graph.visual.settings.TextSettingsModel
import cz.payola.web.client.views.bootstrap.Modal

class TextModal(model: TextSettingsModel) extends Modal("Text settings")
{
    val colorBackground = new ColorPane("text.color.background", "Text background", Some(model.colorBackgroundValue))

    colorBackground.changed += { event =>
        model.colorBackgroundValue = colorBackground.getColor.get
    }

    val color = new ColorPane("text.color.foreground", "Text foreground", Some(model.colorValue))

    color.changed += { event =>
        model.colorValue = color.getColor.get
    }

    override val body = List(new Div(List(color, colorBackground)))
}