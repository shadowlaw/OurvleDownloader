package backend;

import org.json.JSONObject;
import java.io.File;

public class ConfigManager {

    private static final String configPath = "config.txt";
    private static FileManager configFile = new FileManager(configPath);

    public static boolean createConfig(){

        if(configFile.fileExists()){
            configFile.deleteFile();
        }

        configFile.createFile();

        JSONObject configData = new JSONObject();
        configData.put("token","");
        configData.put("contentBasePath",System.getProperty("user.home")+ File.separator+"Documents"+File.separator+"Course Content");
        return configFile.writeLineToFile(configData.toString());
    }

    public static boolean setConfigProperty(String propKey, String propValue){
        try{
            String configString = readConfig();
            configFile.deleteFile();
            JSONObject configData = new JSONObject(configString);
            configData.remove(propKey);
            configData.put(propKey,propValue);
            return configFile.writeLineToFile(configData.toString());
        }catch (Exception e){
            return false;
        }

    }

    public static String readConfig(){
        if(!configFile.fileExists()){
            createConfig();
        }

        return configFile.readFile();
    }

}
