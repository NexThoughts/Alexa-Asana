package com.alexaasana.api

import com.alexaasana.co.TaskCO
import com.asana.Client
import com.asana.models.Project
import com.asana.models.Tag
import com.asana.models.Task
import com.asana.models.User
import com.asana.models.Workspace
import com.asana.requests.CollectionRequest

class AsanaIntegration {

    Task createTask(TaskCO taskCO) {
        Client client = fetchClient()
        Workspace workspace = findWorkSpace(taskCO.workspaceName, client)
        Project project = findOrCreateProject(taskCO)
        if (!project) {
            return null
        }
        Task task = client.tasks.createInWorkspace(workspace.id)
                .data("name", taskCO.taskName)
                .data("projects", Arrays.asList(project.id))
                .execute();

        taskCO.taskId = task.id

        if (task && taskCO.email) {
            assignTaskToUser(taskCO)
        }

        if (task && taskCO.tagName) {
            assignTagToTask(taskCO)
        }
        removeTagFromTask(taskCO)
        return task
    }

    Tag findOrCreateTag(String tagName, String workSpace) {
        Client client = fetchClient()
        Workspace availableWorkSpace = findWorkSpace(workSpace, client)
        if (!workSpace)
            return null
        List<Tag> tagList = client.tags.findByWorkspace(availableWorkSpace.id).execute()
        Tag tag = tagList ? tagList.find { it.name?.equalsIgnoreCase(tagName) } : null
        if (tag) {
            return tag
        }

        return client.tags.createInWorkspace(availableWorkSpace.id)
                .data("name", tagName)
                .execute();
    }


    Workspace findWorkSpace(String workSpace, Client client) {
        List<Workspace> workspaceList = client.workspaces.findAll().execute()
        def availableWorkSpace = workspaceList ? workspaceList.find { it.name?.equalsIgnoreCase(workSpace) } : null
        return availableWorkSpace ? availableWorkSpace as Workspace : null
    }

    Project findOrCreateProject(TaskCO taskCO) {
        Client client = fetchClient()
        Workspace availableWorkSpace = findWorkSpace(taskCO.workspaceName, client);

        if (!availableWorkSpace) {
            return null
        }

        Project project = findProject(availableWorkSpace, taskCO.projectName, client);
        if (project) {
            return project
        }
        return client.projects.createInWorkspace(availableWorkSpace.id)
                .data("name", taskCO.projectName)
                .execute();
    }


    Project findProject(Workspace workspace, String projectName, Client client) {
        List<Project> projects = client.projects.findByWorkspace(workspace.id).execute();
        Project project = projects ? projects.find { it.name?.equalsIgnoreCase(projectName) } : null
        return project
    }

    User findUser(Workspace workspace, String userName, Client client) {
        List<User> users = client.users.findByWorkspace(workspace.id).execute();
        User user = users ? users.find { it.name?.equalsIgnoreCase(userName) } : null
        return user
    }

    Task findTask(String task, Client client) {
        return client.tasks.findById(task).execute();
    }


    Client fetchClient() {
        return Client.accessToken("0/3aa47beefdeb7488e47f1929442e4096");
    }

    Task assignTaskToUser(TaskCO taskCO) {
        Client client = fetchClient()
        Workspace workspace = findWorkSpace(taskCO.workspaceName, client)
        User user = findUser(workspace, taskCO.email, client)
        if (user) {
            Task task = findTask(taskCO.taskId, client)
            return client.tasks.update(task.id)
                    .data("assignee", user)
                    .execute()
        }
        return null
    }

    Task assignTagToTask(TaskCO taskCO) {
        Client client = fetchClient()
        Tag tag = findOrCreateTag(taskCO.tagName, taskCO.workspaceName)
        if (tag) {
            return client.tasks.addTag(taskCO.taskId)
                    .data("tag", tag.id)
                    .execute()
        }
        return null
    }


    Task removeTagFromTask(TaskCO taskCO) {
        Client client = fetchClient()
        Tag tag = findOrCreateTag(taskCO.tagName, taskCO.workspaceName)
        if (tag) {
            return client.tasks.removeTag(taskCO.taskId)
                    .data("tag", tag.id)
                    .execute()
        }
        return null
    }

}
