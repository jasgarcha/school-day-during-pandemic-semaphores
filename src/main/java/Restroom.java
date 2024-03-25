import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Restroom extends SchoolArea {
	public static final int MAX_CAPACITY = 3; //The maximum number of Students allowed in a restroom at a time is 3.
	private Queue<Student> line; //Students use the restroom on a First Coming First Serve (FCFS) basis implemented with a First In First Out (FIFO) queue.
	/* restroomSemaphore: A counting semaphore initialized to the maximum capacity of the restroom (3). 
	 * A Student waits (blocked if all 3 spaces are taken) to enter the restroom. Student signals after done (releasing a space and allowing a waiting Student to enter).
	 * True fairness so Student does not starve while waiting to use the restroom.
	 */
	private Semaphore restroomSemaphore;
	
	public Restroom() {
		super();
		line = new LinkedList<>(); //Students who arrive first are the first out.
		restroomSemaphore = new Semaphore(MAX_CAPACITY, true);
	}
	
	public Semaphore getRestroomSemaphore() {
		return restroomSemaphore;
	}

	//Student gets on the restroom line. The first one on the line is the first one off the line to enter the restroom.
	public void getOnLine(Student student) {
		line.add(student);
	}
	
	//Student gets off the restroom line. The first one off the line is the first one on the line to enter the restroom.
	public void getOffLine(Student student) {
		line.remove(student);
	}
}