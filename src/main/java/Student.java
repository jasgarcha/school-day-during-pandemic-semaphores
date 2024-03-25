import java.util.Random;
import java.util.concurrent.Semaphore;

//Student.
public class Student implements Runnable {
	private Thread thread;
	public static long time; //Time the thread begins execution.
	private Random RNG; //Random number generator.
	private int idNumber; //Unique integer to identify a Student.  
	private boolean gender; //Student's gender. true is female, false is male. 

	public Student(int idNumber) {
		thread = new Thread(this);
		thread.setName("Student-"+idNumber);
		time = System.currentTimeMillis();
		RNG = new Random();
		this.idNumber = idNumber;
		gender = RNG.nextBoolean();
	}

	public void run() {
		School school = SchoolDayDuringCOVID.PS1111;
		SchoolYard schoolYard = school.getSchoolYard();
		Restroom boysRestroom = school.getBoysRestroom();
		Restroom girlsRestroom = school.getGirlsRestroom();
		Classroom classroom = school.getClassroom();
		SchoolArea auditorium = school.getAuditorium();
		
		/* semaphore mutex = 1. Implements Mutual Exclusion over Critical Sections. */
		Semaphore mutex = new Semaphore(1, true); 
		
		//Students wakes up and gets ready.
		msg("Wakes up and gets ready."); 
		//Student starts health questionnaire.
		msg("Starts health questionnaire.");
		//Filling out health questionnaire. Simulated by sleep random time between 0 milliseconds and 5 seconds. 
		try {
			Thread.sleep((long)RNG.nextInt(5000)); 
		} catch (InterruptedException e) {
			System.err.print(e.getMessage()); //Thread is interrupted during sleep. 
		} 
		//Student completes health questionnaire.
		msg("Completes health questionnaire.");
		
		//Student begins commute.
		msg("Begins commute to school.");
		//Commuting. Simulated by sleep random time between 0 milliseconds and 5 seconds. 
		try {
			Thread.sleep((long)RNG.nextInt(5000)); 
		} catch (InterruptedException e) {
			System.err.print(e.getMessage()); //Thread is interrupted during sleep. 
		}
		//Student ends commute and arrives at school.
		msg("Ends commute to school. Arrives at school."); 
		
		//Student enters the schoolyard.
		msg("Enters the schoolyard.");
		schoolYard.enter(this); 
		
		/* Students signals entrance to schoolyard. 
		 * In case Teacher arrived first and was waiting (blocked) for a Student, Teacher is released.
		 */
		V(schoolYard.getStudentArrivalSemaphore());
		
		/* Student waits (blocked) in the schoolyard until called by Teacher (signals).
		 * Student: P(schoolyard); schoolyard.value: schoolyard.value->schoolyard.value-1; schoolyard.queue: {...,Student}.
		 * Student: blocked on schoolyard.
		 */		
		msg("Waits in the schoolyard.");
		P(schoolYard.getSchoolYardSemaphore());
		/* Student is called (signaled) from the schoolyard by the Teacher. 
		 * If blocked:
		 * Student: released from schoolyard.
		 */
		msg("Called by the teacher from the schoolyard.");
		
		/* Implement Mutual Exclusion over incrementing the numberOfStudentsCalled. */
		P(mutex);
		schoolYard.setNumberOfStudentsCalled(schoolYard.getNumberOfStudentsCalled()+1);  //numberOfStudentsCalled++;
		V(mutex);
		
		/* Student exits the schoolyard.
		 * Student: exits the schoolyard.
		 */
		msg("Exits the schoolyard."); 
		schoolYard.exit(this);
		
		//Student heads to the restroom.
		msg("Heads to the restroom."); 
		Restroom restroom = new Restroom(); 
		String genderDescriptor = new String();
		if(gender == false) { //Student is a boy.
			restroom = boysRestroom;			
			genderDescriptor = "boys'";
		}
		if(gender == true) { //Student is a girl.
			restroom = girlsRestroom;
			genderDescriptor = "girls'";
		}
		//Student gets on the restroom line of his or her respective gender.
		msg("Gets on the "+genderDescriptor+" restroom line.");
		restroom.getOnLine(this); 
	
		/* Student waits to enter the restroom. 
		 * If the restroom is full, Student is blocked until another Student signals that space in the restroom is available.
		 * If restroom is not full (restroom.value > 0):
		 * Student: P(restroom); restroom.value: 3,2,1->2,1,0; restroom.queue: {}
		 * If restroom is full (restroom.value < 0): 
		 * Student: P(restroom); restroom.value: restroom.value->restroom.value-1; restroom.queue: {....,Student}
		 * Student: blocked on restroom.
		 */
		msg("Waits to enter the "+genderDescriptor+" restroom.");
		P(restroom.getRestroomSemaphore());
		/* Restroom room is available (was already available or became available after a Student exits and signals).
		 * Student: enters the restroom.
		 * If blocked on restroom:
		 * Student: released from restroom. 
		 * Student: enters the restroom.
		 */
		
		//Students gets off the restroom line. 
		msg("Gets off the "+genderDescriptor+" restroom line.");
		restroom.getOffLine(this); 
		//Student enters the restroom.
		msg("Enters the "+genderDescriptor+" restroom.");
		restroom.enter(this); 
		
		//Student washes hands simulated by sleep random time 0 milliseconds and 5 seconds. 
		try {
			msg("Starts washing hands.");
			Thread.sleep((long)RNG.nextInt(5000)); 
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		}
		//Student finishes washing hands and exits the restroom.
		msg("Finishes washing hands. Exits the "+genderDescriptor+" restroom.");
		restroom.exit(this); 
		
		/* Student is done using the restroom. Student signals restroom.
		 * If a Student is blocked on restroom, a Student is released from restroom.queue.
		 * Student: V(restroom); restroom: restroom.value->restroom.value+1; restroom.queue: {...}
		 */
		V(restroom.getRestroomSemaphore());
		
		//Attend class.
		while(!school.dayEnd()) {
			//Class is in session. Implement Mutual Exclusion over if class is in session. 
			P(mutex);			
			if(classroom.isSession()) {
				//Students in the class when the class is in session. Implement Mutual Exclusion over if Student is in the classroom when class is in session. 			
				if(classroom.getStudents().contains(this)) {
					V(mutex);
					msg("Waits for class to end.");
					/* Students cannot wait (blocked) for class to end. */
					P(classroom.getEndSession());
					/* (Teacher signaled) Class ended. Student signals another Student class is over. */
					V(classroom.getEndSession()); 
					/* Student exits classroom. */
					/* After the last class period, the school day ends. Implement Mutual Exclusion over if the period is past the last period. */ 
					P(mutex);
					if(school.getPeriod() > School.NUMBER_OF_PERIODS) {
						V(mutex);
						classroom.exit(this);
					}
					//After class periods that are not the last period. 
					else {
						V(mutex);
						//"Student(s) will leave the classroom and hurry to have some fun between classes."
						msg("Class ended. Leaves the the classroom and hurries to have some fun between classes.");
						classroom.exit(this);
						try {
							Thread.sleep((long)RNG.nextInt(500)); //Leaves the the classroom and hurries to have some fun between classes simulated by sleep of random time between 0 ms and 500 ms.
						} catch (InterruptedException e) {
							System.err.println(e.getMessage()); //Thread is interrupted during sleep. 
						}
					}
				}
				//Students not in the class attempting to enter while class is in session.
				//"By the time a student gets to class, if the class is already in session, the student(s) will leave for a while (sleep of random time) and walk around the campus and come back later on."
				else {
					V(mutex);
					msg("Cannot enter a class in session. Leaves and walks around the campus.");
					try {
						Thread.sleep((long)RNG.nextInt(5000)); //Walk around campus while class in session simulated by sleep of random time between 0 milliseconds and 5 seconds.
					} catch (InterruptedException e) {
						System.err.println(e.getMessage()); //Thread is interrupted during sleep. 
					}
				}
			}
			//Class is not in session.
			else {
				V(mutex);
				/* Teacher is not in the classroom. Implement Mutual Exclusion over if Teacher has entered the classroom. */
				P(mutex);
				if(!classroom.isTeacherPresent()) {
					V(mutex);
					//Student enters the auditorium.
					auditorium.enter(this);
					msg("Enters auditorium.");
					/* Student waits (blocked) in the auditorium for the Teacher to arrive. */ 
					P(classroom.getTeacherArrival());
					/* Student exits auditorium after the Teacher has arrived (signaled by Teacher). 
					 * Student signals Teacher arrival to another Student. 
					 */
					V(classroom.getTeacherArrival());
					auditorium.exit(this);
					msg("Exits auditorium after Teacher arrives."); 
				}
				//Teacher is in the classroom.
				else {
					V(mutex);
					/* Students not in the classroom when class is in session. Implement Mutual Exclusion over if the Student is not in the classroom when class is in session. */
					P(mutex);
					if(!classroom.getStudents().contains(this)) { 
						V(mutex);
						/* Student enters the classroom. Student waits (blocked) for class to begin. */
						classroom.enter(this); 
						msg("Enters the classroom. Waits for class to begin.");
						P(classroom.getBeginSession());
						/* Class has begun (signaled by Teacher). Student signals class beginning to another Student. */
						V(classroom.getBeginSession());
					}
					else {
						V(mutex);
					}
				}				
			}
		}
		//School day is over. Student waits to leave after attending last class.
		msg("School day is over.");
		
		/* Student N waits for Student N+1 to leave school. 
		 * Wait until all Students are ready to leave school. 
		 */
		while(!school.allStudentsAreWaitingToLeave()) {
			/* Implement Mutual Exclusion over incrementing numberOfStudentsWaitingToLeave. */
			P(mutex);
			if(thread.getName().equals("Student-"+(school.getNumberOfStudents()-school.getNumberOfStudentsWaitingToLeave()))) {
				msg("Waits to leave school after attending last class.");
				school.setNumberOfStudentsWaitingToLeave(school.getNumberOfStudentsWaitingToLeave()+1); //numberOfStudentsWaitingToLeave++;
				/* The last waiting Student signals to leave. */ 
				if(school.getNumberOfStudentsWaitingToLeave() == school.getNumberOfStudents()-1) {
					V(school.getStudentLeave());
				}
				V(mutex);
				P(school.getStudentLeave());
			}
			else {
				V(mutex);
			}
		}
		msg("Leaves school.");
		/* Student N+1 signals Students N to leave school. */ 
		V(school.getStudentLeave());
		
		/* Implement Mutual Exclusion over incrementing numberStudentsGone and if all Students have gone. */
		P(mutex);
		/* Student increments numberStudentsGone after leaving school. */
		school.setNumberOfStudentsGone(school.getNumberOfStudentsGone()+1); //numberOfStudentsGone++;
		/* The last Student to leave signals Teacher. Teacher leaves after the last Student has left. */
		if(school.getNumberOfStudentsGone() == school.getNumberOfStudents()) {
			V(mutex);
			V(school.getTeacherLeave()); 
		}
		else {
			V(mutex);
		}
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
	
	public int getIdNumber() {
		return idNumber;
	}
}