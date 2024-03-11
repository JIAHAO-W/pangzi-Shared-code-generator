package com.yupi.maker.meta;

public class MetaExeception extends RuntimeException {


    public MetaExeception(String message) {
        super(message);
    }

    public MetaExeception(String message, Throwable cause) {
        super(message, cause);
    }
}
