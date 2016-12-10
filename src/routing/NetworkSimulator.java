package routing;

import java.util.LinkedList;

public class NetworkSimulator {

	int round = 0;
	LinkedList<Packet> packetsToDeliver = new LinkedList<Packet>();
	Router[] router;
	String[] routerLog = new String[4];

	public NetworkSimulator() {
		for (int i = 0; i < 4; i++) {
			routerLog[i] = "";
		}
	}

	public void add(Router[] routers) {
		router = routers;
	}

	public void beginRunning() {
		while (!packetsToDeliver.isEmpty()) {
			round++;
			Packet p = packetsToDeliver.pop();
			int routerId = p.getDestination();
			String roundInfo = "R" + round + ": " + p;
			System.out.println(roundInfo);
			routerLog[routerId] += "--------------------------\n";
			routerLog[routerId] += router[routerId].auditDistanceVectorTable();
			routerLog[routerId] += roundInfo + "\n";
			router[routerId].update(p);
			routerLog[routerId] += router[routerId].auditDistanceVectorTable();
		}
	}

	public synchronized void send(Packet p) {
		verifyPacket(p);
		packetsToDeliver.add(p);
	}

	private void verifyPacket(Packet p) {
		int source = p.getSource();
		int destination = p.getDestination();
		if ((source == destination)
				|| ((source == 1) && (destination == 3))
				|| ((source == 3) && (destination == 1))) {
			System.out.println("Illegal packet from " + source
					+ " to " + destination);
			System.exit(1);
		}
	}

	public void printRouterLogs() {
		for (int i = 0; i < 4; i++) {
			System.out.println("--------------------------");
			System.out.println("Router " + i + " Log");
			System.out.println(routerLog[i]);
		}
	}

}
