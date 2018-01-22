package GUI.controllers;

import backend.ConfigManager;
import backend.Core;
import backend.Course;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class contentViewController {

    public AnchorPane contentViewPane;
    public AnchorPane courseListAnchor;
    public GridPane courseList;
    public VBox allCourseReloadContainer;
    public VBox contentLayout;
    private Map<String, String> paths;
    private String shortName;
    private Executor exec;

    @FXML
    public void initialize(){
        SplitPane.setResizableWithParent(this.courseListAnchor, false);

        this.exec = Executors.newCachedThreadPool(runnable -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        loadCourses();
    }

    public void closeApp(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void setContentBasePath(ActionEvent actionEvent) {
        DirectoryChooser baseFileChooser = new DirectoryChooser();
        baseFileChooser.setTitle("Content Download File");
        baseFileChooser.setInitialDirectory(new File(Core.getContentBasePath()));
        File baseFile = baseFileChooser.showDialog(new Stage());

        if(baseFile != null){
            Core.setContentBasePath(baseFile.getAbsolutePath());
            ConfigManager.setConfigProperty("contentBasePath",baseFile.getAbsolutePath());
        }
    }

    public void logout(ActionEvent actionEvent) {
        try{
            Stage primaryStage = (Stage) this.contentViewPane.getScene().getWindow();
            Core.setToken("");
            String viewLocation = "../fxmls/login.fxml";
            Parent contentView = FXMLLoader.load(getClass().getResource(viewLocation));
            primaryStage.setScene(new Scene(contentView,400,325));
        }catch (Exception e){
            e.printStackTrace();
            closeApp(new ActionEvent());
        }
    }

    private void downloadCourse(MouseEvent event) {

        ImageView img = (ImageView) event.getSource();
        VBox imageContainer = (VBox) img.getParent();

        ProgressIndicator downloadProgress = new ProgressIndicator();
        downloadProgress.setPrefSize(21,25);

        imageContainer.getChildren().setAll(downloadProgress);

        Task<Boolean> downloadCourseTask = new DownloadCourse(img, this.courseList);

        downloadCourseTask.setOnRunning(runningEvent -> {
            contentLayout.setAlignment(Pos.CENTER);
            ImageView logo = new ImageView("GUI/images/ourvleLogo.png");
            logo.setFitWidth(200);
            logo.setFitHeight(150);

            contentLayout.getChildren().setAll(logo, new Label("Downloading Course."),
                    new Label("Indicator will stop when download is complete."),
                    new Label("Please select Course for viewing after download is complete.")
            );
        });

        downloadCourseTask.setOnSucceeded(successEvent ->{
            imageContainer.getChildren().setAll(img);
        });

        downloadCourseTask.setOnFailed(failedEvent ->{
            imageContainer.getChildren().setAll(img);
        });

        exec.execute(downloadCourseTask);

    }

    private void loadCourses(){
        Label errorMessage = new Label();
        if (!Core.getUserData()){
            errorMessage.setText("Could not load user. Please Check network connection.");
            this.courseList.addColumn(0, errorMessage);
        }else if(!Core.setAllCourses()){
            errorMessage.setText("No Enrolled Course.");
            this.courseList.addColumn(0, errorMessage);
        }

        ArrayList<Course> courses = Core.getUser().getCourses();

        for(int i=0; i < courses.size();i++){
            Label courseLabel = new Label(courses.get(i).getShortname());
            courseLabel.setId("course"+i);
            courseLabel.setOnMouseClicked(mouseEvent -> {
                loadContent(mouseEvent);
            });

            this.courseList.addColumn(0, courseLabel);
            ImageView downloadImage = new ImageView("GUI/images/download3.png");
            downloadImage.setFitWidth(21);
            downloadImage.setFitHeight(25);
            downloadImage.setOnMouseClicked(event -> {
                downloadCourse(event);
            });
            downloadImage.setId("courseImg"+i);

            VBox courseActionContainer = new VBox(downloadImage);
            courseActionContainer.setAlignment(Pos.CENTER);
            GridPane.setHalignment(courseActionContainer, HPos.CENTER);

            this.courseList.addColumn(1, courseActionContainer);
        }
    }

    public void refreshCourses(MouseEvent mouseEvent){
        Core.getUser().removeCourses();
        courseList.getChildren().setAll();
        loadCourses();
    }

    private void refreshCourse(MouseEvent event){
        String imgId = ((ImageView) event.getSource()).getId();
        String imgNumber = imgId.substring(imgId.length()-1);
        Label courseLabel = (Label) courseList.getScene().lookup("#"+imgId.substring(0, imgId.length()-4)+imgNumber);
        Core.refreshCourseContent(Core.getUser().getCourseByShortname(courseLabel.getText()));

    }

    private void loadContent(MouseEvent mouseEvent){

        String shortName =  ((Label) mouseEvent.getSource()).getText();
        Course selectedCourse = Core.getUser().getCourseByShortname(shortName);
        selectedCourse.setCourseContent(Core.getStudentCourseContent(selectedCourse.getCourseID()));

        ArrayList<String> contentNames = selectedCourse.getFileNames();
        this.paths = selectedCourse.getFilePaths();
        this.shortName = shortName;

        Label courseTitle = new Label(selectedCourse.getFullname());
        contentLayout.setAlignment(Pos.TOP_CENTER);

        if (contentNames.size() ==0 ){
            contentLayout.getChildren().setAll(courseTitle,new Label("No Content."));
        }else{
            GridPane content = new GridPane();
            content.setId("content");
            content.setHgap(3);
            content.setVgap(5);
            content.setPadding(new Insets(10,10,5,10));
            ColumnConstraints column1 = new ColumnConstraints();
            column1.setPercentWidth(80);
            ColumnConstraints column2 = new ColumnConstraints();
            column2.setPercentWidth(20);
            content.getColumnConstraints().addAll(column1, column2);

            for (int i =0; i < contentNames.size(); i++){

                Label contentLabel = new Label(contentNames.get(i));
                contentLabel.setId("content"+i);
                content.addColumn(0, contentLabel);

                ImageView imgview = new ImageView();
                imgview.setFitWidth(21);
                imgview.setFitHeight(25);
                imgview.setId("content"+i+"img");

                if(new File(Core.getContentBasePath()+File.separator+paths.get(contentNames.get(i)).split("]")[0]).exists()){
                    Image image = new Image("GUI/images/open1.png");
                    imgview.setImage(image);
                    imgview.setOnMouseClicked(event -> {
                        systemOpen(event);
                    });
                }else{
                    Image image = new Image("GUI/images/download3.png");
                    imgview.setImage(image);
                    imgview.setOnMouseClicked(event -> {
                        download(event);
                    });
                }

                VBox courseActionContainer = new VBox(imgview);
                courseActionContainer.setAlignment(Pos.CENTER);
                GridPane.setHalignment(courseActionContainer, HPos.CENTER);


                content.addColumn(1, courseActionContainer);
            }

            contentLayout.getChildren().setAll(courseTitle,content);
        }

    }

    private void systemOpen(MouseEvent event){

        ImageView img = (ImageView) event.getSource();
        VBox imgContainer = (VBox) img.getParent();

        ProgressIndicator opening = new ProgressIndicator();
        opening.setPrefSize(21,25);

        imgContainer.getChildren().setAll(opening);

        String imageID = img.getId();
        String labelSearchKey = imageID.substring(0,imageID.length()-3);
        String selectedFileName = ((Label) contentLayout.getScene().lookup("#"+labelSearchKey)).getText();
        String filePath = Core.getContentBasePath()+File.separator+this.paths.get(selectedFileName).split("]")[0];

        Task<Void> openFile = new OpenFile(filePath);

        openFile.setOnSucceeded(successEvent -> {
            imgContainer.getChildren().setAll(img);
        });

        exec.execute(openFile);
    }

    private void download(MouseEvent event){
        ProgressIndicator downloadIndicator = new ProgressIndicator();
        downloadIndicator.setPrefSize(21,25);
        VBox imgContainer = (VBox) ((ImageView) event.getSource()).getParent();
        imgContainer.getChildren().setAll(downloadIndicator);

        ImageView img = ((ImageView) event.getSource());
        String imageID = img.getId();
        String labelSearchKey = imageID.substring(0,imageID.length()-3);
        String selectedFileName = ((Label) contentLayout.getScene().lookup("#"+labelSearchKey)).getText();
        Task <Boolean> downloadTask = new Download(this.shortName, selectedFileName);

        downloadTask.setOnSucceeded(successEvent -> {
            img.setImage(new Image("GUI/images/open1.png"));
            imgContainer.getChildren().setAll(img);
        });

        downloadTask.setOnFailed(failedEvent ->{
            img.setImage(new Image("GUI/images/download3.png"));
            imgContainer.getChildren().setAll(img);
        });

        exec.execute(downloadTask);
    }
}

class Download extends Task<Boolean>{

    private String shortname;
    private String selectedFileName;

    public Download(String shortname, String selectedFileName){
        this.shortname = shortname;
        this.selectedFileName = selectedFileName;
    }

    @Override
    protected Boolean call() throws Exception {
        return Core.download_by_filename(this.shortname, selectedFileName);
    }
}

class DownloadCourse extends Task<Boolean>{

    private ImageView img;
    private GridPane courseList;

    public DownloadCourse(ImageView img, GridPane courseList){
        this.img = img;
        this.courseList = courseList;
    }

    @Override
    protected Boolean call() throws Exception {
        String imgId = img.getId();
        String imgNumber = imgId.substring(imgId.length()-1);
        String shortname = ((Label) courseList.getScene().lookup("#"+imgId.substring(0, imgId.length()-4)+imgNumber)).getText();

        Course selectedCourse = Core.getUser().getCourseByShortname(shortname);

        if (selectedCourse.getFileNames().size() == 0){
            return false;
        }

        Core.download_all_course_content(selectedCourse);

        return true;
    }
}

class OpenFile extends Task<Void>{
    private String filePath;

    public OpenFile(String filePath){
        this.filePath = filePath;
    }

    @Override
    protected Void call() throws Exception {
        try{
            Desktop desktop = Desktop.getDesktop();
            desktop.open(new File(filePath));
        }catch (Exception e){
            System.out.println(e.getCause());
        }
        return null;
    }
}