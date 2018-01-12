package evs.bdi.agent;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Start the agent
 */
public class Main extends Application {
    public static GuiController controller;

    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        Scene scene = new Scene(root);
        // terminate process with jadex when window is closed
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.setScene(scene);
        primaryStage.show();

        PlatformConfiguration config = PlatformConfiguration.getDefaultNoGui();
        config.addComponent("myOwn.PathFinder4BDI.class");
        Starter.createPlatform(config).get();
    }

    public static GuiController getController() {
        return controller;
    }
}