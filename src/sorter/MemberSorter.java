package sorter;

import java.util.List;
import java.util.Random;

import dataModel.Day;
import dataModel.Group;
import dataModel.Location;
import dataModel.Member;

import java.util.ArrayList;
import java.util.Iterator;

public class MemberSorter {
	Member[] members;
	Location[] locations;
	List<Group> groups = new ArrayList<Group>();
	List<Member> altMembers = new ArrayList<Member>();
	List<Member> onlyOneDay = new ArrayList<Member>();
	List<Member> sunday = new ArrayList<Member>();
	List<Member> monday = new ArrayList<Member>();
	List<Member> tuesday = new ArrayList<Member>();
	List<Member> wednesday = new ArrayList<Member>();
	List<Member> thursday = new ArrayList<Member>();
	List<Member> friday = new ArrayList<Member>();
	
	List<List<Member>> allDays = new ArrayList<List<Member>>();
	String[] dayIndex = {"일", "월", "화", "수", "목", "금"};
	
	List<Group> matched = new ArrayList<Group>();
	List<Member> remaining = new ArrayList<Member>();
	List<Member> remMembs = new ArrayList<Member>();
	List<Group> notFullGroups = new ArrayList<Group>();
	
	List<Member> addedMems = new ArrayList<Member>();
	
	int numOfGroupsWithThreeOfficers = 0;
	int numOfGroupsWithFourBoys = 0;
	
	int groupCnt = 0;
	boolean succeeded = false;
	
	
	Random random = new Random();
	
	public MemberSorter(Member[] members, Location[] locations) {
		this.members = members;
		this.locations = locations;
		for(int i = 0; i < members.length; i++) {
			altMembers.add(members[i]);
		}
	}
	
	public void showInitial() {
		initializeAllDays();
		oneDay();
		addAllPossibleDays();
		
		printDayGroups();
	}
	
	public void sort() {
		matchDayLocation();
		
	}
	
	public void showFinal() {
		printLocationMatchedGroups();
		printRemainingMembers();
		printSummary();
	}
	
	public void optimize() {
		addRemainingMembers();
	}
	
	public void initializeAllDays() {
		allDays.add(sunday);
		allDays.add(monday);
		allDays.add(tuesday);
		allDays.add(wednesday);
		allDays.add(thursday);
		allDays.add(friday);
	}
	
	public void printSummary() {
		succeeded = false;
		System.out.println("**********Summary**********");
		System.out.println("--------장소 선호도 반영--------");
		
		int totalMatched = 0;
		int[] locationCnt = {0, 0, 0, 0, 0, 0, 0};
		List<ArrayList<Integer>> locationGroupNum = new ArrayList<ArrayList<Integer>>();
		
		for(int i = 0; i < 7; i++) {
			locationGroupNum.add(new ArrayList<Integer>());
		}
		
		int one = 0;
		int two = 0; 
		int three = 0;
		int four = 0; 
		int five = 0;
		int six = 0;
		int seven = 0;
		
		int atLeast2Devs = 0;
		int atLeast1Dev = 0;
		
		for(Group grp : matched) {
			totalMatched += grp.numOfMembers;
			locationCnt[grp.location.code]++;
			locationGroupNum.get(grp.location.code).add(grp.groupNum);
			
			if(grp.devNum >= 2) {
				atLeast2Devs++;
			}
			if(grp.devNum >= 1) {
				atLeast1Dev++;
			}
			
			for(int i = 0; i < grp.numOfMembers; i++) {
				Member curM = grp.members[i];
				switch(curM.locationPref[grp.location.code]) {
				case 1:
					one++;
					break;
				case 2:
					two++;
					break;
				case 3:
					three++;
					break;
				case 4:
					four++;
					break;
				case 5:
					five++;
					break;
				case 6:
					six++;
					break;
				case 7:
					seven++;
					break;
				}
			}
		}
		
		int[] ranking = {one, two, three, four, five, six, seven};
		
		int total = 0;
		int onetwothree = 0;
		double onetwothreePercentage = 0.0;
		for(int i = 0; i < 7; i++) {
			total += ranking[i];
		}
		for(int i = 0; i < 7; i++) {
			System.out.printf("%d위: %d명 (%.2f%%) ", i+1, ranking[i], ((double)ranking[i]/72)*100);
			if(i < 3) {
				onetwothree += ranking[i];
				onetwothreePercentage += ((double)ranking[i]/72)*100;
			}
		}
		System.out.printf("\n1,2,3위 총합: %d, 백분율: %.2f", onetwothree, onetwothreePercentage);
		
		System.out.println();
		System.out.println("----------장소 배정----------");
		for(int i = 0; i < 7; i++) {
			System.out.printf("%s: %d ", locations[i].name, locationCnt[i]);
			System.out.print("(");
			
			for(int k = 0; k < locationGroupNum.get(i).size(); k++) {
				if(k == locationGroupNum.get(i).size()-1) {
					System.out.print("그룹" + locationGroupNum.get(i).get(k));
				}else {
					System.out.print("그룹" + locationGroupNum.get(i).get(k) + ", ");
				}
			}
			if(i == 6) {
				System.out.print(")");
			}else {
				System.out.print("), ");	
			}
		}
		System.out.println();
		System.out.printf("전체 들어간 인원: %d, 손으로 넣어야 하는 인원: %d\n", totalMatched, 72-totalMatched);
		System.out.printf("운영진 3명짜리 그룹: %d개, 남자 4명짜리 그룹: %d개\n", numOfGroupsWithThreeOfficers, numOfGroupsWithFourBoys);
		System.out.printf("개발 가능한 사람 적어도 1명 있는 조: %d\n", atLeast1Dev);
		
		int successCnt = 0;
		System.out.print("\n선택 기준 1: 1순위 장소로 들어간 사람 40% 이상---");
		if(((double)ranking[0]/72)*100 >= 40.0) {
			System.out.print("성공\n");
			successCnt++;
		}
		else System.out.print("실패\n");
		
		System.out.print("선택 기준 2: 1,2,3순위 장소에 들어간 사람 총 75% 이상---");
		if(onetwothreePercentage >= 75.0) {
			System.out.print("성공\n");
			successCnt++;
		}
		else System.out.print("실패\n");
		
		System.out.print("선택 기준 3: 6순위 장소에 들어간 사람은 최대 3명---");
		if(ranking[5] <= 3) {
			System.out.print("성공\n");
			successCnt++;
		}
		else System.out.print("실패\n");
		
		System.out.print("선택 기준 4: 7순위 장소에 들어간 사람은 없음---");
		if(ranking[6] == 0) {
			System.out.print("성공\n");
			successCnt++;
		}
		else System.out.print("실패\n");
		
		System.out.print("선택 기준 5: 손으로 넣어야 하는 사람 3명 이하---");
		if(72-totalMatched <= 3) {
			System.out.print("성공\n");
			successCnt++;
		}
		else System.out.print("실패\n");
		
		System.out.print("선택 기준 6: 개발 가능한 사람 그룹당 적어도 1명씩---");
		if(atLeast1Dev >= 9) {
			System.out.print("성공\n");
			successCnt++;
		}
		else System.out.print("실패\n");
		
		System.out.print("\n선택 기준 모두 만족시켜야 성공---");
		if(successCnt == 6) {
			System.out.print("성공\n");
			succeeded = true;
		}
		else System.out.print("실패\n");
		
		System.out.println("********Summary End********");
		System.out.println("");
	}
	
