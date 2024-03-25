import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Semaphore;

//Teacher.
public class Teacher implements Runnable {
	private Thread thread;
	public static long time; //Time the thread begins executed.
	private Random RNG; //Random number generator.
	private int breakTime; //A random break between any two teaching periods during the school day.
	private Map<Student, ArrayList<Integer>> attendance; //The attendance of a Student in a class period.

	public Teacher() {
		thread = new Thread(this);
		thread.setName("Teacher");
		time = System.currentTimeMillis();
		RNG = new Random();
		///Break time is either: before 1st class, after 1st class/before 2nd class, after 2nd class.
		//breakTime = RNG.nextInt(School.NUMBER_OF_PERIODS)+1; 
		//However, Teacher "will teach 2 periods." Because there are only 2 class periods and "between any two classes there is a break," the only break time is between the 1st and 2nd class.
		breakTime = 1; //After 1st period and before 2nd period.
		attendance = new HashMap<>();
	}
	
	public void run() {
		School school = SchoolDayDuringCOVID.PS1111;
		SchoolYard schoolYard = school.getSchoolYard();
		Classroom classroom = school.getClassroom();
		
		/* binary semaphore mutex = 1. Implements Mutual Exclusion over Critical Sections. */
		Semaphore mutex = new Semaphore(1, true); 
		
		//Teacher arrives at school.
		msg("Arrives at school"); 
		
		/* Teacher waits for a Student to enter the schoolyard. 
		 * If a Student is already in the schoolyard (signaled) then the Teacher will call a student. Otherwise, the Teacher will block until a Student enters (signals).
		 * If a Student is not in the schoolyard:
		 * Teacher: P(student); student.value: 0->-1; student.queue: {Teacher}.
		 * Teacher: blocked on student.
		 */
		P(schoolYard.getStudentArrivalSemaphore());
		/* Student entered the schoolyard and signaled arrival to Teacher. 
		 * If blocked, Teacher will be released. If a Student was already in the schoolyard, Teacher continues execution without blocking.
		 */
		
		/* The counter numberOfStudentsCalled tracks the number of Students Teacher has called. 
		 * Teacher calls Students until all Students have been called. 
		 */
		int numberOfStudents = school.getNumberOfStudents();
		while(schoolYard.getNumberOfStudentsCalled() != numberOfStudents) {
			/* Implement Mutual Exclusion over if schoolYardSemaphore has queued threads because the number of queued threads changes during execution. */
			P(mutex);
			if(schoolYard.getSchoolYardSemaphore().hasQueuedThreads()) { 
				V(mutex);
				/* Teacher calls (signals) a Student waiting in the schoolyard. 
				 * Teacher: V(schoolyard). schoolyard.value: schoolyard.value->schoolyard.value+1; schoolyard.queue: {...}
				 */
				msg("Calls a student from the schoolyard.");
				V(schoolYard.getSchoolYardSemaphore());
			}
			else { 
				V(mutex);
			}
		}
		/* Teacher has called all Students from the schoolyard. All Students have been called (signaled) by Teacher. */
		msg("Called all students from the schoolyard.");
		
		//Teacher walks to the classroom simulated by random time between 0 milliseconds and 5 seconds. 
		try {
			Thread.sleep((long)RNG.nextInt(5000)); 
		} catch (InterruptedException e) {
			System.err.println(e.getMessage()); //Thread is interrupted during sleep. 
		}	
		
		//Teacher arrives and enters the classroom (teacherPresent is set true).
		classroom.enter(this); 		
		/* Teacher signals entrance to classroom. Students waiting (blocked) for the Teacher are released. */
		V(classroom.getTeacherArrival());
		msg("Enters the classroom.");	
		
		//Prepares to teach classes.
		try {
			Thread.sleep(500); 
		} catch (InterruptedException e) {
			System.err.println(e.getMessage()); //Thread is interrupted during sleep. 
		}
		
		//Teach class until the school day ends.
		while(!school.dayEnd()) {
			//Teacher sets classroom session true (class is in session). Implement Mutual Exclusion over setting classroom session true.
			P(mutex);
			classroom.setSession(true);
			V(mutex);
			/* Teacher signals the beginning of class. */ 
			V(classroom.getBeginSession()); 
			msg("Teaches period "+school.getPeriod()+". Class period "+school.getPeriod()+" starts.");
			//Teacher takes attendance of students in class.
			takeAttendance(classroom, school.getPeriod()); 
			try {
				Thread.sleep(20000); //Each class is a fixed length of 20 seconds. 
			} catch (InterruptedException e) {
				System.err.println(e.getMessage()); //Thread is interrupted during sleep.
			}
			/* Teacher signals the end of class. */
			msg("Class period "+school.getPeriod()+" ends.");
			V(classroom.getEndSession());
			//Teacher sets classroom session false (class is not in session). Implement Mutual Exclusion over setting the classroom session false.
			P(mutex);
			classroom.setSession(false);
			V(mutex);
			//No bell after the last period. Implement Mutual Exclusion over if the period is the last class period.
			P(mutex);
			if(school.getPeriod() == School.NUMBER_OF_PERIODS) {
				V(mutex);
			}
			else {
				//Teacher is on break.
				if(school.getPeriod() == breakTime) {
					msg("On break.");
					try {
						Thread.sleep(5000); //Break time is a fixed length of 5 seconds.
					} catch (InterruptedException e) {
						System.err.println(e.getMessage()); //Thread is interrupted during sleep.
					}
				}
				//Bell to allow Students to enter between classes.
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					System.err.println(e.getMessage()); //Thread is interrupted during sleep.
				}
				//Clear the beginSession and endSession semaphores' values and queues.
				classroom.setBeginSession(new Semaphore(0, true));		
				classroom.setEndSession(new Semaphore(0, true));
				V(mutex);
			}
			/* Implement Mutual Exclusion over incrementing the class period to the next class period. */
			P(mutex);
			school.setPeriod(school.getPeriod()+1); //period++;
			V(mutex);
		}
		//Teacher has taught all classes.
		msg("Taught all classes.");
		
