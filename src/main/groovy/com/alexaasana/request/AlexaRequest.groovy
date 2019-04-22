package com.alexaasana.request

import com.alexaasana.response.AlexaMessageBuilder
import com.alexaasana.response.AlexaResponse
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
                processAction(taskCO)
                alexaResponse = messageBuilder.stopAndExit()
                break
        }
        alexaResponse
    }

    def processAction(TaskCO taskCO) {
        switch (taskCO.action) {
            case "create":
                processCreate(taskCO)
                break
            case "delete":
                break
            case "remove":
                break
            case "add":
                break
            case "update":
                break
            case "list":
                break
            case "assign":
                break
        }
    }

    def processCreate(TaskCO taskCO) {

        if (!taskCO.projectName)
            return "Project Name is required to create task, tag, assign to user"

        if (taskCO.taskName && taskCO.tagName && taskCO.email) {
            createTask(taskCO.projectName, taskCO.taskName, taskCO.tagName, taskCO.email)
        } else if (taskCO.taskName && taskCO.tagName) {
            createTaskAndAssignTag(taskCO.projectName, taskCO.taskName, taskCO.tagName)
        } else if (taskCO.taskName && taskCO.email) {
            createTaskAndAssignUser(taskCO.projectName, taskCO.taskName, taskCO.email)
        } else if (taskCO.taskName) {
            createTask(taskCO.projectName, taskCO.taskName)
        } else if (taskCO.tagName) {
            createTag(taskCO.projectName, taskCO.tagName)
        } else if (taskCO.email) {
            addUser(taskCO.projectName, taskCO.email)
        }
    }

    def createTask(String project, String task, String tag, String email) {

    }

    def createTask(String project, String task) {
        //Check if Task not exist then create and return
    }

    def createTaskAndAssignTag(String project, String task, String tag) {

    }

    def createTaskAndAssignUser(String project, String task, String email) {

    }

    def createTag(String project, String tag) {
        //Check If Tag not exist then create and return tag
    }

    def addUser(String project, String email) {
        //Check If User Not exist then create and return user
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
