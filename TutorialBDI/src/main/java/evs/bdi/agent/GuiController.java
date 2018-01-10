package evs.bdi.agent;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;

import java.awt.*;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

public class GuiController {
    public GridPane boardGrid;
    public Button addDirtButton;
    public BorderPane background;

    private StackPane[][] panes = new StackPane[10][10];

    private ImageView robotView;
    private Image shitImage;
    private ObjectProperty<Point> robotPos = new SimpleObjectProperty<>(new Point(4, 4));
    private HashMap<Point, ImageView> shitPositions = new HashMap<>();

    public void initialize() {
        shitImage = new Image(getClass().getResourceAsStream("/shit.png"));
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

        addDirtButton.setOnMouseClicked(event -> {
            StackPane pane = panes[new Random().nextInt(10)][new Random().nextInt(10)];
            pane.getChildren().remove(robotView);
            pane.setBackground(new Background(new BackgroundFill(Paint.valueOf("red"), null, null)));
            pane.getChildren().add(robotView);
            pane.setPadding(Insets.EMPTY);
        });

        robotPos.addListener((observable, oldValue, newValue) -> {
            for (Point shitPosition : shitPositions.keySet()) {
                if (newValue.equals(shitPosition)) {
                    panes[shitPosition.x][shitPosition.y].getChildren().remove(shitPositions.get(shitPosition));
                    shitPositions.remove(shitPosition);
                }
            }
        });
    }

    public void addShit(int x, int y) {
        StackPane stackPane = panes[x][y];
        if (stackPane.getChildren().size() > 0) {
            return;
        }
        if (isOutsideField(x, y)) {
            throw new IllegalArgumentException("x and y outside of bounds");
        }

        ImageView shitView = new ImageView(shitImage);
        stackPane.getChildren().add(shitView);
        shitPositions.put(new Point(x, y), shitView);
    }

    public void move(int offsetX, int offsetY) {
        Point currentPos = robotPos.get();
        if (isOutsideField(currentPos.x + offsetX, currentPos.y + offsetY)) {
            throw new IllegalArgumentException("cant move out of bounds");
        }
        robotPos.setValue(new Point(currentPos.x + offsetX, currentPos.y + offsetY));
    }

    public Point getRobotPosition() {
        return robotPos.get();
    }

    public Set<Point> getShitPositions() {
        return shitPositions.keySet();
    }

    private static boolean isOutsideField(int x, int y) {
        return !(x >= 0 && x < 10 && y >= 0 && y < 10);
    }
}
