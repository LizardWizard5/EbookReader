package ca.lizardwizard.ebookclient;

import ca.lizardwizard.ebookclient.Lib.EnvReader;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EnvTest {
    @Test
    public void testFileCreation() throws IOException {
        EnvReader<String> envReader = new EnvReader<>();
        envReader.createFile();
        //Check if file exists
        assertTrue(envReader.checkIfFileExists());

    }
    @Test
    public void testWrite() throws IOException {
        EnvReader<String> envReader = new EnvReader<>();
        BufferedReader br = new BufferedReader(new java.io.FileReader("env.txt"));
        envReader.writeVar("TEST_KEY", "TEST_VALUE");
        String line;
        boolean found = false;
        while((line = br.readLine()) != null) {
            if (line.startsWith("TEST_KEY:TEST_VALUE")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testRead() throws IOException {
        EnvReader<String> envReader = new EnvReader<>();
        BufferedReader br = new BufferedReader(new java.io.FileReader("env.txt"));
        String key = envReader.readVar("TEST_KEY");
        assertEquals(key, "TEST_VALUE");

    }
}
