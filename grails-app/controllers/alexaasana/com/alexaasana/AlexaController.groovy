package alexaasana.com.alexaasana

import com.alexaasana.AlexaResponse
import com.amazon.ask.model.Intent
import com.amazon.ask.model.IntentConfirmationStatus
import groovy.json.JsonOutput

class AlexaController {

    def alexaService

    def index() {
        Map alexaRequest = request.JSON as Map
        AlexaResponse alexaResponse = new AlexaResponse()
        alexaService.processRequest(alexaRequest, alexaResponse)
        render JsonOutput.toJson(alexaResponse)
    }
}
