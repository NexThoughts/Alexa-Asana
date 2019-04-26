package alexaasana.com.alexaasana

import com.alexaasana.co.TaskCO
import com.alexaasana.request.AlexaRequest
import grails.core.GrailsApplication

class HomeController {
    GrailsApplication grailsApplication

    def index() {
        /*TaskCO taskCO = new TaskCO()
        taskCO.taskName = "My First Task"
        taskCO.projectName = "Demo Project"
        taskCO.tagName = "Test1"
        taskCO.email = "anubhav"
        AsanaApi asanaIntegration = new AsanaApi()
        asanaIntegration.createTask(taskCO)
        render "DONE-------"*/

        TaskCO taskCO = new TaskCO(taskName: "A dummy task1", projectName: "Dummy Project1", tagName: "testdemo")

        AlexaRequest alexaRequest = new AlexaRequest()
        alexaRequest.processCreate(taskCO, grailsApplication.config.asana.access_token)
        render "done"
    }
}
