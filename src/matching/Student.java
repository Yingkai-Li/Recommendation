package matching;

import java.util.ArrayList;
import java.util.List;

public class Student {
	private int Id;
	private double GPA;
	private ArrayList<Integer> preference = new ArrayList<Integer>();
	public boolean tmp_match_status = false;
	public int propose_num = 0;
	
	public void set_preference(List<Integer> pref) {
		preference = new ArrayList<Integer>(pref);
	}
	
	public int get_preference_at(int i) {
		return preference.get(i);
	}
	
	public int get_next_preference() {
		return preference.get(propose_num);
	}
	
	public boolean has_next_preference() {
		if (propose_num >= preference.size()) {
			return false;
		}
		else {
			return true;
		}
	}
	
	public void remove_preference(int Id) {
		preference.remove(Integer.valueOf(Id));
	}
	
	public int get_prefernce_size() {
		return preference.size();
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public double getGPA() {
		return GPA;
	}

	public void setGPA(double gPA) {
		GPA = gPA;
	}
}
