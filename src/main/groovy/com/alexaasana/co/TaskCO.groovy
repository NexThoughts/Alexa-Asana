package com.alexaasana.co

import com.amazon.ask.model.Intent
import com.amazon.ask.model.Session

class TaskCO {
    String action = ""
    String workspaceName = "Alexa"
    String projectName = ""
    String taskName = ""
    String tagName = ""
    String email = ""
    String taskId = ""
    String type = ""

    TaskCO() {

    }

    TaskCO(Intent intent, Session session) {
        this.projectName = session?.attributes?.projectName ?: intent.getSlots().projectname.value
        this.action = session?.attributes?.action ?: intent.getSlots().action.value
        this.taskName = session?.attributes?.taskName ?: intent.getSlots().taskname.value
        this.tagName = session?.attributes?.tagName ?: intent.getSlots().tagname.value
        this.email = session?.attributes?.email ?: intent.getSlots().email.value
        this.type = session?.attributes?.type ?: intent.getSlots().type.value
    }
}
