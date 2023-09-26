package com.cognizant.workbench.common;

import org.junit.jupiter.api.Test;

public class CommonTest {

    @Test
    void splitTest(){
        String[] split = "".split(":");
        System.out.println(split);
    }
}
