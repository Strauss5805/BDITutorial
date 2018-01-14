package evs.bdi.agent;

import jadex.bdiv3.annotation.*;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.model.MProcessableElement.ExcludeMode;
import jadex.bridge.component.IExecutionFeature;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentFeature;
import javafx.beans.property.SimpleIntegerProperty;

import java.awt.*;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Agent
public class PathFinder4BDI {
    /**
     * Die Position der Aufladestation.
     */
    private final Point chargingPosition = new Point(0, 0);

    /**
     * Zeigt an, ob Aufladen nötig ist.
     */
    private boolean needCharging = false;

    /**
     * Feld für den Akkustand des Roboters. Ist vom Typ SimpleIntegerProperty, damit die Änderungen in der GUI angezeigt
     * werden.
     */
    private SimpleIntegerProperty batteryStatus = new SimpleIntegerProperty(100);
    /**
     * Referenz zur GUI.
     */
    private final GuiController gui;

    @Belief
    private boolean foundAllTargets;

    @Belief
    private Point position;

    @AgentFeature
    private IBDIAgentFeature bdiFeature;
    @AgentFeature
    protected IExecutionFeature execFeature;


    public PathFinder4BDI() {
        gui = Main.getController();
        gui.setBatteryProperty(batteryStatus);
    }

    @AgentCreated
    public void init() throws InterruptedException {
        this.foundAllTargets = false;
        setPosition(new Point(0, 0), "start");
    }

    @Goal(excludemode = ExcludeMode.Never)
    public class MaintainStorageGoal {
        @GoalMaintainCondition(beliefs = "position")
        protected boolean maintain() {
            return foundAllTargets;
        }

        @GoalTargetCondition(beliefs = "position")
        protected boolean target() {
            return foundAllTargets;
        }
    }

    @Plan(trigger = @Trigger(goals = MaintainStorageGoal.class))
    protected void findDirtPlan() {
        try {
            if (foundAllTargets)
                return;

            do {
                if (getDistance(chargingPosition, position) * 2 + 4 > batteryStatus.get()) {
                    System.out.println("Akku bald leer!");
                    needCharging = true;
                }

                Thread.sleep(500);
                Point nextTarget = null;

                Collection<Point> targets = getTargets();

                if (needCharging) {
                    nextTarget = chargingPosition;
                } else if (targets.size() == 0) {
                    System.out.println("Yayyy! Me nau finish.");
                    foundAllTargets = true;
                    return;
                } else {
                    int minDistance = Integer.MAX_VALUE;
                    for (Point target : targets) {
                        int distance = getDistance(position, target);
                        if (distance < minDistance) {
                            nextTarget = target;
                            minDistance = distance;
                        }
                    }
                }

                setCurrentTarget(nextTarget);

                if (position.x > nextTarget.x) {
                    moveLeft();
                } else if (position.x < nextTarget.x) {
                    moveRight();
                } else if (position.y < nextTarget.y) {
                    moveDown();
                } else if (position.y > nextTarget.y) {
                    moveUp();
                }

                if (position.equals(nextTarget)) {
                    if (nextTarget.equals(chargingPosition)) {
                        System.out.print("Akku laden... ");
                        Thread.sleep(1000);
                        batteryStatus.setValue(100);
                        needCharging = false;
                        System.out.println("Auf 100% geladen!");

                    } else if (targets.size() == 0) {
                        System.out.println("Yayyy! Me nau finish.");
                        foundAllTargets = true;
                    }
                }
            } while (!foundAllTargets);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AgentBody
    public void body() {
        bdiFeature.dispatchTopLevelGoal(new MaintainStorageGoal());

        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(this::addDirtRandomly, 0, 2, TimeUnit.SECONDS);

        // this does not work while the plan runs because it uses the same thread
//        execFeature.repeatStep(0, 2000, ia -> {
//            ThreadLocalRandom rand = ThreadLocalRandom.current();
//            addDirt(new Point(rand.nextInt(10), rand.nextInt(10)));
//            foundAllTargets = false;
//
//            return IFuture.DONE;
//        });
    }

    /*
             AB HIER NICHTS BEARBEITEN!!!!!!
     */

    /**
     * Bewegt den Roboter nach links.
     */
    private void moveLeft() throws InterruptedException {
        setPosition(new Point(position.x - 1, position.y), "links");
    }

    /**
     * Bewegt den Roboter nach rechts.
     */
    private void moveRight() throws InterruptedException {
        setPosition(new Point(position.x + 1, position.y), "rechts");
    }

    /**
     * Bewegt den Roboter nach oben.
     */
    private void moveUp() throws InterruptedException {
        setPosition(new Point(position.x, position.y - 1), "oben");
    }

    /**
     * Bewegt den Roboter nach unten.
     */
    private void moveDown() throws InterruptedException {
        setPosition(new Point(position.x, position.y + 1), "unten");
    }

    /**
     * Setzt die Position des Staubsaugerroboters. NICHT DIREKT BENUTZEN! Nutze die Funktionen moveUp etc.
     *
     * @param position die neue Position.
     */
    private void setPosition(Point position, String direction) throws InterruptedException {
        if (batteryStatus.get() <= 0) {
            System.out.println("AHHHHH AKKU LEER!!! ICH KANN MICH NICHT BEWEGEN!!!!!!!11elf");
            Thread.sleep(1000);
            return;
        }
        gui.setPosition(position);
        this.position = position;
        batteryStatus.setValue(batteryStatus.get() - 2);

        System.out.println("Bewegung nach " + direction + ", Akku " + batteryStatus.get() + "%," +
                " Abstand zur Ladestation: " + getDistance(position, chargingPosition));
    }

    /**
     * Fügt an einer zufälligen unbesetzten Position auf dem Feld eine Portion Dreck hinzu
     */
    private void addDirtRandomly() {
        gui.addDirtRandomly();
        foundAllTargets = false;
    }

    /**
     * Berechnet die Anzahl der nötigen Schritte, um von einem Punkt zu einem anderen zu kommen (Manhattan-Distanz).
     *
     * @param p1 der erste Punkt
     * @param p2 der zweite Punkt
     * @return Anzahl der Schritte
     */
    private static int getDistance(Point p1, Point p2) {
        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
    }

    /**
     * Holt eine Liste der Ziele (also Dreckshaufen).
     */
    private Set<Point> getTargets() {
        return gui.getDirtPositions();
    }

    /**
     * Setzt das aktuelle Ziel, damit es in der GUI markiert wird.
     *
     * @param p der Zielpunkt
     */
    private void setCurrentTarget(Point p) {
        if (p == null) {
            throw new IllegalArgumentException("Point p must not be null");
        }
        gui.setCurrentTarget(p);
    }
}
