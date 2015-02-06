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
	private static ArrayList<ArrayList<Integer>> rank_result = new ArrayList<ArrayList<Integer>>();
	private static double best_total_utility = 0;
	
	public static void main(String[] args) throws IOException{
		int i = 0;
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
		arrange_assigned_school(0);
		print_result();
		//System.out.print(check_fairness());
	}
	
	private static void arrange_assigned_school(int adjust_student_index) {
		int i = 0;
		if (adjustable(adjust_student_index)) {
			if (adjust_student_index == students.size() - 1) {
				ranks.clear();
				rank_result.clear();
				for (i = 0; i < according_matching.size(); i++) {
					ArrayList<Integer> tmp = new ArrayList<Integer>();
					ArrayList<Integer> tmp2 = new ArrayList<Integer>();
					for (int j = 0; j < according_matching.get(i).size(); j++) {
						tmp.add(j+1);
					}
					ranks.add(tmp);
					rank_result.add(tmp2);
				}
				perm(0);
				System.out.print("AAAAAA" + "\n");
				print_matching();
				System.out.print("BBBBBB" + "\n");
			}
			else {
				for (i = adjust_student_index + 1; i < students.size(); i++) {
					restore_intial_configuration(i);
				}
				arrange_assigned_school(adjust_student_index+1);
			}
			adjust_matching(adjust_student_index);
			arrange_assigned_school(adjust_student_index);
		}
		else {
			if (adjust_student_index == students.size() - 1) {
				ranks.clear();
				rank_result.clear();
				for (i = 0; i < according_matching.size(); i++) {
					ArrayList<Integer> tmp = new ArrayList<Integer>();
					ArrayList<Integer> tmp2 = new ArrayList<Integer>();
					for (int j = 0; j < according_matching.get(i).size(); j++) {
						tmp.add(j+1);
					}
					ranks.add(tmp);
					rank_result.add(tmp2);
				}
				perm(0);
				System.out.print("AAAAAA" + "\n");
				print_matching();
				System.out.print("BBBBBB" + "\n");
				return;
			}
			else {
				arrange_assigned_school(adjust_student_index+1);
			}
		}
	}
	
	private static void perm(int univ_index) {
		ArrayList<Integer> permList = ranks.get(univ_index);
		ArrayList<Integer> resList = rank_result.get(univ_index);
		int length = permList.size();
		if (length == 0) {
			if (univ_index == ranks.size() - 1) {
				update_max_utility_by_matching();
			}
			else {
				perm(univ_index+1);
			}
		}
		else {
			long fac = 1;
			long index = 0;
			int j = 0;
			int w = 0;
			for (int i = 1; i <= length; i++) {
				fac *= i;
			}
			for (index = 0; index < fac; index++) {
				long tmp = index;
				resList.clear();
				for (j = 1; j <= length; j++) {
					w = (int) (tmp % j);
					resList.add(w, permList.get(j-1));
					tmp = tmp / j;
				}
				if (univ_index == ranks.size() - 1) {
					update_max_utility_by_matching();
				}
				else {
					perm(univ_index+1);
				}
			}
		}
	}
	
	private static void update_max_utility_by_matching() {
		for (int i = 0; i < matching.size(); i++) {
			ArrayList<Matched_School> tmp = matching.get(i);
			for (int j = 0; j < tmp.size(); j++) {
				int index = according_matching.get(tmp.get(j).school_id).indexOf(i);
				tmp.get(j).rank = rank_result.get(tmp.get(j).school_id).get(index);
			}
		}
		double utility = calculate_utility();
		if (utility > best_total_utility && check_fairness()) {
			System.out.print("best_total_utility:" + utility + "\n");
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
				total_utility += total_probability * probability * s.get_weight(tmp.get(j).school_id);
				total_probability *= (1.0 - probability);
			}
		}
		return total_utility;
	}
	
	private static double calculate_probability(double score, int rank, int school_id) {
		double p =  (score * Math.sqrt(Math.sqrt(school_id+1))) / (Math.sqrt(rank) * 200.0);
		if (rank > 1) {
			return 0;
		}
		if (score > 95) {
			if (school_id == 0) {
				return 0.2;
			}
			if (school_id == 1) {
				return 0.5;
			}
			if (school_id == 2) {
				return 0.5;
			}
			if (school_id == 3) {
				return 1;
			}
		}
		else {
			if (school_id == 0) {
				return 0.2;
			}
			if (school_id == 1) {
				return 0.4;
			}
			if (school_id == 2) {
				return 0.5;
			}
			if (school_id == 3) {
				return 1;
			}
		}
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
			return !(matching.get(student_id).get(0).school_id == s.get_preference_at(num));
		}
	}
	
	private static void adjust_matching(int student_id) {
		Student s = students.get(student_id);
		int num = s.get_prefernce_size() - 1;
		ArrayList<Matched_School> tmp_matched_schools = matching.get(student_id);
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
				for (int j = i + 1; j < tmp_matched_schools.size(); j++) {
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
	
	private static boolean check_fairness() {
		double this_util = 0.0;
		double lower_util = 0.0;
		for (int i = 0; i < matching.size(); i++) {
			this_util = 0.0;
			ArrayList<Matched_School> tmp = matching.get(i);
			double total_probability = 1.0;
			Student s = students.get(i);
			for (int j = 0; j < tmp.size(); j++) {
				double probability = calculate_probability(s.getGPA(), tmp.get(j).rank, tmp.get(j).school_id);
				this_util += total_probability * probability * s.get_weight(tmp.get(j).school_id);
				total_probability *= (1.0 - probability);
			}
			for (int k = i+1; k < matching.size(); k++) {
				lower_util = 0.0;
				total_probability = 1.0;
				ArrayList<Matched_School> tmptmp = matching.get(k);
				for (int j = 0; j < tmptmp.size(); j++) {
					double probability = calculate_probability(s.getGPA(), tmptmp.get(j).rank, tmptmp.get(j).school_id);
					lower_util += total_probability * probability * s.get_weight(tmptmp.get(j).school_id);
					total_probability *= (1.0 - probability);
				}
				if (lower_util > this_util) {
					//System.out.print(i + ":" + this_util + ", " + k + ":" + lower_util + "\n");
					return false;
				}
			}
		}
		return true;
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
		int university_num = 4;
		for (i = 0; i < university_num; i++) {
			University tmp_university = new University();
			tmp_university.Id = i;
			universities.add(tmp_university);
		}
		Student s0 = new Student();
		s0.setId(0);
		s0.setGPA(99.7);
		s0.recommended_number = 2;
		s0.add_weight(0, 10.0);
		s0.add_weight(1, 7.0);
		s0.add_weight(2, 6.0);
		s0.add_weight(3, 5.0);
		//s0.add_weight(4, 2.0);
		//s0.add_weight(5, 1.0);
		s0.set_preference_from_weight();
		students.add(s0);
		
		Student s1 = new Student();
		s1.setId(1);
		s1.setGPA(90.7);
		s1.recommended_number = 2;
		s1.add_weight(0, 10.0);
		s1.add_weight(1, 8.0);
		s1.add_weight(2, 6.0);
		s1.add_weight(3, 10.0);
		//s1.add_weight(4, 4.0);
		//s1.add_weight(5, 2.0);
		s1.set_preference_from_weight();
		students.add(s1);
		
		/*Student s2 = new Student();
		s2.setId(2);
		s2.setGPA(79.4);
		s2.recommended_number = 3;
		s2.add_weight(0, 10.0);
		s2.add_weight(1, 10.0);
		s2.add_weight(2, 9.0);
		s2.add_weight(3, 7.0);
		s2.add_weight(4, 5.0);
		s2.add_weight(5, 3.0);
		s2.add_weight(6, 1.0);
		s2.set_preference_from_weight();
		students.add(s2);

		/*Student s3 = new Student();
		s3.setId(3);
		s3.setGPA(67.9);
		s3.recommended_number = 3;
		s3.add_weight(0, 10.0);
		s3.add_weight(1, 10.0);
		s3.add_weight(2, 10.0);
		s3.add_weight(3, 8.0);
		s3.add_weight(4, 6.0);
		s3.add_weight(5, 4.0);
		s3.add_weight(6, 3.0);
		s3.add_weight(7, 1.0);
		s3.set_preference_from_weight();
		students.add(s3);*/
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
		System.out.print("best_total_utility: " + calculate_best_utility() + "\n");
	}
	
	private static double calculate_best_utility() {
		double total_utility = 0.0;
		for (int i = 0; i < best_matching.size(); i++) {
			ArrayList<Matched_School> tmp = best_matching.get(i);
			double total_probability = 1.0;
			Student s = students.get(i);
			for (int j = 0; j < tmp.size(); j++) {
				double probability = calculate_probability(s.getGPA(), tmp.get(j).rank, tmp.get(j).school_id);
				total_utility += total_probability * probability * s.get_weight(tmp.get(j).school_id);;
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
