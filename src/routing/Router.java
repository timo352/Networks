package routing;

public class Router extends Thread {
	// do not modify!

	private int myId = -1;
	private int[][] myDistanceVectorTable = new int[4][4]; //hardcoding 4 routers
	private boolean[] myNeighbors = new boolean[4]; // again hardcoding 4 routers 
	private NetworkSimulator network;
	private int packetNumber = 0;
    // feel free to add other private variables if you need them...

    // do not modify!
	// these values are assigned by Project.java when each of the 4 routers are constructed
	public Router(NetworkSimulator net, int id, int[] distanceToNeighbors) {
		network = net;
		myId = id;
		initArrays(distanceToNeighbors);
	}

    // do not modify!
	// note: the run method is called automatically when the threads are started
	// in Project.java -- you will never invoke this method
	public void run() {
		sendUpdate();
	}

    // this is called only once (by the constructor)
	// this method is responsible for initializing myDistanceVectorTable[]
	// and myNeighbors[] with the appropriate values
	// note: a distance of 999 is the convention being used to represent "infinity"
	// note: do not call sendUpdate from this method -- the first updates will be sent
	//       by run() when the thread is started
	private void initArrays(int[] distanceToNeighbors) {
	// initialize myNeighbors to true if the distance to them is less that 999
		// if this was an actual router we would also want to check to make sure
		// the values they give us aren't negative or other checks like that
		for (int i = 0; i < 4; i++) {
			if (distanceToNeighbors[i] < 999 && distanceToNeighbors[i] > 0) {
				myNeighbors[i] = true;
			}
			// also manually copy over the new distance values to the vector table
			myDistanceVectorTable[myId][i] = distanceToNeighbors[i];
		}

		// fill in the rest of the table with 999
		for (int i = myId + 1; i < myId + 4; i++) {
			for(int j=0; j<4; j++){
				myDistanceVectorTable[i % 4][j] = 999;
			}
		}
	}

    // this is called by NewtorkSimulator when new data arrives
	// this is where the DV algorithm will be implemented to update the myDistanceVectorTable[]
	// as new information arrives
	public void update(Packet p) {
		// stores the id of the packet's originator
		int fromId = p.getSource();
		boolean hasChanged = false;

		// holds the new values for the distance vector table
		int[] newMinimumVector = p.getMinCosts();
		for (int i = 0; i < 4; i++) {
			myDistanceVectorTable[fromId][i] = newMinimumVector[i];
		}

	// run the algorithm
		// because our max value is 999 we don't have to worry about overflowing 
		// the java integer. If that was a concern we would have to add a check
		// to make sure that myDistanceVectorTable[myId][fromId] and [fromId][i]
		// were not Integer.MAX_VALUE (which is what is normally used to store
		// infinity in our graph problems
		for (int i = 0; i < 4; i++) {
			if (myDistanceVectorTable[myId][i] > myDistanceVectorTable[myId][fromId] + myDistanceVectorTable[fromId][i]) {
				myDistanceVectorTable[myId][i] = myDistanceVectorTable[myId][fromId] + myDistanceVectorTable[fromId][i];
				hasChanged = true;
			}
		}

		// send an update message if we changed our values at all
		if (hasChanged) {
			sendUpdate();
		}
	}

    // do not modify!
	// called by update() when updates are required to be sent to neighbors
	private void sendUpdate() {
		for (int i = 0; i < 4; i++) {
			if (myNeighbors[i]) {
				packetNumber++;
				Packet p = new Packet(packetNumber, myId, i, myDistanceVectorTable[myId]);
				network.send(p);
			}
		}
	}

    // do not modify!
	// NetworkSimulator uses this method to maintain logs
	public String auditDistanceVectorTable() {
		String s = "";
		for (int i = 0; i < 4; i++) {
			s += String.format("%d: %4d%4d%4d%4d%n", i,
					myDistanceVectorTable[i][0],
					myDistanceVectorTable[i][1],
					myDistanceVectorTable[i][2],
					myDistanceVectorTable[i][3]);
		}
		return s;
	}

}
