package dataModel;

public class Location{
	public final static int GANGNAM = 0;
	public final static int SINCHON = 1;
	public final static int SADANG = 2;
	public final static int JONGGAK = 3;
	public final static int HYEHWA = 4;
	public final static int WANGSIMNI = 5;
	public final static int JAMSIL = 6;
	
	public int code;
	public String name;
	
	public int[] prefs = new int[72];
	
	
	public Location(int code) {
		this.code = code;
		switch(code) {
		case Location.GANGNAM:
			name = "강남";
			break;
		case Location.SINCHON:
			name = "신촌";
			break;
		case Location.SADANG:
			name = "사당";
			break;
		case Location.JONGGAK:
			name = "종각";
			break;
		case Location.HYEHWA:
			name = "혜화";
			break;
		case Location.WANGSIMNI:
			name = "왕십리";
			break;
		case Location.JAMSIL:
			name = "잠실";
			break;	
		}
	}
	
	public Location(Location l) {
		this.code = l.code;
		this.name = l.name;
	}
	
	
	
}