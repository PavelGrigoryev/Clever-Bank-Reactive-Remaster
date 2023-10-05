package ru.clevertec.gittaggradleplugin.exception

class UncommittedChangesException extends RuntimeException {

    UncommittedChangesException(String message) {
        super(message)
    }

}
