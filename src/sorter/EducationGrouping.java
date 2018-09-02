package sorter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import dataModel.Day;
import dataModel.Location;
import dataModel.Member;

public class EducationGrouping {

	static String[] names;
	static int[] genders = new int[72];
	static int[] statuses = new int[72];
	static Location[] locations = new Location[7];
	static int[][] days = new int[72][];
	static Member[] members = new Member[72];
	static boolean[] devs = new boolean[72];
	
	static String genderTxt = null;
	static String statusTxt = null;
	static String namesTxt = null;
	static String gangnamTxt = null;
	static String sinchonTxt = null;
	static String sadangTxt = null;
	static String jonggakTxt = null;
	static String hyehwaTxt = null;
	static String wangsimniTxt = null;
	static String jamsilTxt = null;
	static String daysTxt = null;
	static String devsTxt = null;
	
	
	public static void main(String[] args) {	
		
		getInfo();
		setInfo();
		Random random = new Random();
		
		for(int i = 0; i < names.length; i++) { //???? ????
			int rand = random.nextInt(names.length);
			String tempName = names[i];
			int tempGender = genders[i];
			int tempStatus = statuses[i];
			int[] tempDay = days[i];
			int[] tempLocs = new int[7];
			boolean tempDevs = devs[i];
			for(int j = 0; j < locations.length; j++) {
				tempLocs[j] = locations[j].prefs[i];
			}
			Member tempMember = members[i];
			
			names[i] = names[rand];
			genders[i] = genders[rand];
			statuses[i] = statuses[rand];
			days[i] = days[rand];
			for(int j = 0; j < locations.length; j++) {
				locations[j].prefs[i] = locations[j].prefs[rand];
			}
			members[i] = members[rand];
			devs[i] = devs[rand];
			
			names[rand] = tempName;
			genders[rand] = tempGender;
			statuses[rand] = tempStatus;
			days[rand] = tempDay;
			for(int j = 0; j < locations.length; j++) {
				locations[j].prefs[rand] = tempLocs[j];
			}
			members[rand] = tempMember;
			devs[rand] = tempDevs;
		}
		
		printInfo();
		
		Member[] inputMembers = new Member[72];
		Location[] inputLocations = new Location[7];
		
		for(int i = 0; i < members.length; i++) {
			inputMembers[i] = new Member(members[i]);
		}
		for(int i = 0; i < locations.length; i++) {
			inputLocations[i] = new Location(locations[i]);
		}
		
		MemberSorter ms = new MemberSorter(inputMembers, inputLocations);
		ms.showInitial();
		ms.sort();
		ms.showFinal();
		ms.optimize();
		
		ms.showFinal();
	}
	
