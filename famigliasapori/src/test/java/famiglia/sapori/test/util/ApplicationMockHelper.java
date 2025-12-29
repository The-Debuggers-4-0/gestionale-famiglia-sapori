package famiglia.sapori.test.util;

import famiglia.sapori.FamigliaSaporiApplication;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;

/**
 * Classe di utilita per il mocking dei campi statici di FamigliaSaporiApplication durante i test.
 */
public class ApplicationMockHelper {

    private static void setSceneField(Scene scene) throws Exception {
        Field sceneField = FamigliaSaporiApplication.class.getDeclaredField("scene");
        sceneField.setAccessible(true);
        sceneField.set(null, scene); // null because it's static
    }

    /** 
     * Configura una scena mock nel campo scene di FamigliaSaporiApplication usando reflection.
     * Questo permette ai controller di chiamare FamigliaSaporiApplication.setRoot() senza NullPointerException.
     *
     * @param stage the TestFX stage to associate with the mock scene
     * @throws Exception if reflection fails
     */
    public static void setupMockScene(Stage stage) throws Exception {
        Scene mockScene = stage.getScene();
        if (Platform.isFxApplicationThread()) {
            // Evita deadlock: siamo già sul thread FX
            setSceneField(mockScene);
            return;
        }

        // La manipolazione della scena JavaFX deve avvenire sul thread dell'applicazione FX
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                setSceneField(mockScene);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    /**
     * Pulisce la scena di mock dopo i test.
     * 
     * @throws Exception if reflection fails
     */
    public static void clearMockScene() throws Exception {
        if (Platform.isFxApplicationThread()) {
            setSceneField(null);
            return;
        }

        // La manipolazione della scena JavaFX deve avvenire sul thread dell'applicazione FX
        CountDownLatch latch = new CountDownLatch(1);
        // Esegui sul thread FX
        Platform.runLater(() -> {
            try {
                setSceneField(null);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });
        // Attendi il completamento
        latch.await();
    }
}
