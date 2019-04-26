package com.alexaasana.response

class AlexaResponse {
    String version = "1.0"
    ResponseBody response = new ResponseBody()
    Map sessionAttributes = new HashMap()
    CardBody card = new CardBody()
    RepromptBuilder reprompt = new RepromptBuilder()
}
