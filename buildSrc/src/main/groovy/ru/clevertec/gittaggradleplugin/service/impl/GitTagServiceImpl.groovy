package ru.clevertec.gittaggradleplugin.service.impl

import ru.clevertec.gittaggradleplugin.exception.AlreadyTaggedException
import ru.clevertec.gittaggradleplugin.exception.UncommittedChangesException
import ru.clevertec.gittaggradleplugin.repository.impl.GitRepositoryImpl
import ru.clevertec.gittaggradleplugin.service.GitTagService
import ru.clevertec.gittaggradleplugin.strategy.impl.NoTagExistsStrategy
import ru.clevertec.gittaggradleplugin.strategy.impl.TagExistsStrategy

class GitTagServiceImpl implements GitTagService {

    def gitRepository = new GitRepositoryImpl()
    def noTagExistsStrategy = new NoTagExistsStrategy()
    def tagExistsStrategy = new TagExistsStrategy()

    @Override
    void pushTagByProjectDir(File projectDir) {
        def uncommitted = gitRepository.findUncommittedChanges(projectDir)
        if (!uncommitted.isEmpty()) {
            def tagVersion = gitRepository.findCurrentTagVersion(projectDir)
            throw new UncommittedChangesException("Detected uncommitted changes in :\n$tagVersion" + '.uncommitted')
        }
        def latestTagVersion = gitRepository.findLatestTagVersion(projectDir)
        def branchName = gitRepository.findCurrentBranchName(projectDir)
        if (latestTagVersion.isEmpty()) {
            def tagName = noTagExistsStrategy.createTagName(branchName, latestTagVersion)
            gitRepository.pushTagToLocalAndOrigin(tagName, projectDir)
            return
        }
        def currentTagVersion = gitRepository.findCurrentTagVersion(projectDir)
        if (latestTagVersion == currentTagVersion) {
            throw new AlreadyTaggedException("The current state of the project is already tagged $currentTagVersion by git")
        } else {
            def tagName = tagExistsStrategy.createTagName(branchName, latestTagVersion)
            gitRepository.pushTagToLocalAndOrigin(tagName, projectDir)
        }
    }

}