	public void printDayGroups() {
		System.out.println("printDayGroups() start");		
		for(int k = 0; k < allDays.size(); k++) {
			System.out.print(dayIndex[k] + ": ");
			List<Member> curDay = allDays.get(k);
			for(int i = 0; i < curDay.size(); i++) {
				if(i == curDay.size()-1) {
					System.out.print(curDay.get(i).name);
				}else {
					System.out.print(curDay.get(i).name + ", ");
				}
				
			}
			System.out.println();
		}
		System.out.println("printDayGroups() end");
		System.out.println("");
		
	}
	
	public void printRemainingMembers() {
		System.out.println("printRemainingMembers() start");		
		for(int k = 0; k < allDays.size(); k++) {
			System.out.print(dayIndex[k] + ": ");
			List<Member> curDay = allDays.get(k);
			for(int i = 0; i < curDay.size(); i++) {
				if(i == curDay.size()-1) {
					System.out.print(curDay.get(i).name);
				}else {
					System.out.print(curDay.get(i).name + ", ");
				}
				
			}
			System.out.println();
		}
		System.out.println();
		
		
		System.out.println("남은 인원수: " + remMembs.size());
		System.out.println("-이름--성별--회/운---강남-신촌-사당-종각-혜화-왕십리-잠실");
		for(Member r : remMembs) {
			if(r.name.length() < 3) {
				System.out.printf("%-4s    ", r.name);	
			}else {
				System.out.printf("%-4s  ", r.name);
			}
			System.out.print(((r.gender == Member.BOY)?"남":"여") + "       ");
			System.out.print(((r.status == Member.OFFICER)?"운영진     ":"회원        "));
			System.out.printf("%d   %d   %d   %d   %d   %d     %d     ", r.locationPref[0], r.locationPref[1], r.locationPref[2], r.locationPref[3], r.locationPref[4], r.locationPref[5], r.locationPref[6]);
			
			System.out.println();
		}
		
		
		System.out.println("printRemainingMembers() end");
		System.out.println("");
		
	}
	
