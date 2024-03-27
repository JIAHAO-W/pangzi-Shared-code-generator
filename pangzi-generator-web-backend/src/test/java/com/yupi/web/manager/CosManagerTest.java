package com.yupi.web.manager;

import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class CosManagerTest {

    @Resource
    private CosManager cosManager;
    @Test
    void deleteObject() {
        cosManager.deleteObject("");
    }

    @Test
    void deleteObjects() {
        cosManager.deleteObjects(Arrays.asList("",""));
    }

    @Test
    void deleteDir() {
        cosManager.deleteDir("");
    }
}