package test;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class JunitTest {

    @Test
    public void testJunit() throws IOException {
        Assert.assertEquals(3, 3);
    }

    @Test
    public void myTest(){
        Assert.assertEquals(true, Character.isLowerCase('ε'));
    }
}