	public void addRemainingMembers() {
		System.out.println("addRemainingMembers() start");

		//투입에 성공한 인원들 리스트
		List<Member> success = new ArrayList<Member>();
		
		//인원비는 그룹 리스트에 넣기
		for(Group g : matched) {
			if(!g.isFull()) {
				notFullGroups.add(g);
			}
		}
		
		//남은인원 리스트에서 돌리기
		List<Member> remList = new ArrayList<Member>();
		for(Member m : remMembs) {
			remList.add(m);
		}
		for(Member m : remList) {
			
			int successGroupNum = -1;
			String pushedB = null;
			String pushedY = null;
			int pushedBGroupNum = -1;
			int pushedYGroupNum = -1;
			
			System.out.print("시도할 인원: " + m.name + ", ");
			
			//가능요일, 장소선호
			int[] possibleDays = m.possibleDays;
			int[] locationPref = m.locationPref;		
			
			//가능요일 그룹중에 장소가 12순위중 있다면 그룹 리스트에 넣기
			List<Group> potential = new ArrayList<Group>();
			System.out.print("가능한 그룹: ");
			for(Group g : matched) {
				for(int d : possibleDays) {
					if(g.day == d && locationPref[g.location.code] <= 2) {
						if(m.status == Member.OFFICER && g.offNum == 2 && numOfGroupsWithThreeOfficers == 3) {
							continue;
						}
						if(m.status == Member.OFFICER && g.offNum == 3) {
							continue;
						}
						if(m.status == Member.MEMBER && g.memNum == 6) {
							continue;
						}
						
						if(m.gender == Member.BOY && g.boyNum == 3 && numOfGroupsWithFourBoys == 3) {
							continue;
						}
						if(m.gender == Member.BOY && g.boyNum == 4) {
							continue;
						}
						if(m.gender == Member.GIRL && g.girlNum == 5) {
							continue;
						}
						
						potential.add(g);
						System.out.print(g.groupNum + " ");
					}
				}
			}
			System.out.println();
			
			Group replacedBAddedHere = null;
			Group replacedYAddedHere = null;
			Group initialMAddedHere = null;
			
			boolean successfullyAdded = false;
			
			outerloop:
			for(Group g : potential) {
				if(!g.isFull()) { //만약 안에 8명이 꽉 안차있으면
					//추가, 인원 종료
					g.addMember(m);
					successfullyAdded = true;
					success.add(m);
					successGroupNum = g.groupNum;
					initialMAddedHere = g;
					
					//남은 인원 리스트에서 해당 남은 인원 빼기
					remMembs.remove(m);
					for(List<Member> day : allDays) {
						day.remove(m);
					}
					
					break;
					
				}else if(g.isFull()) { //만약 안에 8명 꽉 차있으면
					//원래 그룹에서 인원 한명 고르기
					for(Member b : g.members) {
						//운영진은 운영진으로, 회원은 회원으로 바꾸기
						if(b.status != m.status)
							continue;
						if(addedMems.contains(b))
							continue;
						
						//첫번째로 새로 들어갈 그룹
						for(Group a : matched) {
							if(a.equals(g)){
							    continue;
							  }

							boolean dayMatched = false;
							  for(int d : b.possibleDays) {
							    if(a.day == d)
							      dayMatched = true;
							  }
							  
							  if(!a.isFull()) {
								  if(dayMatched && b.locationPref[a.location.code] <= 2) {
									//운영진/성비 조건을 만족하면
										if(b.status == Member.OFFICER && a.offNum == 2 && numOfGroupsWithThreeOfficers == 3 && g.offNum != 3) {
											continue;
										}
										if(b.status == Member.OFFICER && a.offNum > 2) {
											continue;
										}
										if(b.status == Member.MEMBER && a.memNum > 5) {
											continue;
										}
										if(b.locationPref[a.location.code] == 7) {
											continue;
										}
										
										if(b.gender == Member.BOY && a.boyNum == 3 && numOfGroupsWithFourBoys == 3 && g.boyNum != 4) {
											continue;
										}
										if(b.gender == Member.BOY && a.boyNum > 3) {
											continue;
										}
										if(b.gender == Member.GIRL && a.girlNum > 4) {
											continue;
										}
										
										//해당 멤버를 그 그룹에 추가하고 원래 그룹에서 빼기
										a.addMember(b);
										g.removeMember(b);
										
										//원래 그룹에 남은인원 추가
										g.addMember(m);
										
										successfullyAdded = true;
										success.add(m);
										successGroupNum = g.groupNum;
										pushedB = b.name;
										pushedBGroupNum = a.groupNum;
										initialMAddedHere = g;
										
										//운영진 3명, 남자 4명인 그룹 수 업데이트
										updateBoyOfficerNums();
										
										replacedBAddedHere = a;
										
										//남은 인원 리스트에서 해당 남은 인원 빼기
										remMembs.remove(m);
										for(List<Member> day : allDays) {
											day.remove(m);
										}
										
										//인원종료
										break outerloop;
								  }
							  }else if(a.isFull()) {
								  for(Member y : a.members) {
									  if(y.status != b.status) {
										  continue;
									  }
									  if(addedMems.contains(y))
											continue;
									  
									//인원비는 그룹 리스트 iterate하면서
									  for(Group u : notFullGroups) {
											//인원 요일 가능 여부
											boolean dayMatch = false;
											for(int d : b.possibleDays) {
												if(u.day == d)
													dayMatch = true;
											}
											
											//만약 멤버의 가능요일과 장소 2순위까지 해당하면
											if(dayMatch && b.locationPref[u.location.code] <= 2) {
												
												//운영진/성비 조건을 만족하면
												if(y.status == Member.OFFICER && u.offNum == 2 && numOfGroupsWithThreeOfficers == 3 && a.offNum != 3) {
													continue;
												}
												if(y.status == Member.OFFICER && u.offNum > 2) {
													continue;
												}
												if(y.status == Member.MEMBER && u.memNum > 5) {
													continue;
												}
												if(y.locationPref[u.location.code] == 7) {
													continue;
												}
												
												if(y.gender == Member.BOY && u.boyNum == 3 && numOfGroupsWithFourBoys == 3 && a.boyNum != 4) {
													continue;
												}
												if(y.gender == Member.BOY && u.boyNum > 3) {
													continue;
												}
												if(y.gender == Member.GIRL && u.girlNum > 4) {
													continue;
												}
												
												//해당 멤버를 그 그룹에 추가하고 원래 그룹에서 빼기
												u.addMember(y);
												a.removeMember(y);
												
												//원래 그룹에 남은인원 추가
												a.addMember(b);
												g.removeMember(b);
												
												g.addMember(m);
												
												successfullyAdded = true;
												success.add(m);
												successGroupNum = g.groupNum;
												
												pushedB = b.name;
												pushedBGroupNum = a.groupNum;
												
												pushedY = y.name;
												pushedYGroupNum = u.groupNum;
												
												//운영진 3명, 남자 4명인 그룹 수 업데이트
												updateBoyOfficerNums();
												
												replacedBAddedHere = a;
												replacedYAddedHere = u;
												initialMAddedHere = g;
												
												//남은 인원 리스트에서 해당 남은 인원 빼기
												remMembs.remove(m);
												for(List<Member> day : allDays) {
													day.remove(m);
												}
												
												//인원종료
												break outerloop;
											}
										}
								  }
							  }
						}
					}
				}
			}
			
			//만약 그 추가된 다른 그룹이 8명이 꽉차게 됐으면 인원비는 그룹 리스트에서 빼기
			if(replacedYAddedHere != null && replacedYAddedHere.isFull()) {
				notFullGroups.remove(replacedYAddedHere);
			}
			if(replacedBAddedHere != null && replacedBAddedHere.isFull()) {
				notFullGroups.remove(replacedBAddedHere);
			}
			if(initialMAddedHere != null && initialMAddedHere.isFull()) {
				notFullGroups.remove(initialMAddedHere);
			}
			
			if(successfullyAdded) {
				System.out.printf("성공: %s (to: 그룹 %d), replaced %s (to: 그룹 %d), replaced2 %s (to: 그룹 %d)\n",
						m.name, successGroupNum, pushedB, pushedBGroupNum, pushedY, pushedYGroupNum);				
			}
			
		}
		
		
		//아직도 남은 인원은 선호도 3위까지 다시 한 번 돌리기
		remList.clear();
		for(Member m : remMembs) {
			remList.add(m);
		}
		
		
		if(!remList.isEmpty()) {
			System.out.println("\n3위까지 돌리기...");
			for(Member m : remList) {

				int successGroupNum = -1;
				String pushedB = null;
				String pushedY = null;
				int pushedBGroupNum = -1;
				int pushedYGroupNum = -1;
				
				System.out.print("시도할 인원: " + m.name + ", ");
				
				//가능요일, 장소선호
				int[] possibleDays = m.possibleDays;
				int[] locationPref = m.locationPref;		
				
				//가능요일 그룹중에 장소가 12순위중 있다면 그룹 리스트에 넣기
				List<Group> potential = new ArrayList<Group>();
				System.out.print("가능한 그룹: ");
				for(Group g : matched) {
					for(int d : possibleDays) {
						if(g.day == d && locationPref[g.location.code] <= 3) {
							if(m.status == Member.OFFICER && g.offNum == 2 && numOfGroupsWithThreeOfficers == 3) {
								continue;
							}
							if(m.status == Member.OFFICER && g.offNum == 3) {
								continue;
							}
							if(m.status == Member.MEMBER && g.memNum == 6) {
								continue;
							}
							
							if(m.gender == Member.BOY && g.boyNum == 3 && numOfGroupsWithFourBoys == 3) {
								continue;
							}
							if(m.gender == Member.BOY && g.boyNum == 4) {
								continue;
							}
							if(m.gender == Member.GIRL && g.girlNum == 5) {
								continue;
							}
							
							potential.add(g);
							System.out.print(g.groupNum + " ");
						}
					}
				}
				System.out.println();
				
				Group replacedBAddedHere = null;
				Group replacedYAddedHere = null;
				Group initialMAddedHere = null;
				
				boolean successfullyAdded = false;
				
				outerloop:
				for(Group g : potential) {
					if(!g.isFull()) { //만약 안에 8명이 꽉 안차있으면
						//추가, 인원 종료
						g.addMember(m);
						successfullyAdded = true;
						success.add(m);
						successGroupNum = g.groupNum;
						initialMAddedHere = g;
						
						//남은 인원 리스트에서 해당 남은 인원 빼기
						remMembs.remove(m);
						for(List<Member> day : allDays) {
							day.remove(m);
						}
						
						break;
						
					}else if(g.isFull()) { //만약 안에 8명 꽉 차있으면
						//원래 그룹에서 인원 한명 고르기
						for(Member b : g.members) {
							//운영진은 운영진으로, 회원은 회원으로 바꾸기
							if(b.status != m.status)
								continue;
							if(addedMems.contains(b))
								continue;
							
							//첫번째로 새로 들어갈 그룹
							for(Group a : matched) {
								if(a.equals(g)){
								    continue;
								  }

								boolean dayMatched = false;
								  for(int d : b.possibleDays) {
								    if(a.day == d)
								      dayMatched = true;
								  }
								  
								  if(!a.isFull()) {
									  if(dayMatched && b.locationPref[a.location.code] <= 3) {
										//운영진/성비 조건을 만족하면
											if(b.status == Member.OFFICER && a.offNum == 2 && numOfGroupsWithThreeOfficers == 3 && g.offNum != 3) {
												continue;
											}
											if(b.status == Member.OFFICER && a.offNum > 2) {
												continue;
											}
											if(b.status == Member.MEMBER && a.memNum > 5) {
												continue;
											}
											if(b.locationPref[a.location.code] == 7) {
												continue;
											}
											
											if(b.gender == Member.BOY && a.boyNum == 3 && numOfGroupsWithFourBoys == 3 && g.boyNum != 4) {
												continue;
											}
											if(b.gender == Member.BOY && a.boyNum > 3) {
												continue;
											}
											if(b.gender == Member.GIRL && a.girlNum > 4) {
												continue;
											}
											
											//해당 멤버를 그 그룹에 추가하고 원래 그룹에서 빼기
											a.addMember(b);
											g.removeMember(b);
											
											//원래 그룹에 남은인원 추가
											g.addMember(m);
											
											successfullyAdded = true;
											success.add(m);
											successGroupNum = g.groupNum;
											pushedB = b.name;
											pushedBGroupNum = a.groupNum;
											initialMAddedHere = g;
											
											//운영진 3명, 남자 4명인 그룹 수 업데이트
											updateBoyOfficerNums();
											
											replacedBAddedHere = a;
											
											//남은 인원 리스트에서 해당 남은 인원 빼기
											remMembs.remove(m);
											for(List<Member> day : allDays) {
												day.remove(m);
											}
											
											//인원종료
											break outerloop;
									  }
								  }else if(a.isFull()) {
									  for(Member y : a.members) {
										  if(y.status != b.status) {
											  continue;
										  }
										  if(addedMems.contains(y))
												continue;
										  
										//인원비는 그룹 리스트 iterate하면서
										  for(Group u : notFullGroups) {
												//인원 요일 가능 여부
												boolean dayMatch = false;
												for(int d : b.possibleDays) {
													if(u.day == d)
														dayMatch = true;
												}
												
												//만약 멤버의 가능요일과 장소 2순위까지 해당하면
												if(dayMatch && b.locationPref[u.location.code] <= 3) {
													
													//운영진/성비 조건을 만족하면
													if(y.status == Member.OFFICER && u.offNum == 2 && numOfGroupsWithThreeOfficers == 3 && a.offNum != 3) {
														continue;
													}
													if(y.status == Member.OFFICER && u.offNum > 2) {
														continue;
													}
													if(y.status == Member.MEMBER && u.memNum > 5) {
														continue;
													}
													if(y.locationPref[u.location.code] == 7) {
														continue;
													}
													
													if(y.gender == Member.BOY && u.boyNum == 3 && numOfGroupsWithFourBoys == 3 && a.boyNum != 4) {
														continue;
													}
													if(y.gender == Member.BOY && u.boyNum > 3) {
														continue;
													}
													if(y.gender == Member.GIRL && u.girlNum > 4) {
														continue;
													}
													
													//해당 멤버를 그 그룹에 추가하고 원래 그룹에서 빼기
													u.addMember(y);
													a.removeMember(y);
													
													//원래 그룹에 남은인원 추가
													a.addMember(b);
													g.removeMember(b);
													
													g.addMember(m);
													
													successfullyAdded = true;
													success.add(m);
													successGroupNum = g.groupNum;
													
													pushedB = b.name;
													pushedBGroupNum = a.groupNum;
													
													pushedY = y.name;
													pushedYGroupNum = u.groupNum;
													
													//운영진 3명, 남자 4명인 그룹 수 업데이트
													updateBoyOfficerNums();
													
													replacedBAddedHere = a;
													replacedYAddedHere = u;
													initialMAddedHere = g;
													
													//남은 인원 리스트에서 해당 남은 인원 빼기
													remMembs.remove(m);
													for(List<Member> day : allDays) {
														day.remove(m);
													}
													
													//인원종료
													break outerloop;
												}
											}
									  }
								  }
							}
						}
					}
				}
				
				//만약 그 추가된 다른 그룹이 8명이 꽉차게 됐으면 인원비는 그룹 리스트에서 빼기
				if(replacedYAddedHere != null && replacedYAddedHere.isFull()) {
					notFullGroups.remove(replacedYAddedHere);
				}
				if(replacedBAddedHere != null && replacedBAddedHere.isFull()) {
					notFullGroups.remove(replacedBAddedHere);
				}
				if(initialMAddedHere != null && initialMAddedHere.isFull()) {
					notFullGroups.remove(initialMAddedHere);
				}
				
				if(successfullyAdded) {
					System.out.printf("성공: %s (to: 그룹 %d), replaced %s (to: 그룹 %d), replaced2 %s (to: 그룹 %d)\n",
							m.name, successGroupNum, pushedB, pushedBGroupNum, pushedY, pushedYGroupNum);				
				}
				
			}
		}
			
		//4위까지
		remList.clear();
		for(Member m : remMembs) {
			remList.add(m);
		}
		
		
		if(!remList.isEmpty()) {
			System.out.println("\n4위까지 돌리기...");
			for(Member m : remList) {

				int successGroupNum = -1;
				String pushedB = null;
				String pushedY = null;
				int pushedBGroupNum = -1;
				int pushedYGroupNum = -1;
				
				System.out.print("시도할 인원: " + m.name + ", ");
				
				//���ɿ���, ��Ҽ�ȣ
				int[] possibleDays = m.possibleDays;
				int[] locationPref = m.locationPref;		
				
				//���ɿ��� �׷��߿� ��Ұ� 12������ �ִٸ� �׷� ����Ʈ�� �ֱ�
				List<Group> potential = new ArrayList<Group>();
				System.out.print("가능한 그룹: ");
				for(Group g : matched) {
					for(int d : possibleDays) {
						if(g.day == d && locationPref[g.location.code] <= 4) {
							if(m.status == Member.OFFICER && g.offNum == 2 && numOfGroupsWithThreeOfficers == 3) {
								continue;
							}
							if(m.status == Member.OFFICER && g.offNum == 3) {
								continue;
							}
							if(m.status == Member.MEMBER && g.memNum == 6) {
								continue;
							}
							
							if(m.gender == Member.BOY && g.boyNum == 3 && numOfGroupsWithFourBoys == 3) {
								continue;
							}
							if(m.gender == Member.BOY && g.boyNum == 4) {
								continue;
							}
							if(m.gender == Member.GIRL && g.girlNum == 5) {
								continue;
							}
							
							potential.add(g);
							System.out.print(g.groupNum + " ");
						}
					}
				}
				System.out.println();
				
				Group replacedBAddedHere = null;
				Group replacedYAddedHere = null;
				Group initialMAddedHere = null;
				
				boolean successfullyAdded = false;
				
				outerloop:
				for(Group g : potential) {
					if(!g.isFull()) { //���� �ȿ� 8���� �� ����������
						//�߰�, �ο� ����
						g.addMember(m);
						successfullyAdded = true;
						success.add(m);
						successGroupNum = g.groupNum;
						initialMAddedHere = g;
						
						//���� �ο� ����Ʈ���� �ش� ���� �ο� ����
						remMembs.remove(m);
						for(List<Member> day : allDays) {
							day.remove(m);
						}
						
						break;
						
					}else if(g.isFull()) { //���� �ȿ� 8�� �� ��������
						//���� �׷쿡�� �ο� �Ѹ� ����
						for(Member b : g.members) {
							//����� �������, ȸ���� ȸ������ �ٲٱ�
							if(b.status != m.status)
								continue;
							if(addedMems.contains(b))
								continue;
							
							//ù��°�� ���� �� �׷�
							for(Group a : matched) {
								if(a.equals(g)){
								    continue;
								  }

								boolean dayMatched = false;
								  for(int d : b.possibleDays) {
								    if(a.day == d)
								      dayMatched = true;
								  }
								  
								  if(!a.isFull()) {
									  if(dayMatched && b.locationPref[a.location.code] <= 4) {
										//���/���� ������ �����ϸ�
											if(b.status == Member.OFFICER && a.offNum == 2 && numOfGroupsWithThreeOfficers == 3 && g.offNum != 3) {
												continue;
											}
											if(b.status == Member.OFFICER && a.offNum > 2) {
												continue;
											}
											if(b.status == Member.MEMBER && a.memNum > 5) {
												continue;
											}
											if(b.locationPref[a.location.code] == 7) {
												continue;
											}
											
											if(b.gender == Member.BOY && a.boyNum == 3 && numOfGroupsWithFourBoys == 3 && g.boyNum != 4) {
												continue;
											}
											if(b.gender == Member.BOY && a.boyNum > 3) {
												continue;
											}
											if(b.gender == Member.GIRL && a.girlNum > 4) {
												continue;
											}
											
											//�ش� ����� �� �׷쿡 �߰��ϰ� ���� �׷쿡�� ����
											a.addMember(b);
											g.removeMember(b);
											
											//���� �׷쿡 �����ο� �߰�
											g.addMember(m);
											
											successfullyAdded = true;
											success.add(m);
											successGroupNum = g.groupNum;
											pushedB = b.name;
											pushedBGroupNum = a.groupNum;
											initialMAddedHere = g;
											
											//��� 3��, ���� 4���� �׷� �� ������Ʈ
											updateBoyOfficerNums();
											
											replacedBAddedHere = a;
											
											//���� �ο� ����Ʈ���� �ش� ���� �ο� ����
											remMembs.remove(m);
											for(List<Member> day : allDays) {
												day.remove(m);
											}
											
											//�ο�����
											break outerloop;
									  }
								  }else if(a.isFull()) {
									  for(Member y : a.members) {
										  if(y.status != b.status) {
											  continue;
										  }
										  if(addedMems.contains(y))
												continue;
										  
										//�ο���� �׷� ����Ʈ iterate�ϸ鼭
										  for(Group u : notFullGroups) {
												//�ο� ���� ���� ����
												boolean dayMatch = false;
												for(int d : b.possibleDays) {
													if(u.day == d)
														dayMatch = true;
												}
												
												//���� ����� ���ɿ��ϰ� ��� 2�������� �ش��ϸ�
												if(dayMatch && b.locationPref[u.location.code] <= 4) {
													
													//���/���� ������ �����ϸ�
													if(y.status == Member.OFFICER && u.offNum == 2 && numOfGroupsWithThreeOfficers == 3 && a.offNum != 3) {
														continue;
													}
													if(y.status == Member.OFFICER && u.offNum > 2) {
														continue;
													}
													if(y.status == Member.MEMBER && u.memNum > 5) {
														continue;
													}
													if(y.locationPref[u.location.code] == 7) {
														continue;
													}
													
													if(y.gender == Member.BOY && u.boyNum == 3 && numOfGroupsWithFourBoys == 3 && a.boyNum != 4) {
														continue;
													}
													if(y.gender == Member.BOY && u.boyNum > 3) {
														continue;
													}
													if(y.gender == Member.GIRL && u.girlNum > 4) {
														continue;
													}
													
													//�ش� ����� �� �׷쿡 �߰��ϰ� ���� �׷쿡�� ����
													u.addMember(y);
													a.removeMember(y);
													
													//���� �׷쿡 �����ο� �߰�
													a.addMember(b);
													g.removeMember(b);
													
													g.addMember(m);
													
													successfullyAdded = true;
													success.add(m);
													successGroupNum = g.groupNum;
													
													pushedB = b.name;
													pushedBGroupNum = a.groupNum;
													
													pushedY = y.name;
													pushedYGroupNum = u.groupNum;
													
													//��� 3��, ���� 4���� �׷� �� ������Ʈ
													updateBoyOfficerNums();
													
													replacedBAddedHere = a;
													replacedYAddedHere = u;
													initialMAddedHere = g;
													
													//���� �ο� ����Ʈ���� �ش� ���� �ο� ����
													remMembs.remove(m);
													for(List<Member> day : allDays) {
														day.remove(m);
													}
													
													//�ο�����
													break outerloop;
												}
											}
									  }
								  }
							}
						}
					}
				}
				
				//���� �� �߰��� �ٸ� �׷��� 8���� ������ ������ �ο���� �׷� ����Ʈ���� ����
				if(replacedYAddedHere != null && replacedYAddedHere.isFull()) {
					notFullGroups.remove(replacedYAddedHere);
				}
				if(replacedBAddedHere != null && replacedBAddedHere.isFull()) {
					notFullGroups.remove(replacedBAddedHere);
				}
				if(initialMAddedHere != null && initialMAddedHere.isFull()) {
					notFullGroups.remove(initialMAddedHere);
				}
				
				if(successfullyAdded) {
					System.out.printf("성공: %s (to: 그룹 %d), replaced %s (to: 그룹 %d), replaced2 %s (to: 그룹 %d)\n",
							m.name, successGroupNum, pushedB, pushedBGroupNum, pushedY, pushedYGroupNum);				
				}
				
			}
		}
		
		//5위까지
		remList.clear();
		for(Member m : remMembs) {
			remList.add(m);
		}
		
		
		if(!remList.isEmpty()) {
			System.out.println("\n5위까지 돌리기...");
			for(Member m : remList) {

				int successGroupNum = -1;
				String pushedB = null;
				String pushedY = null;
				int pushedBGroupNum = -1;
				int pushedYGroupNum = -1;
				
				System.out.print("시도할 인원: " + m.name + ", ");
				
				//���ɿ���, ��Ҽ�ȣ
				int[] possibleDays = m.possibleDays;
				int[] locationPref = m.locationPref;		
				
				//���ɿ��� �׷��߿� ��Ұ� 12������ �ִٸ� �׷� ����Ʈ�� �ֱ�
				List<Group> potential = new ArrayList<Group>();
				System.out.print("가능한 그룹: ");
				for(Group g : matched) {
					for(int d : possibleDays) {
						if(g.day == d && locationPref[g.location.code] <= 5) {
							if(m.status == Member.OFFICER && g.offNum == 2 && numOfGroupsWithThreeOfficers == 3) {
								continue;
							}
							if(m.status == Member.OFFICER && g.offNum == 3) {
								continue;
							}
							if(m.status == Member.MEMBER && g.memNum == 6) {
								continue;
							}
							
							if(m.gender == Member.BOY && g.boyNum == 3 && numOfGroupsWithFourBoys == 3) {
								continue;
							}
							if(m.gender == Member.BOY && g.boyNum == 4) {
								continue;
							}
							if(m.gender == Member.GIRL && g.girlNum == 5) {
								continue;
							}
							
							potential.add(g);
							System.out.print(g.groupNum + " ");
						}
					}
				}
				System.out.println();
				
				Group replacedBAddedHere = null;
				Group replacedYAddedHere = null;
				Group initialMAddedHere = null;
				
				boolean successfullyAdded = false;
				
				outerloop:
				for(Group g : potential) {
					if(!g.isFull()) { //���� �ȿ� 8���� �� ����������
						//�߰�, �ο� ����
						g.addMember(m);
						successfullyAdded = true;
						success.add(m);
						successGroupNum = g.groupNum;
						initialMAddedHere = g;
						
						//���� �ο� ����Ʈ���� �ش� ���� �ο� ����
						remMembs.remove(m);
						for(List<Member> day : allDays) {
							day.remove(m);
						}
						
						break;
						
					}else if(g.isFull()) { //���� �ȿ� 8�� �� ��������
						//���� �׷쿡�� �ο� �Ѹ� ����
						for(Member b : g.members) {
							//����� �������, ȸ���� ȸ������ �ٲٱ�
							if(b.status != m.status)
								continue;
							if(addedMems.contains(b))
								continue;
							
							//ù��°�� ���� �� �׷�
							for(Group a : matched) {
								if(a.equals(g)){
								    continue;
								  }

								boolean dayMatched = false;
								  for(int d : b.possibleDays) {
								    if(a.day == d)
								      dayMatched = true;
								  }
								  
								  if(!a.isFull()) {
									  if(dayMatched && b.locationPref[a.location.code] <= 5) {
										//���/���� ������ �����ϸ�
											if(b.status == Member.OFFICER && a.offNum == 2 && numOfGroupsWithThreeOfficers == 3 && g.offNum != 3) {
												continue;
											}
											if(b.status == Member.OFFICER && a.offNum > 2) {
												continue;
											}
											if(b.status == Member.MEMBER && a.memNum > 5) {
												continue;
											}
											if(b.locationPref[a.location.code] == 7) {
												continue;
											}
											
											if(b.gender == Member.BOY && a.boyNum == 3 && numOfGroupsWithFourBoys == 3 && g.boyNum != 4) {
												continue;
											}
											if(b.gender == Member.BOY && a.boyNum > 3) {
												continue;
											}
											if(b.gender == Member.GIRL && a.girlNum > 4) {
												continue;
											}
											
											//�ش� ����� �� �׷쿡 �߰��ϰ� ���� �׷쿡�� ����
											a.addMember(b);
											g.removeMember(b);
											
											//���� �׷쿡 �����ο� �߰�
											g.addMember(m);
											
											successfullyAdded = true;
											success.add(m);
											successGroupNum = g.groupNum;
											pushedB = b.name;
											pushedBGroupNum = a.groupNum;
											initialMAddedHere = g;
											
											//��� 3��, ���� 4���� �׷� �� ������Ʈ
											updateBoyOfficerNums();
											
											replacedBAddedHere = a;
											
											//���� �ο� ����Ʈ���� �ش� ���� �ο� ����
											remMembs.remove(m);
											for(List<Member> day : allDays) {
												day.remove(m);
											}
											
											//�ο�����
											break outerloop;
									  }
								  }else if(a.isFull()) {
									  for(Member y : a.members) {
										  if(y.status != b.status) {
											  continue;
										  }
										  if(addedMems.contains(y))
												continue;
										  
										//�ο���� �׷� ����Ʈ iterate�ϸ鼭
										  for(Group u : notFullGroups) {
												//�ο� ���� ���� ����
												boolean dayMatch = false;
												for(int d : b.possibleDays) {
													if(u.day == d)
														dayMatch = true;
												}
												
												//���� ����� ���ɿ��ϰ� ��� 2�������� �ش��ϸ�
												if(dayMatch && b.locationPref[u.location.code] <= 5) {
													
													//���/���� ������ �����ϸ�
													if(y.status == Member.OFFICER && u.offNum == 2 && numOfGroupsWithThreeOfficers == 3 && a.offNum != 3) {
														continue;
													}
													if(y.status == Member.OFFICER && u.offNum > 2) {
														continue;
													}
													if(y.status == Member.MEMBER && u.memNum > 5) {
														continue;
													}
													if(y.locationPref[u.location.code] == 7) {
														continue;
													}
													
													if(y.gender == Member.BOY && u.boyNum == 3 && numOfGroupsWithFourBoys == 3 && a.boyNum != 4) {
														continue;
													}
													if(y.gender == Member.BOY && u.boyNum > 3) {
														continue;
													}
													if(y.gender == Member.GIRL && u.girlNum > 4) {
														continue;
													}
													
													//�ش� ����� �� �׷쿡 �߰��ϰ� ���� �׷쿡�� ����
													u.addMember(y);
													a.removeMember(y);
													
													//���� �׷쿡 �����ο� �߰�
													a.addMember(b);
													g.removeMember(b);
													
													g.addMember(m);
													
													successfullyAdded = true;
													success.add(m);
													successGroupNum = g.groupNum;
													
													pushedB = b.name;
													pushedBGroupNum = a.groupNum;
													
													pushedY = y.name;
													pushedYGroupNum = u.groupNum;
													
													//��� 3��, ���� 4���� �׷� �� ������Ʈ
													updateBoyOfficerNums();
													
													replacedBAddedHere = a;
													replacedYAddedHere = u;
													initialMAddedHere = g;
													
													//���� �ο� ����Ʈ���� �ش� ���� �ο� ����
													remMembs.remove(m);
													for(List<Member> day : allDays) {
														day.remove(m);
													}
													
													//�ο�����
													break outerloop;
												}
											}
									  }
								  }
							}
						}
					}
				}
				
				//���� �� �߰��� �ٸ� �׷��� 8���� ������ ������ �ο���� �׷� ����Ʈ���� ����
				if(replacedYAddedHere != null && replacedYAddedHere.isFull()) {
					notFullGroups.remove(replacedYAddedHere);
				}
				if(replacedBAddedHere != null && replacedBAddedHere.isFull()) {
					notFullGroups.remove(replacedBAddedHere);
				}
				if(initialMAddedHere != null && initialMAddedHere.isFull()) {
					notFullGroups.remove(initialMAddedHere);
				}
				
				if(successfullyAdded) {
					System.out.printf("성공: %s (to: 그룹 %d), replaced %s (to: 그룹 %d), replaced2 %s (to: 그룹 %d)\n",
							m.name, successGroupNum, pushedB, pushedBGroupNum, pushedY, pushedYGroupNum);				
				}
				
			}
		}
		
		//6위까지
		remList.clear();
		for(Member m : remMembs) {
			remList.add(m);
		}
		
		
		if(!remList.isEmpty()) {
			System.out.println("\n5위까지 돌리기...");
			for(Member m : remList) {

				int successGroupNum = -1;
				String pushedB = null;
				String pushedY = null;
				int pushedBGroupNum = -1;
				int pushedYGroupNum = -1;
				
				System.out.print("시도할 인원: " + m.name + ", ");
				
				//���ɿ���, ��Ҽ�ȣ
				int[] possibleDays = m.possibleDays;
				int[] locationPref = m.locationPref;		
				
				//���ɿ��� �׷��߿� ��Ұ� 12������ �ִٸ� �׷� ����Ʈ�� �ֱ�
				List<Group> potential = new ArrayList<Group>();
				System.out.print("가능한 그룹: ");
				for(Group g : matched) {
					for(int d : possibleDays) {
						if(g.day == d && locationPref[g.location.code] <= 6) {
							if(m.status == Member.OFFICER && g.offNum == 2 && numOfGroupsWithThreeOfficers == 3) {
								continue;
							}
							if(m.status == Member.OFFICER && g.offNum == 3) {
								continue;
							}
							if(m.status == Member.MEMBER && g.memNum == 6) {
								continue;
							}
							
							if(m.gender == Member.BOY && g.boyNum == 3 && numOfGroupsWithFourBoys == 3) {
								continue;
							}
							if(m.gender == Member.BOY && g.boyNum == 4) {
								continue;
							}
							if(m.gender == Member.GIRL && g.girlNum == 5) {
								continue;
							}
							
							potential.add(g);
							System.out.print(g.groupNum + " ");
						}
					}
				}
				System.out.println();
				
				Group replacedBAddedHere = null;
				Group replacedYAddedHere = null;
				Group initialMAddedHere = null;
				
				boolean successfullyAdded = false;
				
				outerloop:
				for(Group g : potential) {
					if(!g.isFull()) { //���� �ȿ� 8���� �� ����������
						//�߰�, �ο� ����
						g.addMember(m);
						successfullyAdded = true;
						success.add(m);
						successGroupNum = g.groupNum;
						initialMAddedHere = g;
						
						//���� �ο� ����Ʈ���� �ش� ���� �ο� ����
						remMembs.remove(m);
						for(List<Member> day : allDays) {
							day.remove(m);
						}
						
						break;
						
					}else if(g.isFull()) { //���� �ȿ� 8�� �� ��������
						//���� �׷쿡�� �ο� �Ѹ� ����
						for(Member b : g.members) {
							//����� �������, ȸ���� ȸ������ �ٲٱ�
							if(b.status != m.status)
								continue;
							if(addedMems.contains(b))
								continue;
							
							//ù��°�� ���� �� �׷�
							for(Group a : matched) {
								if(a.equals(g)){
								    continue;
								  }

								boolean dayMatched = false;
								  for(int d : b.possibleDays) {
								    if(a.day == d)
								      dayMatched = true;
								  }
								  
								  if(!a.isFull()) {
									  if(dayMatched && b.locationPref[a.location.code] <= 6) {
										//���/���� ������ �����ϸ�
											if(b.status == Member.OFFICER && a.offNum == 2 && numOfGroupsWithThreeOfficers == 3 && g.offNum != 3) {
												continue;
											}
											if(b.status == Member.OFFICER && a.offNum > 2) {
												continue;
											}
											if(b.status == Member.MEMBER && a.memNum > 5) {
												continue;
											}
											if(b.locationPref[a.location.code] == 7) {
												continue;
											}
											
											if(b.gender == Member.BOY && a.boyNum == 3 && numOfGroupsWithFourBoys == 3 && g.boyNum != 4) {
												continue;
											}
											if(b.gender == Member.BOY && a.boyNum > 3) {
												continue;
											}
											if(b.gender == Member.GIRL && a.girlNum > 4) {
												continue;
											}
											
											//�ش� ����� �� �׷쿡 �߰��ϰ� ���� �׷쿡�� ����
											a.addMember(b);
											g.removeMember(b);
											
											//���� �׷쿡 �����ο� �߰�
											g.addMember(m);
											
											successfullyAdded = true;
											success.add(m);
											successGroupNum = g.groupNum;
											pushedB = b.name;
											pushedBGroupNum = a.groupNum;
											initialMAddedHere = g;
											
											//��� 3��, ���� 4���� �׷� �� ������Ʈ
											updateBoyOfficerNums();
											
											replacedBAddedHere = a;
											
											//���� �ο� ����Ʈ���� �ش� ���� �ο� ����
											remMembs.remove(m);
											for(List<Member> day : allDays) {
												day.remove(m);
											}
											
											//�ο�����
											break outerloop;
									  }
								  }else if(a.isFull()) {
									  for(Member y : a.members) {
										  if(y.status != b.status) {
											  continue;
										  }
										  if(addedMems.contains(y))
												continue;
										  
										//�ο���� �׷� ����Ʈ iterate�ϸ鼭
										  for(Group u : notFullGroups) {
												//�ο� ���� ���� ����
												boolean dayMatch = false;
												for(int d : b.possibleDays) {
													if(u.day == d)
														dayMatch = true;
												}
												
												//���� ����� ���ɿ��ϰ� ��� 2�������� �ش��ϸ�
												if(dayMatch && b.locationPref[u.location.code] <= 6) {
													
													//���/���� ������ �����ϸ�
													if(y.status == Member.OFFICER && u.offNum == 2 && numOfGroupsWithThreeOfficers == 3 && a.offNum != 3) {
														continue;
													}
													if(y.status == Member.OFFICER && u.offNum > 2) {
														continue;
													}
													if(y.status == Member.MEMBER && u.memNum > 5) {
														continue;
													}
													if(y.locationPref[u.location.code] == 7) {
														continue;
													}
													
													if(y.gender == Member.BOY && u.boyNum == 3 && numOfGroupsWithFourBoys == 3 && a.boyNum != 4) {
														continue;
													}
													if(y.gender == Member.BOY && u.boyNum > 3) {
														continue;
													}
													if(y.gender == Member.GIRL && u.girlNum > 4) {
														continue;
													}
													
													//�ش� ����� �� �׷쿡 �߰��ϰ� ���� �׷쿡�� ����
													u.addMember(y);
													a.removeMember(y);
													
													//���� �׷쿡 �����ο� �߰�
													a.addMember(b);
													g.removeMember(b);
													
													g.addMember(m);
													
													successfullyAdded = true;
													success.add(m);
													successGroupNum = g.groupNum;
													
													pushedB = b.name;
													pushedBGroupNum = a.groupNum;
													
													pushedY = y.name;
													pushedYGroupNum = u.groupNum;
													
													//��� 3��, ���� 4���� �׷� �� ������Ʈ
													updateBoyOfficerNums();
													
													replacedBAddedHere = a;
													replacedYAddedHere = u;
													initialMAddedHere = g;
													
													//���� �ο� ����Ʈ���� �ش� ���� �ο� ����
													remMembs.remove(m);
													for(List<Member> day : allDays) {
														day.remove(m);
													}
													
													//�ο�����
													break outerloop;
												}
											}
									  }
								  }
							}
						}
					}
				}
				
				//���� �� �߰��� �ٸ� �׷��� 8���� ������ ������ �ο���� �׷� ����Ʈ���� ����
				if(replacedYAddedHere != null && replacedYAddedHere.isFull()) {
					notFullGroups.remove(replacedYAddedHere);
				}
				if(replacedBAddedHere != null && replacedBAddedHere.isFull()) {
					notFullGroups.remove(replacedBAddedHere);
				}
				if(initialMAddedHere != null && initialMAddedHere.isFull()) {
					notFullGroups.remove(initialMAddedHere);
				}
				
				if(successfullyAdded) {
					System.out.printf("성공: %s (to: 그룹 %d), replaced %s (to: 그룹 %d), replaced2 %s (to: 그룹 %d)\n",
							m.name, successGroupNum, pushedB, pushedBGroupNum, pushedY, pushedYGroupNum);				
				}
				
			}
		}
		
		System.out.println("----------");
		for(int i = 0; i < success.size(); i++) {
			if(i == success.size()-1) {
				System.out.print(success.get(i).name);
			}else {
				System.out.print(success.get(i).name + ", ");	
			}
		}
		System.out.println("\n----------");
		System.out.println("addRemainingMembers() end\n");
	}
	
