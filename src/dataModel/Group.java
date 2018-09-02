package dataModel;

public class Group {
	public Member[] members = new Member[8]; //�� ���� 8��
	public Location location;
	public int day;
	public int numOfMembers = 0;
	public int groupNum = 0;
	public int offNum = 0; //��� ��
	public int memNum = 0; //ȸ�� ��
	public int boyNum = 0; //���� ��
	public int girlNum = 0; //���� ��
	public int devNum = 0; //������ ��
	
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
			dayName = "��";
			break;
		case 1:
			dayName = "��";
			break;
		case 2:
			dayName = "ȭ";
			break;
		case 3:
			dayName = "��";
			break;
		case 4:
			dayName = "��";
			break;
		case 5:
			dayName = "��";
			break;
		}
	}
	
}
