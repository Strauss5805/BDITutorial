package evs.bdi.agent;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX-Einstiegspunkt
 */
public class Main extends Application {
    private static GuiController controller;

    /**
     * Startet das Programm mit GUI und die Jadex-Plattform
     */
    public void start(Stage primaryStage) throws Exception {
        // load and init the fxml document
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        // create and show the JavaFX gui
        Scene scene = new Scene(root);
        // terminate process with jadex when window is closed
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.setScene(scene);
        primaryStage.show();

        // launch the Jadex Platform with our bdi agent
        PlatformConfiguration config = PlatformConfiguration.getDefaultNoGui();
        config.addComponent("evs.bdi.agent.PathFinder4BDI.class");
        Starter.createPlatform(config).get();
    }

    /**
     * Gibt den controller der GUI zurück.
     */
    public static GuiController getController() {
        return controller;
    }
}