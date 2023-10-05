package ru.clevertec.gittaggradleplugin.exception

class AlreadyTaggedException extends RuntimeException {

    AlreadyTaggedException(String message) {
        super(message)
    }

}
