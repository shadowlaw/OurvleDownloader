package backend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Course implements Comparable<Course> {

    private String courseID;
    private String shortname;
    private String fullname;
    private String enrollmentCount;
    private String courseIDNumber;
    private ArrayList<String> fileNames;
    private Map<String, String> filePaths;


    public Course(){
        this.courseID = "";
        this.shortname = "";
        this.fullname = "";
        this.enrollmentCount = "";
        this.courseIDNumber = "";
        this.filePaths = new HashMap<>();
        this.fileNames = new ArrayList<>();
    }

    public Course(JSONObject courseInfo){
        this.courseID = courseInfo.get("id").toString();
        this.shortname = courseInfo.get("shortname").toString();
        this.fullname = courseInfo.get("fullname").toString();
        this.enrollmentCount = courseInfo.get("enrolledusercount").toString();
        this.courseIDNumber = courseInfo.get("idnumber").toString();
        this.filePaths = new HashMap<>();
        this.fileNames = new ArrayList<>();
    }

    public Course(JSONObject courseInfo,JSONArray courseContent){
        this.courseID = courseInfo.get("id").toString();
        this.shortname = courseInfo.get("shortname").toString();
        this.fullname = courseInfo.get("fullname").toString();
        this.enrollmentCount = courseInfo.get("enrolledusercount").toString();
        this.courseIDNumber = courseInfo.get("idnumber").toString();
        if (courseContent.length() != 0){
            courseContentPathBuilder(courseContent);
        }
    }


    private void courseContentPathBuilder(JSONArray courseContent){

        JSONObject topic;
        JSONArray modules;
        JSONObject modContent;
        JSONArray modFileContents;
        JSONObject modFileContent;
        String filePath;
        this.fileNames = new ArrayList<>();
        this.filePaths = new HashMap<>();

        for(int j = 0; j< courseContent.length(); j++){
            topic = courseContent.getJSONObject(j);
            modules = topic.getJSONArray("modules");

            if(modules.length() != 0){
                for(int k = 0; k < modules.length(); k++){
                    modContent = modules.getJSONObject(k);
                    if(modContent.get("modplural").toString().equalsIgnoreCase("files") ||
                            modContent.get("modplural").toString().equalsIgnoreCase("Folders")){
                        modFileContents = modContent.getJSONArray("contents");
                        for(int l = 0; l < modFileContents.length(); l++){
                            modFileContent = modFileContents.getJSONObject(l);
                            if(modFileContent.get("type").toString().equalsIgnoreCase("file")){
                                filePath = this.getShortname().trim()+File.separator+topic.get("name").toString() + File.separator + modFileContent.get("filename").toString();

                                this.fileNames.add(modFileContent.get("filename").toString());
                                this.filePaths.put(modFileContent.get("filename").toString(), filePath+"]"+modFileContent.get("fileurl").toString());
                            }
                        }
                    }
                }
            }
        }
    }

    public ArrayList<String> getFileNames(){
        return this.fileNames;
    }

    public Map<String, String> getFilePaths(){
        return this.filePaths;
    }

    public String getCourseID() {
        return this.courseID;
    }

    public String getShortname() {
        return this.shortname;
    }

    public String getFullname() {
        return this.fullname;
    }

    public String getEnrollmentCount() {
        return this.enrollmentCount;
    }

    public String getCourseIDNumber() {
        return this.courseIDNumber;
    }


    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setEnrollmentCount(String enrollmentCount) {
        this.enrollmentCount = enrollmentCount;
    }

    public void setCourseIDNumber(String courseIDNumber) {
        this.courseIDNumber = courseIDNumber;
    }

    public void setCourseContent(JSONArray courseContent) {
        if(courseContent.length() != 0){
            courseContentPathBuilder(courseContent);
        }
    }

    public boolean isEmpty(){
        return this.courseID.isEmpty();
    }

    @Override
    public int compareTo(Course course) {
        return this.shortname.compareToIgnoreCase(course.getShortname());
    }
}
