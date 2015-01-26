package matching;

import java.util.ArrayList;

public class University {
	public int Id;
	public ArrayList<Integer> student_list = new ArrayList<Integer>();
	public double weight;
	private Student tmp_connected_student = null;
	
	public boolean prefers(Student student)
	{
		if (tmp_connected_student == null) {
			return true;
		}
		else {
			if (student.getGPA() > tmp_connected_student.getGPA()) {
				return true;
			}
			else {
				return false;
			}
		}
	}
	
	public void alter_connected_student(Student student)
	{
		tmp_connected_student = student;
	}
	
	public void add_tmp_connected_student_to_final_list()
	{
		if (tmp_connected_student != null) {
			student_list.add(tmp_connected_student.getId());
			tmp_connected_student.tmp_match_status = false;
			tmp_connected_student.propose_num = 0;
			tmp_connected_student.remove_preference(Id);
			tmp_connected_student = null;
		}
	}
	
	public void remove_former_connected_student()
	{
		if (tmp_connected_student != null) {
			tmp_connected_student.tmp_match_status = false;
			tmp_connected_student.propose_num++;
		}
	}
}
