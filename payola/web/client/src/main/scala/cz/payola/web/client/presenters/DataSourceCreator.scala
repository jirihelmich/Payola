package cz.payola.web.client.presenters

import cz.payola.web.client.View
import s2js.adapters.js.browser._
import cz.payola.web.shared.DataSourceManager
import cz.payola.web.client.views.elements._
import s2js.adapters.js
import s2js.adapters.js.dom
import cz.payola.domain.entities.Plugin
import cz.payola.domain.entities.plugins.Parameter
import cz.payola.web.client.views.bootstrap.InputControl
import s2js.compiler.javascript
import scala.collection.mutable.ListBuffer

class DataSourceCreator(val dataFetcherDivID: String,
    val optionsDivID: String,
    val submitButtonDivID: String,
    val nameFieldDivID: String,
    val descriptionFieldDivID: String,
    val listingURL: String) extends View
{

    // An array of input controls
    var optionInputControls = new ListBuffer[InputControl]()

    // Define internal <select> ID
    val dataFetcherListID = "data_fetcher_list"

    // Get the divs from IDs
    val optionsDiv = document.getElementById(optionsDivID)
    val dataFetcherListWrapper = document.getElementById(dataFetcherDivID)
    val submitButtonDiv = document.getElementById(submitButtonDivID)
    val nameFieldDiv = document.getElementById(nameFieldDivID)
    val descriptionFieldDiv = document.getElementById(descriptionFieldDivID)

    // Create name & description fields
    val nameField = new InputControl("Data source name:", "__dataSourceName__", "My data source", "")
    nameField.render(nameFieldDiv)
    val descriptionField = new InputControl("Description:", "__dataSourceDescription__", "", "")
    descriptionField.render(descriptionFieldDiv)

    // Create a data fetcher list
    val dataFetcherList: js.dom.Input = document.createElement[js.dom.Input]("select")
    dataFetcherList.setAttribute("id", dataFetcherListID)
    dataFetcherList.setAttribute("name", "__dataSourceFetcherType__")
    dataFetcherListWrapper.appendChild(dataFetcherList)

    // Add a onchange event
    dataFetcherList.onchange = { event =>
        reloadOptions()
        true
    }

    // Load data fetchers
    val availableDataFetchers = DataSourceManager.getAvailableDataFetchers()
    availableDataFetchers foreach { dataFetcher =>
        new SelectOption(List(new Text(dataFetcher.name))).render(dataFetcherList)
    }

    if (availableDataFetchers.size == 0){
        // No available data fetchers
        window.alert("There are no data fetcher plugins available!")
        redirectToListing()
    }

    // Reload plugin options
    reloadOptions()

    // Create a submit button
    val submitButton = new Button(new Text("Create Data Source"))
    submitButton.mouseClicked += { event =>
        if (validateInputFields) {
            submitForm()
            true
        }else{
            false
        }
    }
    submitButton.render(submitButtonDiv)

    /** Returns selected plugin according to selected name.
      *
      * @return Selected plugin.
      */
    private def getSelectedPlugin: Plugin = {
        availableDataFetchers.find(_.name == dataFetcherList.value).get
    }

    /** Redirects to the data source listing page.
      *
      */
    private def redirectToListing() {
        window.location.href = listingURL
    }

    /** Lists all options for the selected plugin.
      *
      */
    private def reloadOptions() {
        // Remove all old options
        optionsDiv.innerHTML = ""
        optionInputControls = new ListBuffer[InputControl]()

        val plugin: Plugin = getSelectedPlugin
        plugin.parameters foreach { param: Parameter[_] =>
            // TODO distinguish between string/bool/etc. parameters?
            val inputControl = new InputControl(param.name, param.name, param.defaultValue.toString, "")
            optionInputControls += inputControl
            inputControl.render(optionsDiv)
        }
    }

    @javascript("document.forms['create_form'].submit();")
    private def submitForm(){

    }

    private def validateInputFields: Boolean = {
        var result = false
        if (nameField.input.value == "") {
            window.alert("Data source name may not be empty!")
        }else if (DataSourceManager.dataSourceExistsWithName(nameField.input.value)) {
            window.alert("Data source with this name already exists!")
        }else if (descriptionField.input.value == ""){
            window.alert("Data source description musn't be empty!")
        }else if (!validateOptionsInputFields){
            // leave result false
        }else{
            result = true
        }
        result
    }

    private def validateOptionsInputFields: Boolean = {
        var result = true
        optionInputControls foreach { inputControl: InputControl =>
            if (inputControl.input.value == ""){
                window.alert("Parameter value cannot be empty for " + inputControl.label + ".")
                result = false
            }
        }
        result
    }

    def render(parent: dom.Element) = {
        // TODO
    }

    def destroy() {
        // TODO
    }

    def block() {
        // TODO
    }

    def unblock() {
        // TODO
    }

}
