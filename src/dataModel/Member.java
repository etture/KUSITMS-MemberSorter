package dataModel;

public class Member{
	public static final int BOY = 1; //���� - ��
	public static final int GIRL = 2; //���� - ��
	public static final int OFFICER = 3; //���
	public static final int MEMBER = 4; //ȸ��
	public static final boolean DEVELOPER = true; //������ ����
	public static final boolean NONDEV = false;
	
	public int gender, status; //����, ���/ȸ��
	public int possibleDaysCnt = 0; //����� ������ ��6���� ������ ���� ��
	public String name; //�̸�
	public int[] possibleDays; //������ ���� �迭
	public Member[] otherMembers = new Member[72]; //�ٸ� ȸ����� ���ؼ� �� (����, ���/ȸ��, ������ ���� ��)
	public int[] locationPref = new int[7]; //��� ��ȣ��, ��ȣ ������� �� �迭
	public boolean dev = false; //������ ����
	
	public Member(int gender, int status, String name, int possibleDaysCnt, int[] possibleDays, int[] locationPref, boolean dev) {
		this.gender = gender;
		this.status = status;
		this.name = name;
		this.possibleDaysCnt = possibleDaysCnt;
		this.possibleDays = possibleDays;
		this.locationPref = locationPref;
		this.dev = dev;
	}	
	
	public Member(Member m) {
		this.gender = m.gender;
		this.status = m.status;
		this.name = m.name;
		this.possibleDaysCnt = m.possibleDaysCnt;
		this.possibleDays = new int[this.possibleDaysCnt];
		System.arraycopy(m.possibleDays, 0, this.possibleDays, 0, m.possibleDays.length);
		System.arraycopy(m.locationPref, 0, this.locationPref, 0, m.locationPref.length);
		this.dev = m.dev;
	}
	
}