	public void updateBoyOfficerNums() {
		int threeOfficers = 0;
		int fourBoys = 0;
		for(Group g : matched) {
			if(g.offNum == 3) {
				threeOfficers++;
			}
			
			if(g.boyNum == 4) {
				fourBoys++;
			}
		}
		
		numOfGroupsWithThreeOfficers = threeOfficers;
		numOfGroupsWithFourBoys = fourBoys;
	}
	
	public void printLocationMatchedGroups() {
		System.out.println("printLocationMatchedGroups() start");
		int cnt = 1;
		for(Group grp : matched) {
			System.out.printf("그룹 %d(%s, %s, %d명): ", cnt, grp.dayName, grp.location.name, grp.numOfMembers);
			for(int i = 0; i < grp.numOfMembers; i++) {
				Member curM = grp.members[i];
				if(i < grp.numOfMembers - 1)
					System.out.printf("%s(%s, %d), ", curM.name, (curM.status==Member.MEMBER)?"회":"운", curM.locationPref[grp.location.code]);
				else
					System.out.printf("%s(%s, %d)", curM.name, (curM.status==Member.MEMBER)?"회":"운", curM.locationPref[grp.location.code]);
			}
			cnt++;
			System.out.println();
			int boys = grp.boyNum;
			int girls = grp.girlNum;
			int officers = grp.offNum;
			int developers = grp.devNum;
			System.out.printf("*****남녀비율 = %d:%d, 운영진: %d명, 개발가능: %d명, 남은자리: %d\n", boys, girls, officers, developers, (8-grp.numOfMembers));
		}
		System.out.println("printLocationMatchedGroups() end");
		System.out.println("");
	}
	
