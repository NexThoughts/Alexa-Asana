package com.alexaasana

import com.alexaasana.co.TaskCO
import com.amazon.ask.model.Intent
import com.amazon.ask.model.IntentConfirmationStatus

class AlexaService {

    def grailsApplication

    String SKILL_NAME = 'Asana Manager'
    String GET_HERO_MESSAGE = "Here's your hero: "
    String HELP_MESSAGE = 'You can say please fetch me a hero, or, you can say exit... What can I help you with?'
    String HELP_REPROMPT = 'What can I help you with?'
    String STOP_MESSAGE = 'Enjoy the day...Goodbye!'
    String MORE_MESSAGE = 'Do you want more?'
    String PAUSE = '<break time="0.3s" />'
    String WHISPER = '<amazon:effect name="whispered"/>'

    AlexaResponse help() {
        buildResponse(HELP_MESSAGE, false, "", HELP_REPROMPT)
    }

    AlexaResponse stopAndExit() {
        buildResponse(STOP_MESSAGE, true, "", "")
    }

    AlexaResponse welcome() {
        String welcomeSpeechOutput = "Welcome to Asana Task Manager<break time='0.3s'/>"
        String randomHero = "Cindrella"
        String tempOutput = WHISPER + GET_HERO_MESSAGE + randomHero + PAUSE
        String speechOutput = welcomeSpeechOutput + tempOutput + MORE_MESSAGE
        buildResponse(speechOutput, false, randomHero, MORE_MESSAGE)
    }

    AlexaResponse buildResponse(String speechText, Boolean shouldEndSession, String cardText, String reprompt) {
        String speechOutput = "<speak>" + speechText + "</speak>"

        AlexaResponse alexaResponse = new AlexaResponse()

        ResponseBody responseBody = new ResponseBody()
        responseBody.shouldEndSession = shouldEndSession

        SpeechResponse speechResponse = new SpeechResponse()
        speechResponse.type = "SSML"
        speechResponse.ssml = speechOutput
        responseBody.outputSpeech = speechResponse

        CardBody cardBody = new CardBody()
        cardBody.type = "Simple"
        cardBody.title = SKILL_NAME
        cardBody.content = cardText
        cardBody.text = cardText
        alexaResponse.card = cardBody

        if (reprompt) {
            RepromptBuilder repromptBuilder = new RepromptBuilder()
            SpeechResponse speechResponse1 = new SpeechResponse()
            speechResponse1.type = "PlainText"
            speechResponse1.text = reprompt
            speechResponse1.ssml = reprompt
            repromptBuilder.outputSpeech = speechResponse1
            alexaResponse.reprompt = repromptBuilder
        }

        return alexaResponse
    }

    AlexaResponse processRequest(Map alexaRequest, AlexaResponse alexaResponse) {
        String type = alexaRequest?.request?.type
        println("Request Type " + type)

        if (type == "LaunchRequest") {
            alexaResponse = alexaService.welcome()
        } else if (type == "SessionEndedRequest") {
            alexaResponse = alexaService.stopAndExit()
        } else if (type == "IntentRequest") {
            Intent intent = extractIntent(alexaRequest)
            switch (intent?.name) {
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
                    alexaService.alexaTaskParsing(intent)
                    alexaResponse = alexaService.stopAndExit()
                    break
            }
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

    def alexaTaskParsing(Intent intent) {
        TaskCO taskCO = new TaskCO()
        taskCO.projectName = intent.getSlots().projectname.value
        taskCO.action = intent.getSlots().action.value
        taskCO.taskName = intent.getSlots().taskname.value
        taskCO.tagName = intent.getSlots().tagname.value
        taskCO.email = intent.getSlots().email.value

        println(taskCO.projectName)
    }
}
