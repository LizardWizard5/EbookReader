package ca.lizardwizard.ebookclient.Lib;

import java.io.*;
import java.util.ArrayList;

/**
 * By: Eric Wooldridge
 * Description: A util class for reading a text file with the formatting of a KEY:Value, this includes inserting values and reading values. Ignores lines start with #
 */
public class EnvReader<T> {
    private String path = "env.txt";

    /**
     * Reads value associated with key. If key is not found or does not align with data type T, returns null
     * @param key
     * @return Value associated with key, or null if not found or does not align with data type T
     * @throws FileNotFoundException
     */
    public T readVar(String key) throws FileNotFoundException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        try{
            String line;
            while((line = br.readLine()) != null){
                if(line.startsWith("#")){
                    continue;
                }
                String[] parts = line.split(":");
                if(parts.length == 2 && parts[0].trim().equals(key)){
                    return (T) parts[1].trim();
                }
            }
        } catch (IOException e) {

            return null;
        }

        return null;
    }

    public boolean writeVar(String key, T value) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(path));
        BufferedReader br = new BufferedReader(new FileReader(path));
        ArrayList<String> fileContents = new ArrayList<String>();
        int lineIndex = 0;
        String line;
        while((line = br.readLine()) != null){
            if(line.startsWith("#")){
                fileContents.add( line);
                continue;
            }
            String[] parts = line.split(":");
            if(parts.length == 2 && parts[0].trim().equals(key)){
                fileContents.add( (key + ":" + value.toString()));
            } else {
                fileContents.add( line);
            }
        }
        if(lineIndex == fileContents.size()){
            fileContents.add( (key + ":" + value.toString()));
        }
        for(String s : fileContents){
            bw.write(s);
            bw.newLine();
        }
        bw.flush();
        br.close();

        return true;
    }

    public boolean checkIfFileExists(){
        File f = new File(path);
        return f.exists() && !f.isDirectory();
    }
    public  boolean createFile() throws IOException {
        File f = new File(path);
        if(f.exists()){
            return false;
        }
        f.createNewFile();
        return true;
    }

}
