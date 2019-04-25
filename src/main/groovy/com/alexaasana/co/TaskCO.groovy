package com.alexaasana.co

import com.amazon.ask.model.Intent

class TaskCO {
    String action = ""
    String workspaceName = "Alexa"
    String projectName = ""
    String taskName = ""
    String tagName = ""
    String email = ""
    String taskId = null

    TaskCO() {

    }

    TaskCO(Intent intent) {
        this.projectName = intent.getSlots().projectname.value
        this.action = intent.getSlots().action.value
        this.taskName = intent.getSlots().taskname.value
        this.tagName = intent.getSlots().tagname.value
        this.email = intent.getSlots().email.value
    }
}
