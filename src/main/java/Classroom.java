import java.util.concurrent.Semaphore;

public class Classroom extends SchoolArea{
	/* beginSession: A (blocking) semaphore initialized to 0.
	 * Students wait for the class session to begin after entering the classroom and before the class begins. Teacher signals when class begins session. 
	 * True fairness so Student does not starve while waiting for class to begin.
	 */
	private Semaphore beginSession;
	/* endSession: A (blocking) semaphore initialized to 0.
	 * Students wait for the class to end after class begins. Teacher signals when class ends session. 
	 * True fairness so Student does not starve while waiting for class to end.
	 */
	private Semaphore endSession;
	/* teacherArrival: A (blocking) semaphore initialized to 0.
	 * Students wait (in the Auditorium) for the Teacher to enter the classroom.
	 * True fairness so Student does not starve while waiting for the Teacher to enter the classroom. 
	 */
	private Semaphore teacherArrival;	
	/* session and teacherPresent are Critical Sections updated by Teacher and read by Student. Implement Mutual Exclusion over operations on session and teacherPresent. */ 
	/* session: Teacher sets session to true when class begins. Teacher sets session to false when class ends. */
	private boolean session;
	/* teacherPresent: Teacher is present in the classroom after entering the classroom. */
	private boolean teacherPresent;
		
	public Classroom() {
		super();
		beginSession = new Semaphore(0, true);		
		endSession = new Semaphore(0, true);
		teacherArrival = new Semaphore(0, true);
		session = false; //Class is not in session.
		teacherPresent = false; //Teacher is not present in the classroom. 
	}
	
	public void setSession(boolean session) {
		this.session = session;
	}
	
	public void setBeginSession(Semaphore beginSession) {
		this.beginSession = beginSession;
	}

	public void setEndSession(Semaphore endSession) {
		this.endSession = endSession;
	}

	/* Teacher enters the classroom. Implement Mutual Exclusion over setting teacherPresent true. */
	public void enter(Teacher teacher) {
		P(mutex);
		teacherPresent = true;
		V(mutex);
	}
	
	public boolean isTeacherPresent() {
		return teacherPresent;
	}

	public Semaphore getBeginSession() {
		return beginSession;
	}

	public Semaphore getEndSession() {
		return endSession;
	}

	public Semaphore getTeacherArrival() {
		return teacherArrival;
	}

	public boolean isSession() {
		return session;
	}
}