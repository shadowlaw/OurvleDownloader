package GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import backend.Core;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Core.initApp();
        if(Core.isTokenValid()){
            loadView(primaryStage,"contentView");
        }else{
            loadView(primaryStage,"login");
        }
    }

    public void loadView(Stage primaryStage, String viewName) throws Exception{
        String viewLocation = "fxmls/"+viewName+".fxml";
        Parent root = FXMLLoader.load(getClass().getResource(viewLocation));
        primaryStage.setTitle("OurVLE Downloader");
        primaryStage.setScene(new Scene(root, 400, 325));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
