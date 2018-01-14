package evs.bdi.agent;

import jadex.micro.annotation.Agent;

import java.awt.*;
import java.util.Set;

@Agent
public class PathFinderBDI {
    /**
     * Die Position der Aufladestation.
     */
    private final Point chargingPosition = new Point(0, 0);

    /**
     * Referenz zur GUI.
     */
    private final GuiController gui;

    /**
     * Einfacher Konstruktor, der die Referenz zum GUI-Controller setzt.
     */
    public PathFinderBDI() {
        gui = Main.getController();
    }

    /**
     * Gibt die aktuelle Position des Staubsaugerroboters zurück
     */
    private Point getPositionBelief() {
        // Für Aufgabe 1
        return null;
    }

    /**
     * Setzt die Position des Staubsaugerroboters
     *
     * @param position die neue Position
     */
    private void setPositionBelief(Point position) {
        // Für aufgabe 1
    }

    /**
     * Gibt den aktuellen Ladezustand des Akkus zurück.
     */
    private int getBatteryStatus() {
        // Für Aufgabe 3
        return 100;
    }

    /**
     * Setzt den Akkustand auf 100
     */
    private void chargeBattery() {
        // Für Aufgabe 3
    }

    /**
     * Entfernt 2 von der Akkuladung. Wird bei jeder Bewegung aufgerufen.
     */
    private void drainBattery() {
        // Für Aufgabe 3
    }

    /**
     * Fügt an einer zufälligen unbesetzten Position auf dem Feld eine Portion Dreck hinzu
     */
    private void addDirtRandomly() {
        gui.addDirtRandomly();
    }

    // ================================================
    //         AB HIER NICHTS BEARBEITEN!!!!!!
    // ================================================

    /**
     * Does one step in direction to the target.
     *
     * @param target the target point
     */
    private void stepToTarget(Point target) throws InterruptedException {
        Point position = getPositionBelief();
        if (position.x > target.x) {
            moveLeft();
        } else if (position.x < target.x) {
            moveRight();
        } else if (position.y < target.y) {
            moveDown();
        } else if (position.y > target.y) {
            moveUp();
        }

        Thread.sleep(500);

        if (position.equals(target) && target.equals(chargingPosition)) {
            System.out.print("Akku laden... ");
            Thread.sleep(1000);
            chargeBattery();
            System.out.println("Auf 100% geladen!");
        }
    }

    /**
     * Bewegt den Roboter nach links.
     */
    private void moveLeft() throws InterruptedException {
        setPosition(new Point(getPositionBelief().x - 1, getPositionBelief().y), "links");
    }

    /**
     * Bewegt den Roboter nach rechts.
     */
    private void moveRight() throws InterruptedException {
        setPosition(new Point(getPositionBelief().x + 1, getPositionBelief().y), "rechts");
    }

    /**
     * Bewegt den Roboter nach oben.
     */
    private void moveUp() throws InterruptedException {
        setPosition(new Point(getPositionBelief().x, getPositionBelief().y - 1), "oben");
    }

    /**
     * Bewegt den Roboter nach unten.
     */
    private void moveDown() throws InterruptedException {
        setPosition(new Point(getPositionBelief().x, getPositionBelief().y + 1), "unten");
    }

    /**
     * Setzt die Position des Staubsaugerroboters. NICHT DIREKT BENUTZEN! Nutze die Funktionen moveUp etc.
     *
     * @param position die neue Position.
     */
    private void setPosition(Point position, String direction) throws InterruptedException {
        if (getBatteryStatus() <= 0) {
            System.out.println("AHHHHH AKKU LEER!!! ICH KANN MICH NICHT BEWEGEN!!!!!!!11elf");
            Thread.sleep(1000);
            return;
        }
        gui.setPosition(position);
        setPositionBelief(position);
        drainBattery();

        System.out.println("Bewegung nach " + direction + ", Akku " + getBatteryStatus() + "%," +
                " Abstand zur Ladestation: " + getDistance(position, chargingPosition));
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
