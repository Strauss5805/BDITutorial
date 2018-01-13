package evs.bdi.agent;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;

import java.awt.*;
import java.util.Collections;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GuiController {
    public GridPane boardGrid;
    public Button addDirtButton;
    public BorderPane background;
    public ProgressBar batteryBar;

    private StackPane[][] panes = new StackPane[10][10];
    private StackPane currentTargetPane = null;

    private ImageView robotView;
    private Image dirtImage;
    private ObjectProperty<Point> robotPos = new SimpleObjectProperty<>(new Point(4, 4));
    private ConcurrentHashMap<Point, ImageView> dirtPositions = new ConcurrentHashMap<>();

    public void initialize() {
        dirtImage = new Image(getClass().getResourceAsStream("/dirt.png"));
        robotView = new ImageView(new Image(getClass().getResourceAsStream("/roboter.png")));
        robotView.setPreserveRatio(false);

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                StackPane pane = new StackPane();
                pane.setBorder(new Border(new BorderStroke(Paint.valueOf("#aaaaaa"), BorderStrokeStyle.SOLID, null, BorderStroke.THIN)));
                pane.setPrefSize(50, 50);
                panes[i][j] = pane;
                boardGrid.add(pane, i, j);
            }
        }

        ImageView chargeView = new ImageView(new Image(getClass().getResourceAsStream("/charge.png")));
        panes[0][0].getChildren().add(chargeView);

        addDirtButton.setOnMouseClicked(event -> {
            addDirtRandomly();
        });

        robotPos.addListener((observable, oldValue, newValue) -> {
            StackPane pane = panes[newValue.x][newValue.y];
            pane.getChildren().remove(robotView);
//            pane.setBackground(new Background(new BackgroundFill(Paint.valueOf("red"), null, null)));
            pane.getChildren().add(robotView);
            pane.setPadding(Insets.EMPTY);

            for (Point dirtPosition : dirtPositions.keySet()) {
                if (newValue.equals(dirtPosition)) {
                    panes[dirtPosition.x][dirtPosition.y].getChildren().remove(dirtPositions.get(dirtPosition));
                    dirtPositions.remove(dirtPosition);
                }
            }
        });
    }

    public void addDirt(Point p) {
        StackPane stackPane = panes[p.x][p.y];
        if (stackPane.getChildren().size() > 0) {
            // do not add to fields were already an image is present
            return;
        }
        if (isOutsideField(p)) {
            throw new IllegalArgumentException("x and y outside of bounds");
        }

        Platform.runLater(() -> {
            ImageView dirtView = new ImageView(dirtImage);
            stackPane.getChildren().add(dirtView);
            dirtPositions.put(p, dirtView);
        });
    }

    public void addDirtRandomly() {
        Random r = new Random();
        Point p;
        while (true) {
            p = new Point(r.nextInt(10), r.nextInt(10));
            StackPane stackPane = panes[p.x][p.y];

            if (stackPane.getChildren().size() == 0) {
                addDirt(p);
                return;
            }
        }
    }

    public void setPosition(Point p) {
        if (isOutsideField(p)) {
            throw new IllegalArgumentException("cant move out of bounds");
        }
        Platform.runLater(() -> robotPos.setValue(p));
    }

    public Point getRobotPosition() {
        return robotPos.get();
    }

    public Set<Point> getDirtPositions() {
        return Collections.unmodifiableSet(dirtPositions.keySet());
    }

    private static boolean isOutsideField(Point p) {
        return !(p.x >= 0 && p.x < 10 && p.y >= 0 && p.y < 10);
    }

    public void setCurrentTarget(Point p) {
        if (isOutsideField(p))
            throw new IllegalArgumentException("point is not on field");

        Platform.runLater(() -> {
            if (currentTargetPane != null)
                currentTargetPane.setBackground(null);

            currentTargetPane = panes[p.x][p.y];
            currentTargetPane.setBackground(new Background(new BackgroundFill(Paint.valueOf("#ffaaaa"), null, null)));
        });
    }

    public void setBatteryProperty(IntegerProperty batteryProperty) {
        batteryProperty.addListener((ob, ov, newValue) -> {
            Platform.runLater(() -> batteryBar.setProgress(newValue.doubleValue() / 100));
        });
    }
}
