package cz.payola.web.client.views.bootstrap

import cz.payola.web.client.View
import cz.payola.web.client.views.elements._

class DropDownButton(buttonViews: Seq[View], items: Seq[ListItem], buttonCssClass: String = "")
    extends Div(Nil, "btn-group")
{
    val toggleAnchor = new Anchor(buttonViews ++ List(new Span(Nil, "caret")), "#",
        "btn dropdown-toggle " + buttonCssClass)

    toggleAnchor.setAttribute("data-toggle", "dropdown")

    val menu = new UnorderedList(items, "dropdown-menu")

    override val subViews = List(toggleAnchor, menu)

    def setItems(items: Seq[ListItem]) {
        menu.removeAllChildNodes()
        items.foreach(_.render(menu.domElement))
    }
}