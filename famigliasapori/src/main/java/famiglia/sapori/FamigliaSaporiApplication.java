package famiglia.sapori;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import famiglia.sapori.model.Utente;

/**
 * Applicazione principale per il Gestionale Famiglia Sapori
 */
public class FamigliaSaporiApplication extends Application {

    private static Scene scene;
    private static Utente currentUser;

    public static void setCurrentUser(Utente user) {
        currentUser = user;
    }

    public static Utente getCurrentUser() {
        return currentUser;
    }

    @Override
    public void start(Stage stage) throws IOException {
        Scene initialScene = new Scene(loadFXML("HomeView"), 800, 600);
        setScene(initialScene);
        stage.setTitle("Gestionale Famiglia Sapori");
        stage.setScene(initialScene);
        stage.setFullScreen(false);
        stage.setResizable(true);
        stage.show();
    }

    private static void setScene(Scene s) {
        scene = s;
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(FamigliaSaporiApplication.class.getResource("/fxml/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}