	public void matchDayLocation() {
		numOfGroupsWithThreeOfficers = 0;
		numOfGroupsWithFourBoys = 0;
		
		int groupNum = 1;

		//그룹 9개 만들어서 최대한 채우기
		for(int i = 0; i < 6; i++) {
			int curDay = i;
			List<Member> curDays = allDays.get(curDay);
			if(curDays.size() == 0) {
				i--;
				continue;
			}
			
			Member curMember = null;
			if(curDay == Day.SUN) {
				for(Member her : curDays) {
					if(her.name.equals("�̰���")) { //�Ͽ��ϸ� ������ ��� �켱 ����
						curMember = her;
						break;
					}
				}
			}else if(curDay == Day.MON) {
				for(Member him : curDays) {
					if(him.name.equals("�����")) { //�����ϸ� ������ ��� �켱 ����
						curMember = him;
						break;
					}
				}
			}else if(curDay == Day.WED){
				for(Member him : curDays) {
					if(him.name.equals("������")) { //�����ϸ� ������ ��� �켱 ����
						curMember = him;
						break;
					}
				}
			}else if(curDay == Day.THU) {
				for(Member her : curDays) {
					if(her.name.equals("������")) { //����ϸ� ������ ��� �켱 ����
						curMember = her;
						break;
					}
				}
			}else {
				curMember = curDays.get(0);	
			}
			
			if(curMember == null) {
				curMember = curDays.get(0);
			}
			
		
			//1순위 장소
			int curNo1Loc = 0;
			if(curDay == Day.SUN) {
				curNo1Loc = Location.JONGGAK;
			}else if(curDay == Day.MON) {
				curNo1Loc = Location.SADANG;
			}else if(curDay == Day.TUE) {
				curNo1Loc = Location.JONGGAK;
			}else if(curDay == Day.WED) {
				curNo1Loc = Location.GANGNAM;
			}else if(curDay == Day.THU) {
				curNo1Loc = Location.SINCHON;
			}else {
				for(int j = 0; j < 7; j++) { 
					if(curMember.locationPref[j] == 1) {
						curNo1Loc = j;
					}
				}	
			}
			//
			
			//그룹 새로 만들고 현재 멤버 제일 먼저 더하기
			Group curGroup = new Group(curDay, locations[curNo1Loc], groupNum++);
			curGroup.addMember(curMember);

			
			//현재 멤버 모든 요일에서 없애기
			for(int k = 0; k < 6; k++) {
				List<Member> curOtherDays = allDays.get(k);
				curOtherDays.remove(curMember);
				for(Member m : addedMems) {
					curOtherDays.remove(m);
				}
			}
			//
			
			List<Member> iterateDays = new ArrayList<Member>();
			for(Member membr : curDays) {
				iterateDays.add(membr);
			}
			
			Iterator<Member> iterator = iterateDays.iterator(); 
			Member tempMember = null;
			while(!curGroup.isFull()) {
				if(iterator.hasNext()){
					tempMember = iterator.next();
					
					if(tempMember.status == Member.OFFICER && curGroup.offNum == 2 && numOfGroupsWithThreeOfficers == 3) {
						continue;
					}
					if(tempMember.status == Member.OFFICER && curGroup.offNum == 3) {
						continue;
					}
					if(tempMember.status == Member.MEMBER && curGroup.memNum == 6) {
						continue;
					}
					
					if(tempMember.gender == Member.BOY && curGroup.boyNum == 3 && numOfGroupsWithFourBoys == 3) {
						continue;
					}
					if(tempMember.gender == Member.BOY && curGroup.boyNum == 4) {
						continue;
					}
					if(tempMember.gender == Member.GIRL && curGroup.girlNum == 5) {
						continue;
					}
					
					if(tempMember.locationPref[curNo1Loc] <= 1) {
						curGroup.addMember(tempMember);
					}
				}else {
					break;
				}
			}
			
			for(int k = 0; k < 6; k++) {
				List<Member> tempOtherDays = allDays.get(k);
				
				for(int x = 0; x < curGroup.numOfMembers; x++) {
					Member mb = curGroup.members[x];
					tempOtherDays.remove(mb);
				}
			}
			
			matched.add(curGroup);
			if(curGroup.offNum == 3) {
				numOfGroupsWithThreeOfficers++;
			}
			if(curGroup.boyNum == 4) {
				numOfGroupsWithFourBoys++;
			}
		}
		
		for(int i = 0; i < 3; i++) {
			int curDay = random.nextInt(6);
			
			if(i == 0 || i == 1) {
				curDay = Day.TUE;
			}else{
				while(curDay == Day.TUE) {
					curDay = random.nextInt(6);
				}
			}
			
			List<Member> curDays = allDays.get(curDay);
			
			if(curDays.size() == 0) {
				i--;
				continue;
			}
			
			Member curMember = null;
			curMember = curDays.get(0);
			
			//1���� ���
			int curNo1Loc = 0;
			if(curDay == Day.TUE && i == 0) {
				curNo1Loc = Location.HYEHWA;
			}else {
				for(int j = 0; j < 7; j++) { 
					if(curMember.locationPref[j] == 1) {
						curNo1Loc = j;
					}
				}	
			}
			//
			
			//�׷� ���� ����� ���� ��� ���� ���� ���ϱ�
			Group curGroup = new Group(curDay, locations[curNo1Loc], groupNum++);
			curGroup.addMember(curMember);

			
			//���� ��� ��� ���Ͽ��� ���ֱ�
			for(int k = 0; k < 6; k++) {
				List<Member> curOtherDays = allDays.get(k);
				curOtherDays.remove(curMember);
				for(Member m : addedMems) {
					curOtherDays.remove(m);
				}
			}
			//
			
			List<Member> iterateDays = new ArrayList<Member>();
			for(Member membr : curDays) {
				iterateDays.add(membr);
			}
			
			Iterator<Member> iterator = iterateDays.iterator(); 
			Member tempMember = null;
			while(!curGroup.isFull()) {
				if(iterator.hasNext()){
					tempMember = iterator.next();
					if(tempMember.status == Member.OFFICER && curGroup.offNum == 2 && numOfGroupsWithThreeOfficers == 3) {
						continue;
					}
					if(tempMember.status == Member.OFFICER && curGroup.offNum == 3) {
						continue;
					}
					if(tempMember.status == Member.MEMBER && curGroup.memNum == 6) {
						continue;
					}
					
					if(tempMember.gender == Member.BOY && curGroup.boyNum == 3 && numOfGroupsWithFourBoys == 3) {
						continue;
					}
					if(tempMember.gender == Member.BOY && curGroup.boyNum == 4) {
						continue;
					}
					if(tempMember.gender == Member.GIRL && curGroup.girlNum == 5) {
						continue;
					}
					
					if(tempMember.locationPref[curNo1Loc] <= 1) {
						curGroup.addMember(tempMember);
					}
				}else {
					break;
				}
			}
			for(int k = 0; k < 6; k++) {
				List<Member> tempOtherDays = allDays.get(k);
				
				for(int x = 0; x < curGroup.numOfMembers; x++) {
					Member mb = curGroup.members[x];
					tempOtherDays.remove(mb);
				}
			}
			
			matched.add(curGroup);
			if(curGroup.offNum == 3) {
				numOfGroupsWithThreeOfficers++;
			}
			if(curGroup.boyNum == 4) {
				numOfGroupsWithFourBoys++;
			}
		}
		//9�� �ִ��� ä��
		
		//���� �ο��� ��� �� ����Ʈ�� �߰�
		//��� ���� �ʱ�ȭ
		for(int k = 0; k < allDays.size(); k++) {
			List<Member> curDay = allDays.get(k);
			curDay.clear();
		}
		//�� �߰��ϰ� ��Ī�� ȸ���� �� ����Ʈ (�����ο�)
		for(Member m : members) {
			remMembs.add(m);
		}
		//���� ����
		for(int k = 0; k < matched.size(); k++) {
			Group curGroup = matched.get(k);
			for(Member m : curGroup.members) {
				remMembs.remove(m);
			}
		}
		//���� �ο��� ���Ϻ��� ä���
		for(int h = 0; h < remMembs.size(); h++) {
			Member curMem = remMembs.get(h);
			for(int day : curMem.possibleDays) {
				allDays.get(day).add(curMem);
			}
		}
		
		Iterator daysIterator = allDays.iterator();
		
	}
	
	public void addAllPossibleDays() {
		for(int i = 0; i < members.length; i++) {
			for(int j = 0; j < members[i].possibleDaysCnt; j++) {
				switch(members[i].possibleDays[j]) {
				case Day.SUN:
					sunday.add(members[i]);
					break;
				case Day.MON:
					monday.add(members[i]);
					break;
				case Day.TUE:
					tuesday.add(members[i]);
					break;
				case Day.WED:
					wednesday.add(members[i]);
					break;
				case Day.THU:
					thursday.add(members[i]);
					break;
				case Day.FRI:
					friday.add(members[i]);
					break;
				}
			}
		}
		System.out.println("addAllPossibleDays()");
	}
	
	public void oneDay() {
		for(Member m : members) {
			if(m.possibleDaysCnt == 1)
				onlyOneDay.add(m);
		}
		
		for(Member mb : onlyOneDay) {
			System.out.print(mb.name + " ");
			String dayToPrint = null;
			switch(mb.possibleDays[0]) {
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
			System.out.println(dayToPrint);
		}
		System.out.println("oneDay()");
	}
	
	
}
