package backend;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Provides basic file management methods such as creating, reading from and writing to and downloading of a single file.
 */
public class FileManager extends SSLTrust{


    //File objects to work on.
    private File file;

    /**
     * File manager constructor assigning a file object to the instance variable, file.
     *
     * @param filePath: a string variable holding the absolute file path of an existing or non-existing file.
     * */
    public FileManager(String filePath){
        super();
        this.file = new File(filePath);
    }

    /**
     * Creates a new file in the current filesystem using the file path passed in the constructor.
     * The method will fail if the file already exists.
     *
     * @return boolean: true on successful creation of the file, false if failure occurs.
     * */
    public boolean createFile(){
        try{
            if(this.file.getParent() != null){
                new File(this.file.getParent()).mkdirs();
            }
            return this.file.createNewFile();
        }catch(Exception e){
            System.out.println(e.getMessage());
            return false;
        }

    }

    /**
     * Reads the data from the file path given to the constructor. returns the data read from the file as a string
     *
     * @return String: Returns the string representation of the file contents on success. Will return empty string on failure to read file.
     * */
    public String readFile(){
        if(!fileExists()){
            return "";
        }

        try{
            BufferedReader fileBuffer = new BufferedReader(new FileReader(this.file.toString()));
            String line;
            StringBuilder response = new StringBuilder();

            while((line = fileBuffer.readLine()) != null){
                response.append(line);
            }

            fileBuffer.close();
            if(response.toString().isEmpty()){
                return "Empty";
            }
            return response.toString();
        }catch(Exception e){
            System.out.println(e.getMessage());
            return "";
        }
    }

    /**
     * Writes String data to a file. The method will write the data to the path specified when creating the FileManager object.
     * This method will always append to the end of the file located at the specified file path.
     *
     *
     * If the file path provided at the creation of the object does not exist on the file system, the method will create the file
     * then write the string data passed.
     *
     *
     * @param data: A string of data to be written.
     *
     * @return boolean: returns true on successful write to the file, false otherwise.
     * */
    public boolean writeLineToFile(String data){
        if(!fileExists()){
            createFile();
        }

        try{
            FileWriter fileWriter = new FileWriter(this.file,true);
            fileWriter.write(data);
            fileWriter.close();
            return true;
        }catch(Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * Moves a file from the file path provided on creation of the object to a destination file path.
     * The file will overwrite the file at the destination file path if the destination file path already exists.
     * The method will update the instances file object with the new files location on completion of the move.
     * The method will fail if the instance file does not exist on the file system.
     *
     * @param dest: a string containing the destination file path.
     *
     * @return boolean: returns true on successful file move, false otherwise.
     * */
    public boolean moveFile(String dest){

        if(!fileExists()){
            return false;
        }

        Path srcPath = Paths.get(toString());
        Path destPath = Paths.get(dest);

        try{
            Files.move(srcPath,destPath, StandardCopyOption.REPLACE_EXISTING);
            setFile(destPath.toString());
            return true;
        }catch(Exception e){
            return false;
        }

    }


    /**
     * Downloads the file specified at a given url and stored it at the instance file path specified.
     * If instance file path does not exist on the file system, then it is created and the file at the specified url is
     * downloaded into it.
     *
     * The method will fail if internet access is not available.
     *
     * @param fileAbsoluteURL: Absolute url of the file to be downloaded.
     * @return boolean: Returns true on successful download, false otherwise.
     * */
    public boolean download(String fileAbsoluteURL){

        if(!fileExists()){
            createFile();
        }

        try{
            URL fileURL  = new URL(fileAbsoluteURL);
            HttpURLConnection downloadConnection = (HttpURLConnection) fileURL.openConnection();
            ReadableByteChannel downloadChannel = Channels.newChannel(downloadConnection.getInputStream());
            FileOutputStream fileWriter = new FileOutputStream(toString());

            fileWriter.getChannel().transferFrom(downloadChannel,0, Long.MAX_VALUE);

            return true;
        }catch(Exception e){
            System.out.println(e.getCause());
            return false;
        }
    }

    /**
     * Deletes the file specified at the instance file path it its exists.
     *
     * method will not do anything if file does not exist.
     *
     * @return boolean: Returns true on successful file delete, false otherwise.
     * */
    public boolean deleteFile(){
        return this.file.delete();
    }

    /**
     * This method tests wheres the instance file path exists.
     *
     * @return boolean: Returns true if file exists on the file system, false otherwise.
     * */
    public boolean fileExists(){
        return this.file.exists();
    }

    /**
     * Gets the filename at the path specified when creating the FileManager instance.
     *
     * @return String: Returns the file name at the instance file
     * */
    public String getName(){
        return this.file.getName();
    }

    public void setFile(String absoluteFilePath){
        this.file =  new File(absoluteFilePath);
    }

    /**
     * Returns the file path specified at the creation of the FileManager instance.
     *
     * @return String: Returns the file path specified at the creation of the FileManager instance.*/
    public String toString()
    {
        return this.file.toString();
    }
}
