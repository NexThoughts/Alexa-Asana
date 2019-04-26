package com.alexaasana.api

import com.alexaasana.co.TaskCO
import com.asana.Client
import com.asana.models.*

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

    Task findTaskById(String task, Client client) {
        return client.tasks.findById(task).execute();
    }

    Task findTaskFromProject(Project project, String taskName, Client client) {
        List<Task> tasks = client.tasks.findByProject(project.id).execute();
        Task task = tasks ? tasks.find { it.name?.equalsIgnoreCase(taskName) } : null
        return task
    }

    Task findTaskByName(TaskCO taskCO, Client client) {
        Workspace workspace = findWorkSpace(taskCO.workspaceName, client)
        User user = findUser(workspace, taskCO.email, client)
        List<Task> tasks = client.tasks.findAll().query("assignee", user.id).query("workspace", workspace.id).execute();
        Task task = tasks ? tasks.find { it.name?.equalsIgnoreCase(taskCO.taskName) } as Task : null
        return task
    }


    Client fetchClient() {
        return Client.accessToken("0/3aa47beefdeb7488e47f1929442e4096");
    }

    Task assignTaskToUser(TaskCO taskCO) {
        Client client = fetchClient()
        Workspace workspace = findWorkSpace(taskCO.workspaceName, client)
        User user = findUser(workspace, taskCO.email, client)
        if (user) {
            Task task = findTaskById(taskCO.taskId, client)
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

    Task addProjectToTask(TaskCO taskCO) {
        Client client = fetchClient()
        Project project = findOrCreateProject(taskCO)
        Task task = findTaskByName(taskCO, client)
        if (project && task) {
            return client.tasks.addProject(task.id)
                    .data("project", project.id)
                    .execute()
        }
        return null
    }

    Task removeProjectFromTask(TaskCO taskCO) {
        Client client = fetchClient()
        Workspace workspace = findWorkSpace(taskCO.workspaceName, client)
        Project project = findProject(workspace, taskCO.projectName, client)
        Task task = project ? findTaskFromProject(project, taskCO.taskName, client) : null
        if (project && task) {
            return client.tasks.removeProject(task.id)
                    .data("project", project.id)
                    .execute()
        }
        return null
    }

}
