package myOwn;

import java.awt.Point;
import java.util.concurrent.ThreadLocalRandom;

import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalMaintainCondition;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.model.MProcessableElement.ExcludeMode;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentFeature;

@Agent
public class PathFinder4BDI {

	@AgentFeature
	protected IExecutionFeature execFeature;

	@Belief
	protected boolean found;

	@Belief
	protected Point position;

	@AgentFeature
	protected IBDIAgentFeature bdiFeature;

	protected Point target;

	protected int batteryStatus;

	@AgentCreated
	public void init() {
		this.found = false;
		this.target = new Point(5, 5);
		this.position = new Point(0, 0);
		this.batteryStatus = 100;
	}

	@Goal(excludemode = ExcludeMode.Never)
	public class MaintainStorageGoal {
		@GoalMaintainCondition(beliefs = "position")
		protected boolean maintain() {
			return found;
		}

		@GoalTargetCondition(beliefs = "position")
		protected boolean target() {
			return found;
		}
	}

	@Plan(trigger = @Trigger(goals = MaintainStorageGoal.class))
	protected void move() throws InterruptedException {
		if (batteryStatus <= 0) {
			System.out.println("AHHHHH CANT MOVE!!!!!!!");
			return;
		}

		if (found)
			return;

		if (batteryStatus < 20) {
			System.out.print("Need to charge!");
			target.x = 0;
			target.y = 0;
		}

		Thread.sleep(600);

		if (position.x > target.x) {
			position.x--;
			batteryStatus = batteryStatus - 2;
			System.out.println("moved left");
		} else if (position.x < target.x) {
			position.x++;
			batteryStatus = batteryStatus - 2;
			System.out.println("moved right");
		} else if (position.y < target.y) {
			position.y++;
			batteryStatus = batteryStatus - 2;
			System.out.println("moved up");
		} else if (position.y > target.y) {
			position.y--;
			batteryStatus = batteryStatus - 2;
			System.out.println("moved down");
		}
		System.out.println("Position:" + position.toString() + "Target:" + target.toString());
		if (position.equals(target)) {
			if (target.x == 0 && target.y == 0) {
				System.out.print("charge battery...");
				Thread.sleep(1000);
				batteryStatus = 100;
				System.out.println("battry charged");
			}
			System.out.println("Yayyy! Me nau finish.");
			found = true;
		}
	}

	@AgentBody
	public void body() {
		bdiFeature.dispatchTopLevelGoal(new MaintainStorageGoal());

		execFeature.repeatStep(0, 4000, new IComponentStep<Void>() {

			public IFuture<Void> execute(IInternalAccess ia) {
				int nextx = ThreadLocalRandom.current().nextInt(-5, 5 + 1);
				int nexty = ThreadLocalRandom.current().nextInt(-5, 5 + 1);
				target.x = nextx;
				target.y = nexty;
				found = false;

				return IFuture.DONE;
			}
		});

	}
}
