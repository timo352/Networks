package routing;

public class Packet {

	private int identifier = -1;
	private int source = -1;
	private int destination = -1;
	private int[] mincost = new int[4];

	;
          
  public Packet(int id, int s, int d, int[] mc) {
		identifier = id;
		source = s;
		destination = d;
    // to create a true copy of array
		// must copy it element by element
		for (int i = 0; i < 4; i++) {
			mincost[i] = mc[i];
		}
	}

	public int getSource() {
		return source;
	}

	public int getDestination() {
		return destination;
	}

	public final int[] getMinCosts() {
		return mincost;
	}

	public String toString() {
		String s = "Packet " + destination + "-" + source + "-" + identifier + ": ";
		for (int i = 0; i < 4; i++) {
			s += mincost[i] + " ";
		}
		return s;
	}

}
