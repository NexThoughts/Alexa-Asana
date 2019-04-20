package com.alexaasana

import com.fasterxml.jackson.annotation.JsonProperty

class SpeechResponse {
    String type
    String ssml
    String text
    PlayBehaviour playBehavior
}
