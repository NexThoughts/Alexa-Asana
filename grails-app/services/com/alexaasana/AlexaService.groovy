package com.alexaasana

import com.alexaasana.request.AlexaRequest
import com.alexaasana.response.AlexaResponse

class AlexaService {

    AlexaResponse processRequest(Map alexaRequest) {
        AlexaRequest request = new AlexaRequest()
        return request.processRequest(alexaRequest)
    }
}
