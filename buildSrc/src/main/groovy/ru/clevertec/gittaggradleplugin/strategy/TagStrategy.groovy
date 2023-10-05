package ru.clevertec.gittaggradleplugin.strategy

interface TagStrategy {

    String createTagName(String branchName, String latestTagVersion)

}