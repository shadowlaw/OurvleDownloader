package backend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

/**
 * Backend driver class. This class holds all the functionality of the app and can be added to a GUI or command line
 * representation of the application. a config file, config.txt, is need in the same directory on the file system for
 * token initialization on instantiation.*/
public class Core {
    private static Student user;
    private static HttpConnection ourvleConnect = new HttpConnection();
    private static String ourvleBaseURL = "https://ourvle.mona.uwi.edu/";
    private static String token;
    private static String contentBasePath;

    public static String getContentBasePath() {
        return contentBasePath;
    }

    public static void setContentBasePath(String newPath) {
        contentBasePath = newPath;
    }

    /**
     * Initializes token and other required configurations*/
    public static void initApp(){
        //Opens config file
        String configDataString = ConfigManager.readConfig();

        JSONObject configData = new JSONObject(configDataString);
        token = configData.get("token").toString();
        contentBasePath = configData.get("contentBasePath").toString();
    }

    /**
     * API Token setter*/
    public static void setToken(String newToken){
        token = newToken;
        ConfigManager.setConfigProperty("token",token);
    }

    public static boolean isTokenValid(){
        if(isTokenSet()){
            return getUserData();
        }

        return false;
    }

    /**
     * Returns true if token is not an empty string, false otherwise.
     * */
    public static boolean isTokenSet(){
        return !token.isEmpty();
    }

    /**
     * user variable getter.
     * */
    public static Student getUser() {
        return user;
    }

    /**
     * Gets logged in user's user information. Returns true upon success, false otherwise.
     * .*/
    public static boolean getUserData(){
        String userInfoURL = ourvleBaseURL+"webservice/rest/server.php?wstoken="+token+"&wsfunction=core_webservice_get_site_info&moodlewsrestformat=json";
        String response = ourvleConnect.GET(userInfoURL);

        if(response.isEmpty()){
            return false;
        }

        try{
            new JSONObject(response).get("message");
            return false;
        }catch (Exception e){
            user = new Student(new JSONObject(response));
            return true;
        }
    }


    /**
     * Gets the user's currently enrolled courses.
     *
     * @return JSONArray: Returns the information for all current user enrolled courses on success. Returns an empty JSONArray if method fails..
     * */
    public static JSONArray getStudentCourseInfo(){
        String userCoursesInfoURL = ourvleBaseURL+"webservice/rest/server.php?wstoken="+token+"&wsfunction=core_enrol_get_users_courses&moodlewsrestformat=json&userid="+ user.getUserid();
        String response = ourvleConnect.GET(userCoursesInfoURL);

        if(response.isEmpty()){
            return new JSONArray();
        }

        return new JSONArray(response);

    }

    /**
     * Gets the course content for a particular course given the course id.
     *
     * @param courseID: target course's id number.
     * @return JSONArray: returns empty JSONArray if no courses are available or if method fails due to no network connection.<br/>
     * */
    public static JSONArray getStudentCourseContent(String courseID){
        String response;

        String courseContentURL = ourvleBaseURL+"webservice/rest/server.php?wstoken="+token+"&wsfunction=core_course_get_contents&moodlewsrestformat=json&courseid="+courseID;
        response = ourvleConnect.GET(courseContentURL);

        if (response.isEmpty()){
            return new JSONArray();
        }

        return new JSONArray(response);
    }

    /**
     * Sets the course data for each course the user is current enrolled in.
     *
     * @return boolean: true on method success, false otherwise.
     * */
    public static boolean setAllCourses(){

        JSONArray courseInfo = getStudentCourseInfo();
        JSONObject currentCourseInfo;

        if(courseInfo.length() != 0 ){
            for(int i=0; i < courseInfo.length(); i++){
                currentCourseInfo = courseInfo.getJSONObject(i);
                user.setCourse(currentCourseInfo);
            }
            return true;
        }
        return false;
    }

    /**
     * This method gets the API token for a user given the username and password.
     * Method will fail if no network connection is available or some other fatal error occurs.
     *
     * @param username: String representation of users ID number.
     * @param password: String representation of users password.
     *
     * @return String: Returns user token on success and Failed on method failure.
     * */
    public static String getTokenHTTP(String username, String password){
        String tokenUrl = ourvleBaseURL+"login/token.php?";
        String params = "username="+username.trim()+"&password="+password.trim()+"&service=moodle_mobile_app";
        String response = ourvleConnect.POST(tokenUrl, params);


        if(response.isEmpty()){
            return "failed";
        }

        try {
            return new JSONObject(response).get("token").toString();
        }catch (Exception e){
            return"failed";
        }

    }

    /**
     * Downloads a file given the course's shortname and the filename.
     *
     * @param courseShortname: shortname of the course.
     * @param filename: filename of file to be downloaded.
     *
     * @return boolean: True on method succes, false otherwise.
     * */
    public static boolean download_by_filename(String courseShortname, String filename){
        Map<String, String> filePaths = user.getCourseByShortname(courseShortname).getFilePaths();
        String filePathValue = filePaths.get(filename);
        return download_file(getContentBasePath()+File.separator+filePathValue.split("]")[0],filePathValue.split("]")[1]);
    }

    /**
     * Downloads file given absolute file path and download url.
     *
     * @param absoluteFilePath: filepath from root of the file system to destination filename including file extension.
     * @param downloadURL: string containing url to download file.
     *
     * @return boolean: True if file was successfully downloaded, false otherwise.
     * */
    private static boolean download_file (String absoluteFilePath, String downloadURL){
        FileManager CurrentContent = new FileManager(absoluteFilePath);
        boolean result = false;

        if (!CurrentContent.fileExists()){
            result = CurrentContent.download(downloadURL+"&token="+token);
        }

        return result;
    }

    /**
     * Downloads all files for a given course.
     *
     * @param course: A course object containing all course information.
     *
     * @return boolean: true on completion, false otherwise.
     * */
    public static boolean download_all_course_content(Course course){
        Map<String, String> filePaths = course.getFilePaths();

        if(filePaths.size() !=0 ){
            filePaths.forEach((k,v)->{
                String absoluteFilePath = contentBasePath+File.separator+v.split("]")[0];
                String downloadURL = v.split("]")[1];
                download_file(absoluteFilePath, downloadURL);
            });
        }

        return true;
    }

    /**
     * Downloads all files for all enrolled courses.
     *
     * @return boolean: true on completion, false otherwise.
     * */
    public static boolean download_all_courses(){
        ArrayList<Course> courses = user.getCourses();
        if (courses.size() == 0 ){
            return false;
        }

        for(int i=0; i < courses.size(); i++){
            download_all_course_content(courses.get(i));
        }
        return true;
    }

    /**
     * sets course content for given course from API.
     * */
    public static void refreshCourseContent(Course course){
        if(!course.isEmpty()){
            course.setCourseContent(getStudentCourseContent(course.getCourseID()));
        }
    }
}
