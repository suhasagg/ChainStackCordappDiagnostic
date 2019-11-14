package com.nodeDiagnosticInfo.repository;

public class InfoIdentifierExistsException extends IllegalArgumentException {

    public InfoIdentifierExistsException(final String identifier) {
        super("Identifier already exists:" + identifier);
    }
}
