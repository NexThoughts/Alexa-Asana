package alexaasana.com.alexaasana

import grails.converters.JSON

class AlexaController {

    def alexaService

    def index() {
        println(request.JSON)

        def map = [:]
        println("****************************")

        alexaService.createAsanaProject()

        println("Request Type " + request.JSON.request.type)
        if (request?.JSON?.request?.type == "LaunchRequest") {
            map = alexaService.welcome()
        } else if (request?.JSON?.request?.type == "SessionEndedRequest") {
            println("Session Ended")
        } else if (request?.JSON?.request?.type == "IntentRequest") {
            println(request?.JSON?.request?.intent?.name)
            switch (request?.JSON?.request?.intent?.name) {
                case "AMAZON.HelpIntent":
                    println("I am asking for Help")
                    map = alexaService.help()
                    break
                case "AMAZON.StopIntent":
                    println("I am asking to stop")
                    break
                case "AMAZON.CancelIntent":
                    println("I am asking to Cancel")
                    break
                case "AsanaAppIntent":
                    println("I am asking to Asana App")
                    break
            }
        }
        println("****************************")
        println(map)
        render map as JSON
    }
}
