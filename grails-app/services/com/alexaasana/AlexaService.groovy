package com.alexaasana

import com.alexaasana.request.AlexaRequest
import com.alexaasana.response.AlexaResponse

class AlexaService {

    def grailsApplication

    def processRequest(Map alexaRequest, AlexaResponse alexaResponse) {
        AlexaRequest request = new AlexaRequest(grailsApplication.config.asana.access_token)
        request.processRequest(alexaRequest, alexaResponse)
    }
}