	static void getInfo() {
		
		try {
			genderTxt = getTxt("gender.txt");
			statusTxt = getTxt("status.txt");
			namesTxt = getTxt("names.txt");
			gangnamTxt = getTxt("gangnam.txt");
			sinchonTxt = getTxt("sinchon.txt");
			sadangTxt = getTxt("sadang.txt");
			jonggakTxt = getTxt("jonggak.txt");
			hyehwaTxt = getTxt("hyehwa.txt");
			wangsimniTxt = getTxt("wangsimni.txt");
			jamsilTxt = getTxt("jamsil.txt");
			daysTxt = getTxt("days.txt");
			devsTxt = getTxt("devs.txt");
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	static String getTxt(String fileName) throws IOException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");
		
		try {
			while((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}
			
			return stringBuilder.toString();
		}finally {
			reader.close();
		}
	}
	
	static void setInfo() {
		names = namesTxt.split(System.getProperty("line.separator"));
		
		String[] gendersString = genderTxt.split(System.getProperty("line.separator")); 
		String[] statusesString = statusTxt.split(System.getProperty("line.separator")); 
		String[] devsString = devsTxt.split(System.getProperty("line.separator"));
		
		String genderTmp = null;
		String statusTmp = null;
		String devTmp = null;
		
		for(int i = 0; i < 72; i++) {
			genderTmp = gendersString[i];
			statusTmp = statusesString[i];
			devTmp = devsString[i];
			
			if(genderTmp.equals("남")) {
				genders[i] = Member.BOY;	
			}else {
				genders[i] = Member.GIRL;
			}
			
			if(statusTmp.equals("운영진")) {
				statuses[i] = Member.OFFICER;	
			}else {
				statuses[i] = Member.MEMBER;
			}
			
			if(devTmp.equals("true")) {
				devs[i] = Member.DEVELOPER;
			}else {
				devs[i] = Member.NONDEV;
			}
		}
		
		String[] gangnam = gangnamTxt.split(System.getProperty("line.separator"));
		String[] sinchon = sinchonTxt.split(System.getProperty("line.separator"));
		String[] sadang = sadangTxt.split(System.getProperty("line.separator"));
		String[] jonggak = jonggakTxt.split(System.getProperty("line.separator"));
		String[] hyehwa = hyehwaTxt.split(System.getProperty("line.separator"));
		String[] wangsimni = wangsimniTxt.split(System.getProperty("line.separator"));
		String[] jamsil = jamsilTxt.split(System.getProperty("line.separator"));
		String[][] locs = {gangnam, sinchon, sadang, jonggak, hyehwa, wangsimni, jamsil};
		
		int[][] personalLoc = new int[72][7];
		for(int i = 0; i < 7; i++) {
			locations[i] = new Location(i);
			for(int j = 0; j < 72; j++) {
				locations[i].prefs[j] = Integer.parseInt(locs[i][j]);
			}
		}
		
		for(int i = 0; i < 72; i++) {
			for(int j = 0; j < 7; j++) {
				personalLoc[i][j] = locations[j].prefs[i];
			}
		}
		
		String[] daysInGroup = daysTxt.split(System.getProperty("line.separator"));
		String[] daysTemp = null;
		int[] daysInt = null;
		for(int i = 0; i < days.length; i++) {
			daysTemp = daysInGroup[i].split(", ");
			daysInt = new int[daysTemp.length];
			for(int j = 0; j < daysTemp.length; j++) {
				switch(daysTemp[j]) {
				case "일요일":
					daysInt[j] = Day.SUN;
					break;
				case "월요일":
					daysInt[j] = Day.MON;
					break;
				case "화요일":
					daysInt[j] = Day.TUE;
					break;
				case "수요일":
					daysInt[j] = Day.WED;
					break;
				case "목요일":
					daysInt[j] = Day.THU;
					break;
				case "금요일":
					daysInt[j] = Day.FRI;
					break;
				}
				
			}
			days[i] = daysInt;
		}
				
		for(int i = 0; i < members.length; i++) {
			members[i] = new Member(genders[i], statuses[i], names[i], days[i].length, days[i], personalLoc[i], devs[i]);
		}
	}
	
	static void printInfo() {
		System.out.println("-이름--성별--회/운---강남-신촌-사당-종각-혜화-왕십리-잠실---됨--되는날");
		for(int i = 0; i < names.length; i++) {
			if(names[i].length() == 2) {
				System.out.printf("%-4s    ", names[i]);	
			}else {
				System.out.printf("%-4s  ", names[i]);
			}
			
			System.out.print(((genders[i] == Member.BOY)?"남":"여") + "       ");
			System.out.print(((statuses[i] == Member.OFFICER)?"운영진     ":"회원        "));
			System.out.printf("%d   %d   %d   %d   %d   %d     %d     ", locations[0].prefs[i], locations[1].prefs[i], locations[2].prefs[i], locations[3].prefs[i], locations[4].prefs[i], locations[5].prefs[i], locations[6].prefs[i]);
			
			System.out.print(members[i].possibleDaysCnt + "  ");
			
			int tempDay = 0;
			String dayToPrint = null;
			for(int j = 0; j < days[i].length; j++) {
				switch(days[i][j]) {
				case 0:
					dayToPrint = "일";
					break;
				case 1:
					dayToPrint = "월";
					break;
				case 2:
					dayToPrint = "화";
					break;
				case 3:
					dayToPrint = "수";
					break;
				case 4:
					dayToPrint = "목";
					break;
				case 5:
					dayToPrint = "금";
					break;
				}
				System.out.print(dayToPrint);
				if(j == days[i].length - 1)
					continue;
				else
					System.out.print(", ");
			}
			
			System.out.println();
		}
	}
}


