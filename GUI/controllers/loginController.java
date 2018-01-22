package GUI.controllers;

import backend.Core;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class loginController {


    public JFXTextField username;
    public JFXPasswordField password;
    public Label messageLabel;

    public AnchorPane rootPane;
    public ProgressIndicator loginIndi;

    private Executor exec;

    @FXML
    public void initialize(){
        this.exec = Executors.newCachedThreadPool(runnable -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

    }

    public void closeApp(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void login(ActionEvent actionEvent) {

        Task<Boolean> loginTask = new Login(this.username.getText(), this.password.getText());

        loginTask.setOnRunning(runningEvent -> {
            loginIndi.setVisible(true);
            messageLabel.setText("Attempting Login");
        });

        loginTask.setOnSucceeded(successEvent ->{
            loadContentView();
        });

        loginTask.setOnCancelled(cancelledEvent ->{
            loginIndi.setVisible(false);
            messageLabel.setText("Login Failed");
        });

        loginTask.setOnFailed(failedEvent -> {
            loginIndi.setVisible(false);
            messageLabel.setText("Login Failed");
        });
        exec.execute(loginTask);
    }


    private void loadContentView(){
        try{
            Stage primaryStage = (Stage) this.rootPane.getScene().getWindow();
            String viewLocation = "../fxmls/contentView.fxml";
            Parent contentView = FXMLLoader.load(getClass().getResource(viewLocation));
            primaryStage.setScene(new Scene(contentView,400,325));
        }catch(Exception e){
            e.printStackTrace();
            Core.setToken("");
            closeApp(new ActionEvent());
        }
    }

    public void checkLogin(KeyEvent keyEvent) {
        if (keyEvent.getCode().toString().equalsIgnoreCase("enter")){
            login(new ActionEvent());
        }
    }
}

class Login extends Task<Boolean>{

    private String username;
    private String password;

    public Login(String username, String password){
        this.username = username;
        this.password = password;
    }

    @Override
    protected Boolean call() throws Exception {
        if(valid()){
            String token = Core.getTokenHTTP(this.username.trim(), this.password.trim());

            if(!token.equalsIgnoreCase("failed")){
                Core.setToken(token);
                return true;
            }
        }
        cancel();
        return false;
    }

    private boolean valid(){
        try{
            new Integer(this.username);

            if(this.username.length() < 8 ||password.length() == 0){
                return false;
            }
        }catch(Exception e){
            return false;
        }
        return true;
    }
}