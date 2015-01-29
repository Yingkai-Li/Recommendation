package matching;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Match {
	private static ArrayList<Student> students = new ArrayList<Student>();
	private static ArrayList<University> universities = new ArrayList<University>();
	private static HashMap<Integer, ArrayList<Matched_School>> matching = new HashMap<Integer, ArrayList<Matched_School>>();
	private static HashMap<Integer, ArrayList<Integer>> according_matching = new HashMap<Integer, ArrayList<Integer>>();
	private static HashMap<Integer, ArrayList<Matched_School>> best_matching = new HashMap<Integer, ArrayList<Matched_School>>();
	private static ArrayList<ArrayList<Integer>> ranks = new ArrayList<ArrayList<Integer>>();
	private static double best_total_utility = 0;
	
	public static void main(String[] args) throws IOException{
		int i = 0;
		int count = 0;
		int adjust_student_index = 0;
		load_data();
		System.out.print(students.size() + " " + universities.size() + "\n");
		for (i = 0; i < universities.size(); i++) {
			according_matching.put(i, new ArrayList<Integer>());
		}
		for (Student s : students) {
			if (s.recommended_number >= s.get_prefernce_size()) {
				ArrayList<Matched_School> tmp_recommended_school = new ArrayList<Matched_School>();
				for (i = 0; i < s.get_prefernce_size(); i++) {
					Matched_School tmp = new Matched_School();
					tmp.rank = 0;
					tmp.school_id = s.get_preference_at(i);
					tmp_recommended_school.add(tmp);
				}
				matching.put(s.getId(), tmp_recommended_school);
			}
			else {
				ArrayList<Matched_School> tmp_recommended_school = new ArrayList<Matched_School>();
				for (i = 0; i < s.recommended_number; i++) {
					Matched_School tmp = new Matched_School();
					tmp.rank = 0;
					tmp.school_id = s.get_preference_at(i);
					tmp_recommended_school.add(tmp);
				}
				matching.put(s.getId(), tmp_recommended_school);
			}
		}
		for (i = 0; i < matching.size(); i++) {
			ArrayList<Matched_School> tmp = matching.get(i);
			for (int j = 0; j < tmp.size(); j++) {
				according_matching.get(tmp.get(j).school_id).add(i);
			}
		}
		while (true) {
			if (adjustable(adjust_student_index)) {
				adjust_matching(adjust_student_index);
			}
			else {
				if (adjust_student_index == students.size() - 1) {
					print_result();
					return;
				}
				else {
					adjust_student_index++;
					adjust_matching(adjust_student_index);
					for (i = 0; i < adjust_student_index; i++) {
						restore_intial_configuration(i);
					}
				}
			}
			ranks.clear();
			for (i = 0; i < according_matching.size(); i++) {
				ArrayList<Integer> tmp = new ArrayList<Integer>();
				for (int j = 0; j < according_matching.get(i).size(); j++) {
					tmp.add(j+1);
				}
				ranks.add(tmp);
			}
			perm(0, 0);
			count++;
			System.out.print(count + ":" + best_total_utility + "\n");
			//print_matching();
		}
	}
	
	private static void perm(int univ_index, int start_index) {
		if (univ_index == ranks.size()) {
			return;
		}
		ArrayList<Integer> buffer = ranks.get(univ_index);
		int end_index = buffer.size() - 1;
		if (end_index == -1) {
			perm(univ_index+1, 0);
		}
		if (start_index == end_index) {
			for (int i = 0; i < matching.size(); i++) {
				ArrayList<Matched_School> tmp = matching.get(i);
				for (int j = 0; j < tmp.size(); j++) {
					int index = according_matching.get(tmp.get(j).school_id).indexOf(i);
					tmp.get(j).rank = ranks.get(tmp.get(j).school_id).get(index);
				}
			}
			double utility = calculate_utility();
			//System.out.print(utility + "\n");
			if (utility > best_total_utility) {
				System.out.print(univ_index + ":" + start_index + "\n");
				best_total_utility = utility;
				best_matching.clear();
				for (int i = 0; i < students.size(); i++) {
					ArrayList<Matched_School> tmp = new ArrayList<Matched_School>();
					ArrayList<Matched_School> matching_tmp = matching.get(i);
					for (int j = 0; j < matching_tmp.size(); j++) {
						Matched_School tmp_school = new Matched_School();
						tmp_school.rank = matching_tmp.get(j).rank;
						tmp_school.school_id = matching_tmp.get(j).school_id;
						tmp.add(tmp_school);
					}
					best_matching.put(i, tmp);
				}
				//best_matching = new HashMap<Integer, ArrayList<Matched_School>>(matching);
				print_matching();
				print_result();
				print_according_matching();
				print_ranks();
			}
			//print_result();
			perm(univ_index+1, 0);
		}
		else {
			for (int i = start_index; i < end_index; i++) {
				int tmp = buffer.get(start_index);
				buffer.set(start_index, buffer.get(i));
				buffer.set(i, tmp);
				perm(univ_index, start_index + 1);
				tmp = buffer.get(start_index);
				buffer.set(start_index, buffer.get(i));
				buffer.set(i, tmp);
			}
		}
	}
	
	private static double calculate_utility() {
		double total_utility = 0.0;
		for (int i = 0; i < matching.size(); i++) {
			ArrayList<Matched_School> tmp = matching.get(i);
			double total_probability = 1.0;
			Student s = students.get(i);
			for (int j = 0; j < tmp.size(); j++) {
				double probability = calculate_probability(s.getGPA(), tmp.get(j).rank, tmp.get(j).school_id);
				total_utility += total_probability * probability * universities.get(tmp.get(j).school_id).weight;
				total_probability *= (1.0 - probability);
			}
		}
		return total_utility;
	}
	
	private static double calculate_probability(double score, int rank, int school_id) {
		double p =  (score * Math.sqrt(Math.sqrt(school_id))) / (Math.sqrt(rank) * 200.0);
		/*if (school_id == 0) {
			return 0.2;
		}
		if (school_id == 1) {
			return 0.3;
		}
		if (school_id == 2) {
			return 0.5;
		}
		if (school_id == 3) {
			return 0.6;
		}
		if (school_id == 4) {
			return 0;
		}
		if (school_id > 4) {
			return 0;
		}*/
		if (p > 1.0) {
			return 1.0;
		}
		else {
			return p;
		}
	}
	
	private static boolean adjustable(int student_id) {
		Student s = students.get(student_id);
		int num = s.get_prefernce_size() - s.recommended_number;
		if (num < 0) {
			return false;
		}
		else {
			//System.out.print(student_id + ":" + s.get_prefernce_size() + ":" + s.recommended_number + ":" + num + ":" + matching.get(student_id).get(0).school_id + ":" + s.get_preference_at(num));
			return !(matching.get(student_id).get(0).school_id == s.get_preference_at(num));
		}
	}
	
	private static void adjust_matching(int student_id) {
		Student s = students.get(student_id);
		int num = s.get_prefernce_size() - 1;
		ArrayList<Matched_School> tmp_matched_schools = matching.get(student_id);
		//if (tmp_matched_schools.get(index))
		for (int i = tmp_matched_schools.size() - 1; i >= 0; i--) {
			if (tmp_matched_schools.get(i).school_id == s.get_preference_at(num)) {
				num--;
			}
			else {
				num = s.find_preference_index(tmp_matched_schools.get(i).school_id) + 1;
				Matched_School tmp_matched = tmp_matched_schools.get(i);
				according_matching.get(tmp_matched.school_id).remove(Integer.valueOf(student_id));
				tmp_matched.school_id = s.get_preference_at(num);
				according_matching.get(tmp_matched.school_id).add(student_id);
				for (int j = i + 1; j < tmp_matched_schools.size() - 1; j++) {
					num++;
					Matched_School tmp = tmp_matched_schools.get(j);
					according_matching.get(tmp.school_id).remove(Integer.valueOf(student_id));
					tmp.school_id = s.get_preference_at(num);
					according_matching.get(tmp.school_id).add(student_id);
				}
				return;
			}
		}
	}
	
	private static void restore_intial_configuration(int student_id) {
		Student s = students.get(student_id);
		int i = 0;
		ArrayList<Matched_School> former_matched = matching.get(student_id);
		for (i = 0; i < former_matched.size(); i++) {
			according_matching.get(former_matched.get(i).school_id).remove(Integer.valueOf(student_id));
		}
		if (s.recommended_number >= s.get_prefernce_size()) {
			ArrayList<Matched_School> tmp_recommended_school = new ArrayList<Matched_School>();
			for (i = 0; i < s.get_prefernce_size(); i++) {
				Matched_School tmp = new Matched_School();
				tmp.rank = 0;
				tmp.school_id = s.get_preference_at(i);
				tmp_recommended_school.add(tmp);
				according_matching.get(tmp.school_id).add(student_id);
			}
			matching.replace(student_id, tmp_recommended_school);
		}
		else {
			ArrayList<Matched_School> tmp_recommended_school = new ArrayList<Matched_School>();
			for (i = 0; i < s.recommended_number; i++) {
				Matched_School tmp = new Matched_School();
				tmp.rank = 0;
				tmp.school_id = s.get_preference_at(i);
				tmp_recommended_school.add(tmp);
				according_matching.get(tmp.school_id).add(student_id);
			}
			matching.replace(student_id, tmp_recommended_school);
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
			tmp_university.weight = 1.0;
			//tmp_university.weight = 10.0 / (double)(i+1);
			//tmp_university.weight = 10 - i;
			universities.add(tmp_university);
		}
		/*for (i = 0; i < 15; i++) {
			Student stu = new Student();
			stu.setId(i+1);
			stu.setGPA(100.0 - i * 2);
			List<Integer> pref = new ArrayList<Integer>();
			for (int j = 0; j < university_num; j++) {
				pref.add(j);
			}
			stu.set_preference(pref);
			students.add(stu);
		}*/
		Student s0 = new Student();
		s0.setId(0);
		s0.setGPA(99.7);
		s0.recommended_number = 3;
		List<Integer> pref0 = new ArrayList<Integer>();
		//pref0.add(0);
		pref0.add(1);
		pref0.add(2);
		pref0.add(3);
		pref0.add(4);
		pref0.add(5);
		s0.set_preference(pref0);
		students.add(s0);
		
		Student s1 = new Student();
		s1.setId(1);
		s1.setGPA(90.7);
		s1.recommended_number = 3;
		List<Integer> pref1 = new ArrayList<Integer>();
		//pref1.add(0);
		pref1.add(1);
		pref1.add(2);
		pref1.add(3);
		pref1.add(4);
		pref1.add(5);
		s1.set_preference(pref1);
		students.add(s1);
		
		Student s2 = new Student();
		s2.setId(2);
		s2.setGPA(79.4);
		s2.recommended_number = 3;
		List<Integer> pref2 = new ArrayList<Integer>();
		pref2.add(1);
		pref2.add(2);
		pref2.add(3);
		pref2.add(4);
		pref2.add(5);
		pref2.add(6);
		pref2.add(7);
		s2.set_preference(pref2);
		students.add(s2);

		Student s3 = new Student();
		s3.setId(3);
		s3.setGPA(67.9);
		s3.recommended_number = 3;
		List<Integer> pref3 = new ArrayList<Integer>();
		pref3.add(2);
		pref3.add(3);
		pref3.add(4);
		pref3.add(5);
		pref3.add(6);
		pref3.add(7);
		pref3.add(8);
		pref3.add(9);
		s3.set_preference(pref3);
		students.add(s3);

		Student s4 = new Student();
		s4.setId(4);
		s4.setGPA(55.7);
		s4.recommended_number = 3;
		List<Integer> pref4 = new ArrayList<Integer>();
		pref4.add(1);
		pref4.add(2);
		pref4.add(3);
		pref4.add(4);
		pref4.add(5);
		pref4.add(6);
		pref4.add(7);
		pref4.add(8);
		pref4.add(9);
		s4.set_preference(pref4);
		students.add(s4);
	}
	
	private static void print_result() {
		System.out.print("best_total_utility: " + best_total_utility + "\n");
		for (int j = 0; j < best_matching.size(); j++) {
			System.out.print(j + ":\n");
			ArrayList<Matched_School> tmp = best_matching.get(j);
			for (int k = 0; k < tmp.size(); k++) {
				System.out.print(tmp.get(k).school_id + " " + tmp.get(k).rank + "\n");
			}
		}
		System.out.print("best_total_utility: " + calculate_beat_utility() + "\n");
	}
	
	private static double calculate_beat_utility() {
		double total_utility = 0.0;
		for (int i = 0; i < best_matching.size(); i++) {
			ArrayList<Matched_School> tmp = best_matching.get(i);
			double total_probability = 1.0;
			Student s = students.get(i);
			for (int j = 0; j < tmp.size(); j++) {
				double probability = calculate_probability(s.getGPA(), tmp.get(j).rank, tmp.get(j).school_id);
				total_utility += total_probability * probability * universities.get(tmp.get(j).school_id).weight;
				total_probability *= (1.0 - probability);
			}
		}
		return total_utility;
	}
	
	private static void print_matching() {
		System.out.print("matching:\n");
		for (int j = 0; j < matching.size(); j++) {
			System.out.print(j + ":\n");
			ArrayList<Matched_School> tmp = matching.get(j);
			for (int k = 0; k < tmp.size(); k++) {
				System.out.print(tmp.get(k).school_id + " " + tmp.get(k).rank + "\n");
			}
		}
	}
	
	private static void print_according_matching() {
		System.out.print("according_matching:\n");
		for (int j = 0; j < according_matching.size(); j++) {
			System.out.print(j + ": ");
			ArrayList<Integer> tmp = according_matching.get(j);
			for (int k = 0; k < tmp.size(); k++) {
				System.out.print(tmp.get(k) + " ");
			}
			System.out.print("\n");
		}
	}
	
	private static void print_ranks() {
		System.out.print("ranks:\n");
		for (int j = 0; j < ranks.size(); j++) {
			System.out.print(j + ":\n");
			ArrayList<Integer> tmp = ranks.get(j);
			for (int k = 0; k < tmp.size(); k++) {
				System.out.print(tmp.get(k) + " " + tmp.get(k) + "\n");
			}
		}
	}
}
