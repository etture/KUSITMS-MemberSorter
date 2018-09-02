package dataModel;

public class Group {
	public Member[] members = new Member[8]; //한 조에 8명씩
	public Location location;
	public int day;
	public int numOfMembers = 0;
	public int groupNum = 0;
	public int offNum = 0; //운영진 수
	public int memNum = 0; //회원 수
	public int boyNum = 0; //남자 수
	public int girlNum = 0; //여자 수
	public int devNum = 0; //개발자 수
	
	public String dayName;
	
	public Group(int day) {
		this.day = day;
		setDayName();
	}
	
	public Group(int day, Location location) {
		this.day = day;
		this.location = location;
		setDayName();
	}
	
	public Group(int day, Location location, int groupNum) {
		this.day = day;
		this.location = location;
		this.groupNum = groupNum;
		setDayName();
	}
	
	public boolean addMember(Member member) {
		if(numOfMembers >= 8)
			return false;
		members[numOfMembers] = member;
		numOfMembers++;
		if(member.status == Member.OFFICER) {
			offNum++;
		}else {
			memNum++;
		}
		if(member.gender == Member.BOY) {
			boyNum++;
		}else {
			girlNum++;
		}
		
		if(member.dev) {
			devNum++;
		}
		
		return true;
	}
	
	public boolean removeMember(Member member) {
		if(numOfMembers == 0)
			return false;
		for(int i = 0; i < numOfMembers; i++) {
			if(members[i].equals(member)) {
				for(int j = i; j < numOfMembers-1; j++) {
					members[j] = members[j+1];
				}
				members[numOfMembers-1] = null;
				numOfMembers--;
				
				if(member.status == Member.OFFICER) {
					offNum--;
				}else {
					memNum--;
				}
				
				if(member.gender == Member.BOY) {
					boyNum--;
				}else {
					girlNum--;
				}
				
				if(member.dev) {
					devNum--;
				}
				
				return true;
			}
		}
		return false;
	}
	
	public boolean isFull() {
		if(numOfMembers == 8)
			return true;
		else
			return false;
	}
	
	public void setDayName() {
		switch(day%6) {
		case 0:
			dayName = "일";
			break;
		case 1:
			dayName = "월";
			break;
		case 2:
			dayName = "화";
			break;
		case 3:
			dayName = "수";
			break;
		case 4:
			dayName = "목";
			break;
		case 5:
			dayName = "금";
			break;
		}
	}
	
}
