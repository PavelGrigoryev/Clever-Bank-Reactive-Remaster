package ru.clevertec.gittaggradleplugin.strategy.impl

import ru.clevertec.gittaggradleplugin.strategy.TagStrategy

class NoTagExistsStrategy implements TagStrategy {

    @Override
    String createTagName(String branchName, String latestTagVersion) {
        latestTagVersion = 'v0.1'
        def branchMap = [
                'dev'   : latestTagVersion,
                'qa'    : latestTagVersion,
                'stage' : "$latestTagVersion-rc",
                'master': latestTagVersion
        ] as HashMap
        branchMap.getOrDefault(branchName, "$latestTagVersion-SNAPSHOT")
    }

}
