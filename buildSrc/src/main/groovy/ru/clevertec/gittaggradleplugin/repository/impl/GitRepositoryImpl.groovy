package ru.clevertec.gittaggradleplugin.repository.impl

import ru.clevertec.gittaggradleplugin.builder.GitCommandBuilder
import ru.clevertec.gittaggradleplugin.repository.GitRepository

class GitRepositoryImpl implements GitRepository {

    @Override
    String findUncommittedChanges(File projectDir) {
        GitCommandBuilder.builder()
                .git()
                .diff()
                .execute(projectDir)
    }

    @Override
    String findLatestTagVersion(File projectDir) {
        GitCommandBuilder.builder()
                .git()
                .describe()
                .tags()
                .abbrev(0)
                .execute(projectDir)
    }

    @Override
    String findCurrentTagVersion(File projectDir) {
        GitCommandBuilder.builder()
                .git()
                .describe()
                .tags()
                .execute(projectDir)
    }

    @Override
    String findCurrentBranchName(File projectDir) {
        GitCommandBuilder.builder()
                .git()
                .branch()
                .showCurrent()
                .execute(projectDir)
    }

    @Override
    void pushTagToLocalAndOrigin(String tagName, File projectDir) {
        GitCommandBuilder.builder()
                .git()
                .tag()
                .command(tagName)
                .execute(projectDir)
        GitCommandBuilder.builder()
                .git()
                .push()
                .origin()
                .command(tagName)
                .execute(projectDir)
    }

}
