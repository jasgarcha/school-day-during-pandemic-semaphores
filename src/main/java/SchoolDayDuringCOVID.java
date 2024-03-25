public class SchoolDayDuringCOVID {
	public static final School PS1111 = new School(); //PS1111. The school both Students and Teacher attend (Student and Teacher threads access) following a hybrid teaching regimen.
	public static long time; //Time the main thread begins execution.
	
	/**
	 * @param args Usage: -s, --students: The number of students as a positive integer. No arguments: the default number of students (13).
	 */
	public static void main(String[] args) {
		//"Initialize the time at the beginning of the main method, so that it is unique to all threads."
		time = System.currentTimeMillis();
		
		//Input validation for -s command line argument.
		if(args.length == 1 || args.length > 2){ 
			System.err.println("Usage: -s, --students: The number of students as a positive integer. No arguments: the default number of students (13).");
			System.exit(-1);
		}
		if(args.length == 2) {
			if(args[0].equals("-s") || args[0].equals("--students")) {
				try {
					int numberOfStudents = Integer.parseInt(args[1]);
					if(numberOfStudents < 0) {
						System.err.println("Usage: -s, --students: The number of students as a positive integer. No arguments: the default number of students (13).");
						System.exit(-1);
					}
					//Valid.
					else { 
						PS1111.setNumberOfStudents(numberOfStudents);	
					}
				} catch(Exception e) {
					System.err.println("Usage: -s, --students: The number of students as a positive integer. No arguments: the default number of students (13).");
					System.exit(-1);
				}
			}
			else {
				System.err.println("Usage: -s, --students: The number of students as a positive integer. No arguments: the default number of students (13).");
				System.exit(-1);
			}
		}
		
		//Create students.
		Student[] students = new Student[PS1111.getNumberOfStudents()];
		for(int i = 0; i < students.length; i++) {
			students[i] = new Student(i+1); //The first Student Id is 1.
		}
		
		//Create teacher.
		Teacher teacher = new Teacher();
			
		//Start the Student thread.
		for(int i = 0; i < students.length; i++) {
			students[i].start();
		}
	
		//Start the teacher thread.
		teacher.start();
	}
}