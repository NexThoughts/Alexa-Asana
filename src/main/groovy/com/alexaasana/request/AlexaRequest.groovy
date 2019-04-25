package com.alexaasana.request

import com.alexaasana.api.AsanaIntegration
import com.alexaasana.response.AlexaMessageBuilder
import com.alexaasana.response.AlexaResponse
import com.alexaasana.co.TaskCO
import com.amazon.ask.model.Intent
import com.amazon.ask.model.IntentConfirmationStatus
import com.asana.Client
import com.asana.models.Project
import com.asana.models.Tag
import com.asana.models.Task
import com.asana.models.Workspace
import com.asana.requests.CollectionRequest

class AlexaRequest {

    String asanaKey = ""
    AlexaMessageBuilder messageBuilder

    AlexaRequest() {}

    AlexaRequest(String asanaKey) {
        this.asanaKey = asanaKey
        this.messageBuilder = new AlexaMessageBuilder()
    }

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

    AlexaResponse processAction(TaskCO taskCO) {
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

    AlexaResponse processCreate(TaskCO taskCO) {
        if (!taskCO.projectName) {
            messageBuilder.projectNameRequired(taskCO)
        }

        AsanaIntegration asanaIntegration = new AsanaIntegration()

        if (taskCO.taskName) {
            Task task = asanaIntegration.createTask(taskCO)
            messageBuilder.success("Task with id ${task.id} has been created.")
        } else if (taskCO.projectName) {
            Project project = asanaIntegration.findOrCreateProject(taskCO)
            messageBuilder.success("Project with id ${project.id} has been created.")
        } else if (taskCO.tagName) {
            Tag tag = asanaIntegration.findOrCreateTag(taskCO.tagName, taskCO.workspaceName)
            messageBuilder.success("Tag with id ${tag.id} has been created.")
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
*/

        /* Task demoTask = client.tasks
                 .createInWorkspace(workspace.id)
                 .data("name", taskCO.taskName)
                 .data("projects", [selectedProject.id])
                 .data("tags", [selectedTag.id])
                 .execute()*/

        List<Task> tasks = client.tasks.findByProject(selectedProject.id) as ArrayList

        Task demoTask = tasks.find { it.name.equalsIgnoreCase(taskCO.taskName) }

        client.tasks
                .addTag(demoTask.id)
                .data("tag", selectedTag.id)
                .execute()


        /*if (taskCO.taskName && taskCO.tagName && taskCO.email) {
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

    def createTask(String projectStr, String taskStr) {
        //Check if Task not exist then create and return

        Project project = findOrCreateProject(projectStr)
        if (!project) {
            return null
        }
        Task task = client.tasks.createInWorkspace(project.id)
                .data("name", taskCO.taskName)
                .data("projects", [project.id])
                .execute();

        return task
    }

    Project findOrCreateProject(String workspaceName, String projectName) {
        Client client = Client.accessToken(this.asanaKey)
        Workspace availableWorkSpace = findWorkSpace(workspaceName, client);

        if (!availableWorkSpace) {
            return null
        }

        Project project = findProject(availableWorkSpace, projectName, client);
        if (project) {
            return project
        }
        return client.projects.createInWorkspace(availableWorkSpace.id)
                .data("name", projectName)
                .execute();
    }

    Workspace findWorkSpace(String workSpace, Client client) {
        CollectionRequest<Workspace> workspaceList = client.workspaces.findAll()
        def availableWorkSpace = workspaceList ? workspaceList.find { it.name?.equals(workSpace) } : null
        return availableWorkSpace ? availableWorkSpace as Workspace : null
    }

    Project findProject(Workspace workspace, String projectName, Client client) {
        List<Project> projects = client.projects.findByWorkspace(workspace.id).execute();
        Project project = projects ? projects.find { it.name?.equals(projectName) } : null
        return project
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
