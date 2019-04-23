package alexaasana.com.alexaasana

import com.alexaasana.api.AsanaIntegration
import com.alexaasana.co.TaskCO

class HomeController {

    def index() {
        TaskCO taskCO = new TaskCO()
        taskCO.taskName = "My First Task"
        taskCO.projectName = "Demo Project"
        taskCO.tagName = "Test1"
        taskCO.email = "anubhav"
        AsanaIntegration asanaIntegration = new AsanaIntegration()
        asanaIntegration.createTask(taskCO)
        render "DONE-------"
    }
}
