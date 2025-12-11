package famiglia.sapori.test.util;

import famiglia.sapori.FamigliaSaporiApplication;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;

/**
 * Utility class to mock static FamigliaSaporiApplication fields for testing.
 */
public class ApplicationMockHelper {

    /**
     * Sets up a mock Scene in FamigliaSaporiApplication.scene field using reflection.
     * This allows controllers to call FamigliaSaporiApplication.setRoot() without NPE.
     *
     * @param stage the TestFX stage to associate with the mock scene
     * @throws Exception if reflection fails
     */
    public static void setupMockScene(Stage stage) throws Exception {
        // JavaFX scene manipulation must happen on FX Application Thread
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                // Get the existing scene from stage
                Scene mockScene = stage.getScene();
                
                // Use reflection to set private static field
                Field sceneField = FamigliaSaporiApplication.class.getDeclaredField("scene");
                sceneField.setAccessible(true);
                sceneField.set(null, mockScene); // null because it's static
                
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        latch.await(); // Wait for Platform.runLater to complete
    }

    /**
     * Clears the mock scene after tests.
     * 
     * @throws Exception if reflection fails
     */
    public static void clearMockScene() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                Field sceneField = FamigliaSaporiApplication.class.getDeclaredField("scene");
                sceneField.setAccessible(true);
                sceneField.set(null, null);
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        latch.await();
    }
}
