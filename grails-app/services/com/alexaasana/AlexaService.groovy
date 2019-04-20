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

    def help() {
        buildResponseWithRepromt(HELP_MESSAGE, false, "", HELP_REPROMPT)
    }

    Map welcome() {
        String welcomeSpeechOutput = "Welcome to Asana Task Manager<break time='0.3s'/>"
        String randomHero = data.get(0)
        String tempOutput = WHISPER + GET_HERO_MESSAGE + randomHero + PAUSE
        String speechOutput = welcomeSpeechOutput + tempOutput + MORE_MESSAGE
        buildResponseWithRepromt(speechOutput, false, randomHero, MORE_MESSAGE)
    }

    Map buildResponseWithRepromt(String speechText, Boolean shouldEndSession, String cardText, String reprompt) {
        String speechOutput = "<speak>" + speechText + "</speak>"
        ["version" : "1.0",
         "response": [
                 "shouldEndSession": shouldEndSession,
                 "outputSpeech"    : ["type": "SSML", ssml: speechOutput]
         ],
         "card"    : ["type": "Simple", "title": SKILL_NAME, "content": cardText, "text": cardText],
         "reprompt": [
                 "outputSpeech": [
                         "type": "PlainText",
                         "text": reprompt,
                         "ssml": reprompt
                 ]
         ]
        ]
    }
}
