package evs.bdi.agent;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Start the agent
 */
public class Main extends Application {
    public void start(Stage primaryStage) throws Exception {
        final Pane pane = FXMLLoader.load(getClass().getResource("/gui.fxml"));
        Scene scene = new Scene(pane);
        primaryStage.setScene(scene);
        primaryStage.show();

        PlatformConfiguration config = PlatformConfiguration.getDefaultNoGui();
        config.addComponent("myOwn.PathFinder4BDI.class");
        Starter.createPlatform(config).get();
    }
}