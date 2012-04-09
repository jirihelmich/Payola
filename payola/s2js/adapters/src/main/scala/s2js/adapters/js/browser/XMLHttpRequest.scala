package s2js.adapters.js.browser

class XMLHttpRequest
{
    var readyState: Int = 0

    var responseText: String = ""

    var responseXML: String = ""

    var status: Int = 0

    var statusText: String = ""

    var onreadystatechange: () => Unit = null

    var onerror: () => Unit = null

    def abort() {}

    def getResponseHeader(header: String): String = ""

    def open(method: String, url: String, async: Boolean = true, username: String = "",
        password: String = "") {}

    def send(data: String = "") {}

    def setRequestHeader(header: String, value: String) {}
}
