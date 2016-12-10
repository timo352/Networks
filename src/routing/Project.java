package routing;

public class Project {

	public final static void main(String[] argv) {
		System.out.println("Started running...");

		NetworkSimulator simulator = new NetworkSimulator();

		Router[] routers = new Router[4];
		routers[0] = new Router(simulator, 0, new int[]{0, 1, 3, 7});
		routers[1] = new Router(simulator, 1, new int[]{1, 0, 1, 999});
		routers[2] = new Router(simulator, 2, new int[]{3, 1, 0, 2});
		routers[3] = new Router(simulator, 3, new int[]{7, 999, 2, 0});

		simulator.add(routers);

		try {
			for (int i = 0; i < 4; i++) {
				routers[i].start();
				routers[i].join();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		simulator.beginRunning();

		System.out.println("Completed.");
		System.out.println();

		simulator.printRouterLogs();
	}

}