		/* Teacher waits (blocked) for the last Student to leave (signals). */
		msg("School day is over. Waits for the last student to leave.");
		P(school.getTeacherLeave());
		/* The last Student left, signaling Teacher to leave. Teacher (released) leaves. */		
		msg("Leaves after the last student has left.");
		
		//"A daily report with information about what classes and when each student attended".
		dailyReport();
	}
	
	//Wait.
	public void P(Semaphore semaphore) {
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			System.out.println("Wait is an atomic operation. Wait cannot be interrupted and two threads cannot Wait on a Semaphore at the same time. ");
			System.err.println(e.getMessage());
		}
	}
	
	//Signal.
	public void V(Semaphore semaphore) {
		semaphore.release();
	}
	
	public void start() {
		thread.start();
	}
	
	//Standardizes output, from the Project 1 specifications. Modified getName() to thread.getName() because implements Runnable versus extends Thread. 
	public void msg(String m) {
		System.out.println("["+(System.currentTimeMillis()-time)+"] "+thread.getName()+": "+m);
	}
	
	//Take attendance of the students in a class period.
	private void takeAttendance(Classroom classroom, int period) {
		for(int i = 0; i < classroom.getStudents().size(); i++) { 
			ArrayList<Integer> periodsAttended;
			Student student = classroom.getStudents().get(i);
			if(attendance.containsKey(student)) {
				periodsAttended = attendance.get(student);
			}
			else {
				periodsAttended = new ArrayList<>();
			}
			periodsAttended.add(period);
			attendance.put(student, periodsAttended);
		}
	}
	
	//"A daily report with information about what classes and when each student attended".
	private void dailyReport() {
		msg("Daily Report:");
		System.out.println("Student Id | Total Number of Attended Classes | Period Number");
		Set<Student> presentStudents = attendance.keySet();
		for(Student presentStudent: presentStudents) {
			ArrayList<Integer> periodsAttended = attendance.get(presentStudent);
			System.out.print("Student-"+presentStudent.getIdNumber()+" | " + periodsAttended.size() + " | ");
			for(int i = 0; i < periodsAttended.size(); i++) {
				if(i == periodsAttended.size()-1) {
					System.out.print(periodsAttended.get(i));
				}
				else {
					System.out.print(periodsAttended.get(i)+",");
				}
			}
			System.out.println();
		}
	}
}