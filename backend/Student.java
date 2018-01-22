package backend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Student {

    private String username;
    private String firstname;
    private String lastname;
    private String full_name;
    private String user_picture_url;
    private String userid;
    private ArrayList<Course> courses = new ArrayList<>();

    public Student(JSONObject userInfo){
        this.username = userInfo.get("username").toString();
        this.firstname = userInfo.get("firstname").toString();
        this.lastname = userInfo.get("lastname").toString();
        this.full_name = userInfo.get("fullname").toString();
        this.user_picture_url = userInfo.get("userpictureurl").toString();
        this.userid = userInfo.get("userid").toString();
    }

    //Getters
    public String getUsername() {
        return this.username;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public String getLastname() {
        return this.lastname;
    }

    public String getFull_name() {
        return this.full_name;
    }

    public String getUser_picture_url() {
        return this.user_picture_url;
    }

    public String getUserid() {
        return this.userid;
    }

    public ArrayList<Course> getCourses() {
        return this.courses;
    }

    /**
     * Gets a specified course (through binary search) given the course's shortname.
     * Will fail if the shortname provided is not present in the course list*
     *
     * @param shortName: String representation of the shortname of the course.
     *
     * @retrun Course: Returns desired course object on successful completion, Returns an empty course object otherwise.
     * */
    public Course getCourseByShortname(String shortName){
        Course result = new Course();

        List<Course> courses = getCourses();
        if(courses.size() != 0){
            Course searchKey = new Course();
            searchKey.setShortname(shortName);
            int index = Collections.binarySearch(courses, searchKey);

            if(index >=0){
                return getCourses().get(index);
            }
        }

        return result;
    }

    //Course Setter
    public void setCourse(JSONObject courseInfo, JSONArray courseContent) {
        this.courses.add(new Course(courseInfo,courseContent));
        sortCourses();
    }

    public void setCourse(JSONObject courseInfo) {
        this.courses.add(new Course(courseInfo));
        sortCourses();
    }

    public void removeCourses() {
        this.courses = new ArrayList<>();
    }

    /**
     * Sorts all courses by shortname.
     * */
    private void sortCourses(){
        Collections.sort(getCourses(),new Comparator<Course>(){
            public int compare(Course c1, Course c2){
                return c1.getShortname().compareToIgnoreCase(c2.getShortname());
            }
        });
    }
}
