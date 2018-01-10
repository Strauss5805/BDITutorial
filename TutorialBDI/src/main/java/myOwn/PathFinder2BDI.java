package myOwn;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalMaintainCondition;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.model.MProcessableElement.ExcludeMode;
import jadex.bdiv3.runtime.impl.PlanFailureException;
import jadex.bridge.component.IExecutionFeature;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentFeature;

@Agent
public class PathFinder2BDI {

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
	protected void move() {
		if (found) {
			return;
		}
		
		if (position.x > target.x) {
			position.x--;
			System.out.println("moved left");
		}
		else if (position.x < target.x) {
			position.x++;
			System.out.println("moved right");
		}
		else if (position.y < target.y) {
			position.y++;
			System.out.println("moved up");
		}
		else if (position.y > target.y) {
			position.y--;
			System.out.println("moved down");
		}
		
		if (position.equals(target)) {
			System.out.println("Position:"+position.toString()+"Target:"+target.toString());
			System.out.println("yayy finish");
			found = true;
		} else {
			System.out.println("Position:"+position.toString()+"Target:"+target.toString());
		}
	}


	@AgentBody
	public void body() {
		bdiFeature.dispatchTopLevelGoal(new MaintainStorageGoal());
	}
}
