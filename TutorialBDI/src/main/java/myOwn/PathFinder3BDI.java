package myOwn;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalMaintainCondition;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bdiv3.annotation.GoalResult;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.model.MProcessableElement.ExcludeMode;
import jadex.bdiv3.runtime.impl.PlanFailureException;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentFeature;
import myOwn.PathFinder2BDI.MaintainStorageGoal;

@Agent
public class PathFinder3BDI {

	@Belief
	protected boolean found;

	@Belief
	protected Point position;

	@AgentFeature
	protected IBDIAgentFeature bdiFeature;

	protected Point target;


	@Belief
	protected Map<String, String> wordtable;

	@AgentCreated
	public void init() {
		this.wordtable = new HashMap<String, String>();
		wordtable.put("coffee", "Kaffee");
		wordtable.put("milk", "Milch");
		wordtable.put("cow", "Kuh");
		wordtable.put("cat", "Katze");
		wordtable.put("dog", "Hund");
		
		this.found = false;
		this.target = new Point(5, 5);
		this.position = new Point(0, 0);
	}

	@Goal(excludemode = ExcludeMode.Never)
	public class MaintainStorageGoal {
		@GoalMaintainCondition(beliefs = "position")
		protected boolean maintain() {
			System.out.println(found);
			return found;
		}

		@GoalTargetCondition(beliefs = "position")
		protected boolean target() {
			return found;
		}
	}
	

	@Plan(trigger=@Trigger(goals=MaintainStorageGoal.class))
	protected String translateA()
	{
	  System.out.println("Plan A");
	  throw new PlanFailureException();
	}

	@Plan(trigger=@Trigger(goals=MaintainStorageGoal.class))
	protected String translateB()
	{
	  System.out.println("Plan B");
	  return "huhu";
	}
	
	@AgentBody
	public void body() {
		bdiFeature.dispatchTopLevelGoal(new MaintainStorageGoal());
	}
}
