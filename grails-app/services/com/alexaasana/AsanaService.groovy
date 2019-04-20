package com.alexaasana

import com.alexaasana.co.TaskCO
import com.asana.Client
import com.asana.models.Project
import com.asana.models.Task
import com.asana.models.Workspace
import com.asana.requests.CollectionRequest
import grails.gorm.transactions.Transactional

@Transactional
class AsanaService {

    def grailsApplication

    Task createTask(TaskCO taskCO) {
        Client client = fetchClient()
        Workspace availableWorkSpace = findWorkSpace(taskCO.workspaceName, client);

        if (!availableWorkSpace) {
            return null
        }


        Project project = findProject(availableWorkSpace, taskCO.projectName, client);

        if (!project) {
            project = client.projects.createInWorkspace(availableWorkSpace.id)
                    .data("name", taskCO.projectName)
                    .execute();
        }

        Task task = client.tasks.createInWorkspace(project.id)
                .data("name", taskCO.task)
                .data("projects", [project.id])
                .execute();

        return task
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

    Client fetchClient() {
        return Client.accessToken(grailsApplication.config.asana.access_token);
    }
}
