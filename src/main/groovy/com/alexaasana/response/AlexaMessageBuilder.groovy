package com.alexaasana.response

import com.alexaasana.co.TaskCO
import groovy.json.JsonOutput

class AlexaMessageBuilder {

    String SKILL_NAME = 'Asana Manager'
    String GET_HERO_MESSAGE = "Here's your hero: "
    String HELP_MESSAGE = 'You can say please fetch me a hero, or, you can say exit... What can I help you with?'
    String HELP_REPROMPT = 'What can I help you with?'
    String STOP_MESSAGE = 'Enjoy the day...Goodbye!'
    String MORE_MESSAGE = 'Do you want more?'
    String PAUSE = '<break time="0.3s" />'
    String WHISPER = '<amazon:effect name="whispered"/>'

    AlexaResponse help() {
        buildResponse(HELP_MESSAGE, false, "", HELP_REPROMPT, "")
    }

    AlexaResponse stopAndExit() {
        buildResponse(STOP_MESSAGE, true, "", "", "")
    }

    AlexaResponse success(String message) {
        buildResponse(message, true, "", "", "")
    }

    AlexaResponse welcome() {
        String welcomeSpeechOutput = "Welcome to Asana Task Manager<break time='0.3s'/>"
        String randomHero = "Cindrella"
        String tempOutput = WHISPER + GET_HERO_MESSAGE + randomHero + PAUSE
        String speechOutput = welcomeSpeechOutput + tempOutput + MORE_MESSAGE
        buildResponse(speechOutput, false, randomHero, MORE_MESSAGE, "")
    }

    AlexaResponse projectNameRequired(TaskCO taskCO) {
        String message = "Project Name is required. <break time='0.3s'/> Please tell the project name by saying my project is name"
        String reprompt = "Please tell the project name by saying my project is name"
        Boolean shouldEndSession = false
        String cardText = "Project Name is required."

        String sessionAttribute = JsonOutput.toJson(taskCO)

        buildResponse(message, shouldEndSession, cardText, reprompt, sessionAttribute)
    }

    AlexaResponse buildResponse(String speechText, Boolean shouldEndSession, String cardText, String reprompt, String sessionAttribute) {
        String speechOutput = "<speak>" + speechText + "</speak>"

        AlexaResponse alexaResponse = new AlexaResponse()

        if (sessionAttribute) {
            alexaResponse.sessionAttributes = sessionAttribute
        }

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
}
