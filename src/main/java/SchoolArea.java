import java.util.ArrayList;
import java.util.concurrent.Semaphore;

//School Area: a part of school which may contain Students or Teachers.
public class SchoolArea {
	protected ArrayList<Student> students; //Students in the school area.
	/* currentCapacity: The current capacity of the School Area. 
	 * currentCapacity counter is a Critical Section because multiple Students can update while Teacher or Student read. Implement Mutual Exclusion over operations on currentCapacity.
	 * Initialized to 0 to indicate empty.
	 */
	protected int currentCapacity; 
	/* binary semaphore mutex = 1. Implements Mutual Exclusion over Critical Sections. */
	protected Semaphore mutex;
	
	public SchoolArea() {
		students = new ArrayList<>();
		currentCapacity = 0;
		mutex = new Semaphore(1, true);
	}
	
	/* Student enters the school area. Implement Mutual Exclusion over incrementing currentCapacity because Students entering can concurrently increment the counter. */
	public void enter(Student student) {
		students.add(student);
		P(mutex);
		currentCapacity++; //Two Students cannot increment the capacity when entering a school area at the same time.
		V(mutex);
	}
		
	/* Student exits the school area. Implement Mutual Exclusion over decrementing currentCapacity because Students exiting can concurrently decrement the counter. */
	public void exit(Student student) {
		students.remove(student);
		P(mutex); 
		currentCapacity--; //Two Students cannot decrement the capacity when exiting a school area at the same time.
		V(mutex);
	}
	
	public int getCurrentCapacity() {
		return currentCapacity;
	}

	public ArrayList<Student> getStudents() {
		return students;
	}
	
	//Wait.
	protected void P(Semaphore semaphore) {
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			System.out.println("Wait is an atomic operation. Wait cannot be interrupted and two threads cannot Wait on a Semaphore at the same time. ");
			System.err.println(e.getMessage());
		}
	}
	
	//Signal.
	protected void V(Semaphore semaphore) {
		semaphore.release();
	}
}