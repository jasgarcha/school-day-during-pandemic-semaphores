import java.util.concurrent.Semaphore;

public class SchoolYard extends SchoolArea {
	/* studentArrivalSemaphore: A (blocking) semaphore initialized to 0.
	 * Teacher waits (blocked) for a Student to enter the schoolyard. 
	 * True fairness so Teacher does not starve while waiting for a Student to arrive.
	 */
	private Semaphore studentArrivalSemaphore;
	/* schoolYardSemaphore: A (blocking) semaphore initialized to 0.
	 * Student waits to be called (signaled) by Teacher. Teacher calls Students waiting (blocked) in the schoolyard. 
	 * True fairness so a Student does not starve while waiting to be called by Teacher.
	 */
	private Semaphore schoolYardSemaphore; 
	/* numberOfStudentsCalled: The number of Students called by Teacher from the schoolyard. 
	 * Initialized to 0 to indicate no Students have yet been called by Teacher.
	 * numberOfStudentsCalled counter is a Critical Section because it is updated by Student and read by Teacher. Implement Mutual Exclusion over operations on numberOfStudentsCalled. 
	 */
	private int numberOfStudentsCalled; 

	public SchoolYard() {
		super();
		schoolYardSemaphore = new Semaphore(0, true); 
		studentArrivalSemaphore = new Semaphore(0, true);
		numberOfStudentsCalled = 0;
	}

	public Semaphore getSchoolYardSemaphore() {
		return schoolYardSemaphore;
	}

	public Semaphore getStudentArrivalSemaphore() {
		return studentArrivalSemaphore;
	}

	public int getNumberOfStudentsCalled() {
		return numberOfStudentsCalled;
	}

	public void setNumberOfStudentsCalled(int numberOfStudentsCalled) {
		this.numberOfStudentsCalled = numberOfStudentsCalled;
	}
}