package ru.clevertec.gittaggradleplugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import ru.clevertec.gittaggradleplugin.service.impl.GitTagServiceImpl

import static ru.clevertec.gittaggradleplugin.constant.GitTagPluginConstants.GIT
import static ru.clevertec.gittaggradleplugin.constant.GitTagPluginConstants.LOGO

class GitTagPlugin implements Plugin<Project> {

    def gitTagService = new GitTagServiceImpl()

    @Override
    void apply(Project project) {
        project.tasks.register('gitTag') {
            group = GIT
            doFirst {
                println LOGO
            }
            doLast {
                def projectDir = project.projectDir
                gitTagService.pushTagByProjectDir(projectDir)
            }
        }
    }

}
