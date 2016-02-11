public class Dist implements Comparable<Dist>{

	double dist;
	int index;
	
	public Dist(double dist, int index)
	{
		this.dist = dist;
		this.index = index;
	}
	public int compareTo(Dist otherObj )
	{
		return (this.dist < otherObj.dist) ? -1 :(this.dist > otherObj.dist) ? 1:0;
	}
}
