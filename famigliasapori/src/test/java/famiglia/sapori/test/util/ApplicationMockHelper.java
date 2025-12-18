package famiglia.sapori.test.util;

import famiglia.sapori.FamigliaSaporiApplication;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;

/**
 * Utility class to mock static FamigliaSaporiApplication fields for testing.
 */
public class ApplicationMockHelper {

    private static void setSceneField(Scene scene) throws Exception {
        Field sceneField = FamigliaSaporiApplication.class.getDeclaredField("scene");
        sceneField.setAccessible(true);
        sceneField.set(null, scene); // null because it's static
    }

    /**
     * Sets up a mock Scene in FamigliaSaporiApplication.scene field using reflection.
     * This allows controllers to call FamigliaSaporiApplication.setRoot() without NPE.
     *
     * @param stage the TestFX stage to associate with the mock scene
     * @throws Exception if reflection fails
     */
    public static void setupMockScene(Stage stage) throws Exception {
        Scene mockScene = stage.getScene();
        if (Platform.isFxApplicationThread()) {
            // Avoid deadlock: we're already on FX thread.
            setSceneField(mockScene);
            return;
        }

        // JavaFX scene manipulation must happen on FX Application Thread
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
     * Clears the mock scene after tests.
     * 
     * @throws Exception if reflection fails
     */
    public static void clearMockScene() throws Exception {
        if (Platform.isFxApplicationThread()) {
            setSceneField(null);
            return;
        }

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                setSceneField(null);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }
}
