package com.alexaasana.request

import com.alexaasana.response.AlexaMessageBuilder
import com.alexaasana.response.AlexaResponse
import com.alexaasana.response.CardBody
import com.alexaasana.response.RepromptBuilder
import com.alexaasana.response.ResponseBody
import com.alexaasana.response.SpeechResponse
import com.alexaasana.co.TaskCO
import com.amazon.ask.model.Intent
import com.amazon.ask.model.IntentConfirmationStatus

class AlexaRequest {

    AlexaResponse processRequest(Map alexaRequest, AlexaResponse alexaResponse) {
        String type = alexaRequest?.request?.type

        println("Request Type " + type)
        AlexaMessageBuilder messageBuilder = new AlexaMessageBuilder()
        if (type == "LaunchRequest") {
            alexaResponse = messageBuilder.welcome()
        } else if (type == "SessionEndedRequest") {
            alexaResponse = messageBuilder.stopAndExit()
        } else if (type == "IntentRequest") {
            alexaResponse = processIntentRequest(alexaRequest, messageBuilder, alexaResponse)
        }
        alexaResponse
    }

    private AlexaResponse processIntentRequest(Map alexaRequest, AlexaMessageBuilder messageBuilder, AlexaResponse alexaResponse) {
        Intent intent = extractIntent(alexaRequest)
        switch (intent?.name) {
            case "AMAZON.HelpIntent":
                println("I am asking for Help")
                alexaResponse = messageBuilder.help()
                break
            case "AMAZON.StopIntent":
                println("I am asking to stop")
                break
            case "AMAZON.CancelIntent":
                println("I am asking to Cancel")
                alexaResponse = messageBuilder.stopAndExit()
                break
            case "AsanaAppIntent":
                println("I am asking to Asana App")
                TaskCO taskCO = new TaskCO(intent)
                println(taskCO.properties)
                alexaResponse = messageBuilder.stopAndExit()
                break
        }
        alexaResponse
    }

    Intent extractIntent(Map alexaRequest) {
        Map intentBuilder = alexaRequest?.request?.intent as Map
        Intent.Builder builder = Intent.builder()
        builder.withSlots(intentBuilder.slots as Map)
        builder.withName(intentBuilder.name as String)
        builder.withConfirmationStatus(IntentConfirmationStatus.valueOf(intentBuilder.confirmationStatus as String))

        Intent intent = builder.build()
        intent
    }
}
