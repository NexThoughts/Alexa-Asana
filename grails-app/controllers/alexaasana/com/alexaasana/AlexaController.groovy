package alexaasana.com.alexaasana

import com.alexaasana.AlexaResponse
import groovy.json.JsonOutput

class AlexaController {

    def alexaService

    def index() {
        println(request.JSON)

        AlexaResponse alexaResponse = null
        println("****************************")

        println("Request Type " + request.JSON.request.type)
        if (request?.JSON?.request?.type == "LaunchRequest") {
            alexaResponse = alexaService.welcome()
        } else if (request?.JSON?.request?.type == "SessionEndedRequest") {
            println("Session Ended")
        } else if (request?.JSON?.request?.type == "IntentRequest") {
            println(request?.JSON?.request?.intent?.name)
            switch (request?.JSON?.request?.intent?.name) {
                case "AMAZON.HelpIntent":
                    println("I am asking for Help")
                    alexaResponse = alexaService.help()
                    break
                case "AMAZON.StopIntent":
                    println("I am asking to stop")
                    break
                case "AMAZON.CancelIntent":
                    println("I am asking to Cancel")
                    alexaResponse = alexaService.stopAndExit()
                    break
                case "AsanaAppIntent":
                    println("I am asking to Asana App")
                    break
            }
        }


        render JsonOutput.toJson(alexaResponse)
    }
}
