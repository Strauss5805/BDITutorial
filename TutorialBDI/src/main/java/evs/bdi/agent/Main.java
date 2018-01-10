package evs.bdi.agent;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;

/**
 * Start the agent
 *
 */
public class Main {
    public static void main(String[] args) {
        PlatformConfiguration   config  = PlatformConfiguration.getDefaultNoGui();

        config.addComponent("myOwn.PathFinder4BDI.class");
        Starter.createPlatform(config).get();
    }
}