# school-day-during-pandemic-semaphores

Synchronize the students and teacher (threads) using semaphores and operations on semaphores.

This project demonstrates my familiarity with semaphores--a fundamental Operating Systems concept.

## Usage
Arguments: 
`-s, --students`: The number of students as a positive integer.

If no arguments are provided, the default number of students is 13.

## Logic
"During this COVID time students at PS1111 have to follow strict rules. After a student
wakes up and gets ready for a new school day, (s)he will complete a Health
Questionnaire (simulate this using a sleep of random time). Next s(he) will commute
to school. (sleep of random time).

Once arrived at school, student(s) will wait in the schoolyard to be called by the
teacher (the teacher will call each of them). Once called by the teacher, before
entering the classroom, students must wash their hands. They will head to the
restrooms. There are two restrooms, one for “girls” and one for “boys.” The
capacity of the restroom is three. You can decide if a student is a boy or a girl using
a random number or you can consider that students with an odd id are boys while
the others are girls. Students will wait their turn to use the restrooms.

By the time a student gets to class, if the class is already in session, the student(s)
will leave for a while (sleep of random time) and walk around the campus and
come back later on. If the class is not in session yet, student(s) will wait for the
teacher to arrive and enter the auditorium.

Once the class is in session, students will immediately get bored and cannot wait for
the class to end. The teacher will teach (simulate it using sleep for a fixed interval
of time) and when done, he will let the students know that the class ended.

Student(s) will leave the classroom and hurry to have some fun between classes
(sleep of random time). (Note: use an implementation similar to the one for source
graph. The teacher will signal one student only.)

Once having arrived at the school the teacher will let students in. During the day,
he will teach 2 periods. Each class takes a fixed amount of the time period. Between
any two classes there is a break.

The school closes after the two classes end. At the end of school day, students leave
the school. Each student will wait another student; they will leave in decreasing
order of their name or their ID.

The teacher will wait until the last student leaves and after that he will terminate as
well.

A daily report with information about what classes and when each student attended
throughout the day must be displayed. It can be displayed by the teacher before
terminating or in the main method." (Dr. Simina Fluture)