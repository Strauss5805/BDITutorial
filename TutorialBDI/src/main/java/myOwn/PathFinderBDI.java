package myOwn;

import java.awt.Point;

import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bdiv3.annotation.GoalResult;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentFeature;

@Agent
public class PathFinderBDI {

	@AgentFeature
	protected IExecutionFeature execFeature;

	@Belief
	protected boolean found;

	@Belief
	protected Point position;

	@AgentFeature
	protected IBDIAgentFeature bdiFeature;

	protected Point target;

	@AgentCreated
	public void init() {
		this.found = false;
		this.target = new Point(5, 5);
		this.position = new Point(0, 0);
	}

	@Goal
	public class MaintainStorageGoal {
		@GoalParameter
		protected Point position;
		@GoalResult
		protected String gword;

		public MaintainStorageGoal(Point start) {
			this.position = start;
		}
	}

	@Plan(trigger = @Trigger(goals = MaintainStorageGoal.class))
	protected void move() {
		if (found) {
			return;
		}

		if (position.x > target.x) {
			position.x--;
			System.out.println("moved left");
		} else if (position.x < target.x) {
			position.x++;
			System.out.println("moved right");
		} else if (position.y < target.y) {
			position.y++;
			System.out.println("moved up");
		} else if (position.y > target.y) {
			position.y--;
			System.out.println("moved down");
		}

		if (position.equals(target)) {
			System.out.println("Position:" + position.toString() + "Target:" + target.toString());
			System.out.println("yayy finish");
			found = true;
		} else {
			System.out.println("Position:" + position.toString() + "Target:" + target.toString());
		}
	}

	@AgentBody
	public void body() {

		Point start = new Point(0, 0);
		String gword = (String) bdiFeature.dispatchTopLevelGoal(new MaintainStorageGoal(start)).get();


		// bdiFeature.dispatchTopLevelGoal(new MaintainStorageGoal());

	}
}
