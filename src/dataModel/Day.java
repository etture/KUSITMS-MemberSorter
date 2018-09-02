package dataModel;

public class Day {
	public final static int SUN = 0;
	public final static int MON = 1;
	public final static int TUE = 2;
	public final static int WED = 3;
	public final static int THU = 4;
	public final static int FRI = 5;
	//토요일은 세션이니까 제외
	
	int day;
	
	public Day(int day) {
		this.day = day;
	}

}
