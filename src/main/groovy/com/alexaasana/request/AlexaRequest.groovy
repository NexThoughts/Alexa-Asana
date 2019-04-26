package com.alexaasana.request

import com.alexaasana.api.AsanaIntegration
import com.alexaasana.co.TaskCO
import com.alexaasana.response.AlexaMessageBuilder
import com.alexaasana.response.AlexaResponse
import com.amazon.ask.model.Intent
import com.amazon.ask.model.IntentConfirmationStatus
import com.amazon.ask.model.Session
import com.asana.models.Project
import com.asana.models.Tag
import com.asana.models.Task

class AlexaRequest {

    AlexaResponse processRequest(Map alexaRequest) {
        String type = alexaRequest?.request?.type

        println("Request Type " + type)
        AlexaMessageBuilder messageBuilder = new AlexaMessageBuilder()
        if (type == "LaunchRequest") {
            return messageBuilder.welcome()
        } else if (type == "SessionEndedRequest") {
            return messageBuilder.stopAndExit()
        } else if (type == "IntentRequest") {
            return processIntentRequest(alexaRequest, messageBuilder)
        } else {
            return null
        }
    }

    private AlexaResponse processIntentRequest(Map alexaRequest, AlexaMessageBuilder messageBuilder) {
        AlexaResponse alexaResponse = null
        Intent intent = extractIntent(alexaRequest)
        Session session = extractAttribute(alexaRequest)
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
                TaskCO taskCO = new TaskCO(intent, session)
                alexaResponse = processAction(taskCO)
                break
            case "PlayMusic":
                break
        }
        return alexaResponse
    }

    AlexaResponse playMusic(TaskCO taskCO) {
        if (!taskCO.type) {
            return null
        }
        

    }

    AlexaResponse processAction(TaskCO taskCO) {
        AlexaResponse alexaResponse = null
        switch (taskCO.action) {
            case "create":
                println("I am going to create")
                alexaResponse = processCreate(taskCO)
                break
            case "delete":
                processRemove(taskCO)
                break
            case "remove":
                processRemove(taskCO)
                break
            case "add":
                processAdd(taskCO)
                break
            case "update":
                processUpdate(taskCO)
                break
            case "assign":
                processAdd(taskCO)
                break
        }
        return alexaResponse
    }

    AlexaResponse processUpdate(TaskCO taskCO) {
        return null
    }

    AlexaResponse processAdd(TaskCO taskCO) {
        return null
    }

    AlexaResponse processRemove(TaskCO taskCO) {
        return null
    }

    AlexaResponse processCreate(TaskCO taskCO) {
        AlexaMessageBuilder messageBuilder = new AlexaMessageBuilder()
        if (!taskCO.projectName) {
            return messageBuilder.projectNameRequired(taskCO)
        }

        println("I have asana project name")

        AsanaIntegration asanaIntegration = new AsanaIntegration()

        if (taskCO.taskName) {
            println("I have task name")
            Task task = asanaIntegration.createTask(taskCO)
            return messageBuilder.success("Task with id ${task.id} has been created.")
        } else if (taskCO.projectName) {
            Project project = asanaIntegration.findOrCreateProject(taskCO)
            return messageBuilder.success("Project with id ${project.id} has been created.")
        } else if (taskCO.tagName) {
            Tag tag = asanaIntegration.findOrCreateTag(taskCO.tagName, taskCO.workspaceName)
            return messageBuilder.success("Tag with id ${tag.id} has been created.")
        }
/*
        Client client = Client.accessToken(key)
        Workspace workspace = client.workspaces.findAll().find { it.name.equalsIgnoreCase(taskCO.workspaceName) }

        println("selected workspace is " + workspace.properties)

        List<Project> projects = client.projects.findByWorkspace(workspace.id).execute()

        Project selectedProject = projects.find { it.name == taskCO.projectName }

        if (!selectedProject) {
            selectedProject = client.projects
                    .createInWorkspace(workspace.id)
                    .data("name", taskCO.projectName)
                    .execute()
        }
        println("Selected Project Is " + selectedProject.properties)

        List<Tag> tags = client.tags.findByWorkspace(workspace.id).execute()
        println("tags " + tags)
        Tag selectedTag = tags.find {
            it.name.equalsIgnoreCase(taskCO.tagName)
        }

        if (!selectedTag) {
            selectedTag = client.tags
                    .createInWorkspace(workspace.id)
                    .data("name", taskCO.tagName)
                    .execute()
        }

        println("Selected tag is " + selectedTag.properties)
 Task demoTask = client.tasks
                 .createInWorkspace(workspace.id)
                 .data("name", taskCO.taskName)
                 .data("projects", [selectedProject.id])
                 .data("tags", [selectedTag.id])
                 .execute()

                 List<Task> tasks = client.tasks.findByProject(selectedProject.id) as ArrayList

         Task demoTask = tasks.find { it.name.equalsIgnoreCase(taskCO.taskName) }

         client.tasks
                 .addTag(demoTask.id)
                 .data("tag", selectedTag.id)
                 .execute()if (taskCO.taskName && taskCO.tagName && taskCO.email) {
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
        }*/
    }

    def createTask(String project, String task, String tag, String email) {

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

    Session extractAttribute(Map alexaRequest) {
        Map sessionBuilder = alexaRequest?.session as Map
        println(sessionBuilder)
        Session.Builder builder = Session.builder()
        builder.withAttributes(sessionBuilder.attributes as Map)

        Session session = builder.build()
        session
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
