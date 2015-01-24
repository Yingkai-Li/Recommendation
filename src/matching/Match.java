package matching;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Match {
	private static ArrayList<Student> students = new ArrayList<Student>();
	private static ArrayList<University> universities = new ArrayList<University>();
	
	public static void main(String[] args) throws IOException{
		int i = 0;
		boolean flag = true;
		load_data();
		System.out.print(students.size() + " " + universities.size() + "\n");
		while (exist_unmatch_preference()) {
			//print_student();
			flag = true;
			while (flag) {
				flag = false;
				for (i = 0; i < students.size(); i++) {
					//System.out.print(i + " " + students.get(i).tmp_match_status + " " + students.get(i).propose_num + "\n");
					if (students.get(i).tmp_match_status == false && students.get(i).has_next_preference()) {
						flag = true;
						int preferred_university = students.get(i).get_next_preference();
						if (universities.get(preferred_university).prefers(students.get(i))) {
							universities.get(preferred_university).remove_former_connected_student();
							universities.get(preferred_university).alter_connected_student(students.get(i));
							students.get(i).tmp_match_status = true;
						}
						else {
							students.get(i).propose_num++;
						}
					}
				}
			}
			for (i = 0; i < universities.size(); i++) {
				universities.get(i).add_tmp_connected_student_to_final_list();
			}
			for (i = 0; i < students.size(); i++) {
				students.get(i).propose_num = 0;
			}
		}
		for (i = 0; i < universities.size(); i++) {
			System.out.print(i + ":");
			for (int j = 0; j < universities.get(i).student_list.size(); j++) {
				System.out.print(universities.get(i).student_list.get(j) + " ");
			}
			System.out.print("\n");
		}
	}
	
	private static void print_student() {
		for (int i = 0; i < students.size(); i++) {
			System.out.print(i + " " + students.get(i).get_prefernce_size() + ":");
			for (int j = 0; j < students.get(i).get_prefernce_size(); j++) {
				System.out.print(students.get(i).get_preference_at(j) + " ");
			}
			System.out.print("\n");
		}
		System.out.print("\n");
	}
	
	private static void load_data() {
		int i = 0;
		int university_num = 10;
		for (i = 0; i < university_num; i++) {
			University tmp_university = new University();
			tmp_university.Id = i;
			universities.add(tmp_university);
		}
		for (i = 0; i < 15; i++) {
			Student stu = new Student();
			stu.setId(i+1);
			stu.setGPA(100.0 - i * 2);
			List<Integer> pref = new ArrayList<Integer>();
			for (int j = 0; j < university_num; j++) {
				pref.add(j);
			}
			stu.set_preference(pref);
			students.add(stu);
		}
		/*Student s1 = new Student();
		s1.setId(1);
		s1.setGPA(92.7);
		List<Integer> pref1 = new ArrayList<Integer>();
		pref1.add(1);
		pref1.add(2);
		pref1.add(3);
		//pref1.add(4);
		//pref1.add(5);
		s1.set_preference(pref1);
		students.add(s1);
		
		Student s2 = new Student();
		s2.setId(2);
		s2.setGPA(89.4);
		List<Integer> pref2 = new ArrayList<Integer>();
		pref2.add(3);
		pref2.add(1);
		pref2.add(4);
		pref2.add(2);
		pref2.add(5);
		s2.set_preference(pref2);
		students.add(s2);

		Student s3 = new Student();
		s3.setId(3);
		s3.setGPA(87.9);
		List<Integer> pref3 = new ArrayList<Integer>();
		pref3.add(3);
		pref3.add(4);
		pref3.add(2);
		pref3.add(5);
		pref3.add(6);
		s3.set_preference(pref3);
		students.add(s3);

		Student s4 = new Student();
		s4.setId(4);
		s4.setGPA(85.7);
		List<Integer> pref4 = new ArrayList<Integer>();
		pref4.add(4);
		pref4.add(3);
		pref4.add(5);
		pref4.add(6);
		pref4.add(7);
		s4.set_preference(pref4);
		students.add(s4);*/
	}
	
	private static boolean exist_unmatch_preference() {
		for (int i = 0; i < students.size(); i++) {
			if (students.get(i).get_prefernce_size() != 0) {
				return true;
			}
		}
		return false;
	}
}
