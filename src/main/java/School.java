import java.util.concurrent.Semaphore;

public class School {
	private int numberOfStudents; //The number of students. The default number of students is 13.
	private SchoolYard schoolYard; //Schoolyard.
	private Restroom boysRestroom; //Boys' restroom.
	private Restroom girlsRestroom; //Girls' restroom.
	private Classroom classroom; //Classroom.
	private SchoolArea auditorium; //Auditorium.
	public static final int NUMBER_OF_PERIODS = 2; //The number of periods in the school day.
	/* period: The current class period of the school day.
	 * Class starts at period 1.
	 * period counter is a Critical Section updated by Teacher and read by Teacher and Student. Implement Mutual Exclusion over operations on period. 
	 */
	private int period; 	
	/* binary semaphore mutex = 1. Implements Mutual Exclusion over Critical Sections. */
	private Semaphore mutex; 
	/* studentLeave: A (blocking) semaphore initialized to 0.
	 * Student N waits for Student N+1 to leave school at the end of the school day.
	 * True fairness so Student N does not starve while waiting for for Student N+1 to leave school. 
	 * FIFO order is essential to guarantee Students leave in decreasing order of their ID.
	 */
	private Semaphore studentLeave;
	/* teacherLeave: A (blocking) semaphore initialized to 0.
	 * Teacher waits (blocked) for the last Student to leave before leaving. The last Student to leave signals Teacher to leave.
	 * False fairness because only Teacher blocks on teacherLeave. FIFO order is not required for teacherLeave.queue. 
	 */
	private Semaphore teacherLeave;
	/* numberOfStudentsWaitingToLeave and numberOfStudentsGone counters are Critical Sections updated and read by Students and Teacher. 
	 * Implement Mutual Exclusion over operations on numberOfStudentsGone and numberOfStudentsWaitingToLeave 
	 */
	/* numberOfStudentsWaitingToLeave: The number of Students waiting to leave school at the end of the school day. 
	 * 0 Students are waiting to leave at the beginning of the school day. 
	 */
	private int numberOfStudentsWaitingToLeave;
	/* numberOfStudentsGone: The number of Students who have left school and gone home. The last Student to leave school signals Teacher to leave.
	 * 0 Students have gone at the beginning of the school day.	
	 */
	private int numberOfStudentsGone; 

	public School() { 
		numberOfStudents = 13;
		schoolYard = new SchoolYard();
		boysRestroom = new Restroom();
		girlsRestroom = new Restroom();
		classroom = new Classroom();
		auditorium = new SchoolArea();
		period = 1;
		mutex = new Semaphore(1, true);
		studentLeave = new Semaphore(0, true);
		teacherLeave = new Semaphore(0, false);
		numberOfStudentsWaitingToLeave = 0; 
		numberOfStudentsGone = 0; 
	}
	
	public int getNumberOfStudents() {
		return numberOfStudents;
	}

	public void setNumberOfStudents(int numberOfStudents) {
		this.numberOfStudents = numberOfStudents;
	}

	public SchoolYard getSchoolYard() {
		return schoolYard;
	}
	
	public Restroom getBoysRestroom() {
		return boysRestroom;
	}

	public Restroom getGirlsRestroom() {
		return girlsRestroom;
	}

	public Classroom getClassroom() {
		return classroom;
	}
	
	public SchoolArea getAuditorium() {
		return auditorium;
	}
	
	public int getPeriod() {
		return period;
	}
	
	public void setPeriod(int period) {
		this.period = period;
	}
	
	public Semaphore getStudentLeave() {
		return studentLeave;
	}
	
	public Semaphore getTeacherLeave() {
		return teacherLeave;
	}
	
	/* The school day ends after the last period. Implement Mutual Exclusion over if the period is before or is the last period. */
	public boolean dayEnd() {
		P(mutex);
		if(period <= NUMBER_OF_PERIODS) {
			V(mutex);
			return false;
		}
		else {
			V(mutex);
			return true;
		}
	}

	public int getNumberOfStudentsWaitingToLeave() {
		return numberOfStudentsWaitingToLeave;
	}

	public void setNumberOfStudentsWaitingToLeave(int numberOfStudentsWaitingToLeave) {
		this.numberOfStudentsWaitingToLeave = numberOfStudentsWaitingToLeave;
	}
	
	public int getNumberOfStudentsGone() {
		return numberOfStudentsGone;
	}

	public void setNumberOfStudentsGone(int numberOfStudentsGone) {
		this.numberOfStudentsGone = numberOfStudentsGone;
	}
	
	/* Students wait to leave school. Implement Mutual Exclusion over if all the Students are waiting to leave school. */
	public boolean allStudentsAreWaitingToLeave() {
		P(mutex);
		if(numberOfStudentsWaitingToLeave == numberOfStudents) {
			V(mutex);
			return true;
		}
		else {
			V(mutex);
			return false;
		}
	}
	
	//Wait.
	private void P(Semaphore semaphore) {
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			System.out.println("Wait is an atomic operation. Wait cannot be interrupted and two threads cannot Wait on a Semaphore at the same time. ");
			System.err.println(e.getMessage());
		}
	}
	
	//Signal.
	private void V(Semaphore semaphore) {
		semaphore.release();
	}
}