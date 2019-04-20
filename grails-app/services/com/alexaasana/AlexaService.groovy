package com.alexaasana

class AlexaService {

    def grailsApplication
    String SKILL_NAME = 'Disney Heroes'
    String GET_HERO_MESSAGE = "Here's your hero: "
    String HELP_MESSAGE = 'You can say please fetch me a hero, or, you can say exit... What can I help you with?'
    String HELP_REPROMPT = 'What can I help you with?'
    String STOP_MESSAGE = 'Enjoy the day...Goodbye!'
    String MORE_MESSAGE = 'Do you want more?'
    String PAUSE = '<break time="0.3s" />'
    String WHISPER = '<amazon:effect name="whispered"/>'

    List data = ['Aladdin  ', 'Cindrella ', 'Bambi', 'Bella ', 'Bolt ', 'Donald Duck', 'Genie ', 'Goofy', 'Mickey Mouse',]

    AlexaResponse help() {
        buildResponse(HELP_MESSAGE, false, "", HELP_REPROMPT)
    }

    AlexaResponse stopAndExit() {
        buildResponse(STOP_MESSAGE, true, "", "")
    }

    AlexaResponse welcome() {
        String welcomeSpeechOutput = "Welcome to Asana Task Manager<break time='0.3s'/>"
        String randomHero = data.get(0)
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
}
