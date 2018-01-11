package myOwn;

import evs.bdi.agent.Main;
import jadex.bdiv3.annotation.*;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.model.MProcessableElement.ExcludeMode;
import jadex.bridge.component.IExecutionFeature;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentFeature;

import java.awt.*;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Agent
public class PathFinder4BDI {
    ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
    @AgentFeature
    protected IExecutionFeature execFeature;

    @Belief
    protected boolean foundAllTargets;

    @Belief
    protected Point position;

    @AgentFeature
    protected IBDIAgentFeature bdiFeature;

    protected final Point chargingPosition = new Point(0, 0);

    protected boolean needCharging = false;

    protected int batteryStatus;

    @AgentCreated
    public void init() {
        this.foundAllTargets = false;
        setPosition(new Point(0, 0));
        this.batteryStatus = 100;
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
    protected void move() throws InterruptedException {
        try {
            if (foundAllTargets)
                return;

            do {
                if (batteryStatus <= 0) {
                    System.out.println("AHHHHH CANT MOVE!!!!!!!");
                    Thread.sleep(1000);
                    return;
                }

                if (getDistance(chargingPosition, position) * 2 + 4 > batteryStatus) {
                    System.out.println("Need to charge!");
                    needCharging = true;
                }

                Thread.sleep(600);
                Point nextTarget = null;

                Collection<Point> targets = getTargets();
                if (targets.size() == 0 && !needCharging) {
                    System.out.println("Yayyy! Me nau finish.");
                    foundAllTargets = true;
                    return;
                }

                if (needCharging) {
                    nextTarget = chargingPosition;
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
                System.out.println("Position: (" + position.x + "," + position.y + ") Target: (" + nextTarget.x + "," + nextTarget.y + ") Distance : " + getDistance(position, nextTarget));

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
                        System.out.print("charging battery...");
                        Thread.sleep(1000);
                        batteryStatus = 100;
                        needCharging = false;
                        System.out.println("battery charged");

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

        timer.scheduleWithFixedDelay(() -> {
            ThreadLocalRandom rand = ThreadLocalRandom.current();
            addDirt(new Point(rand.nextInt(10), rand.nextInt(10)));
            foundAllTargets = false;
        }, 0, 2, TimeUnit.SECONDS);

        // this does not work while the plan runs because it uses the same thread
//        execFeature.repeatStep(0, 2000, ia -> {
//            ThreadLocalRandom rand = ThreadLocalRandom.current();
//            addDirt(new Point(rand.nextInt(10), rand.nextInt(10)));
//            foundAllTargets = false;
//
//            return IFuture.DONE;
//        });
    }

    protected void setPosition(Point position) {
        this.position = position;
        Main.getController().setPosition(position);
    }

    private void moveLeft() {
        setPosition(new Point(position.x - 1, position.y));
        batteryStatus = batteryStatus - 2;
        System.out.println("moved left, battery " + batteryStatus + ", distance to charging: " + getDistance(position, chargingPosition));
    }

    private void moveRight() {
        setPosition(new Point(position.x + 1, position.y));
        batteryStatus = batteryStatus - 2;
        System.out.println("moved right, battery " + batteryStatus + ", distance to charging: " + getDistance(position, chargingPosition));
    }

    private void moveUp() {
        setPosition(new Point(position.x, position.y - 1));
        batteryStatus = batteryStatus - 2;
        System.out.println("moved up, battery " + batteryStatus + ", distance to charging: " + getDistance(position, chargingPosition));
    }

    private void moveDown() {
        setPosition(new Point(position.x, position.y + 1));
        batteryStatus = batteryStatus - 2;
        System.out.println("moved down, battery " + batteryStatus + ", distance to charging: " + getDistance(position, chargingPosition));
    }

    private static void addDirt(Point p) {
        Main.getController().addShit(p);
    }

    private static int getDistance(Point p1, Point p2) {
        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
    }

    private static Set<Point> getTargets() {
        return Main.getController().getShitPositions();
    }

    private static void setCurrentTarget(Point p) {
        Main.getController().setCurrentTarget(p);
    }
}
