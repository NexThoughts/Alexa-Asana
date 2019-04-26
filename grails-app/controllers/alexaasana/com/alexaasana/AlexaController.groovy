package com.alexaasana

import com.alexaasana.response.AlexaResponse
import groovy.json.JsonOutput

class AlexaController {

    def alexaService

    def index() {
        println("Going to hit API")
        Map alexaRequest = request.JSON as Map
        AlexaResponse alexaResponse = alexaService.processRequest(alexaRequest)
        println("API return")
        println(JsonOutput.toJson(alexaResponse))
        render JsonOutput.toJson(alexaResponse)
    }
}
