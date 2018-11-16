import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 * This is the main class that launches the PDF parsing algorithm and is initialized by the GUI.
 */

public class AppManager {

    public boolean isSetup = false;
    Loader loader;

    CalendarView gui;

    public ArrayList<Course> userCourseList = new ArrayList<Course>();
    public ArrayList<Course> groupCourseList = new ArrayList<>();

    public ArrayList<Integer> freeTimeList = new ArrayList<>();

    String userName;
    String userID;

    File schedulePDF;


    int minimumLength = 1;


    int tempStartTime;
    int tempEndTime;


    /**
     * This method is called by the GUI to initialize an instance of the Loader class and proceed with identifying the user's schedule.
     * @param file This file is the PDF schedule passed in from the file selector window.
     * @throws IndexOutOfBoundsException Exception thrown in case the software is already set up and needs to be reset prior to usage.
     */

    public void loadSchedule(File file) throws IndexOutOfBoundsException{

            if (isSetup == true) {
                throw new IndexOutOfBoundsException("The software has already been activated, please reset it if you want to change the schedule");
            }
            loader = new Loader(file);
            //File file = new File("C:\\Users\\Alexh\\Desktop\\EclipseWorkspace\\SE 300\\ER_SCHED_PRT.pdf");
            userCourseList = loader.getCourseList();
            userName = loader.getUserName();
            userID = loader.getUserID();
            System.out.println("The student ID number is " + userID);
            System.out.println("The student's name is " + userName);
            for (Course x : userCourseList){
                x.timeConvert();
                //System.out.println("The military time is :" + x.startTime + " " + x.endTime);
            }

            findCommonTime(userCourseList);

            isSetup = true;

    }


    public void connectDatabase (){

        int number = 1;



        try {

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            Connection con = DriverManager.getConnection("jdbc:sqlserver://den1.mssql8.gear.host", "meetme", "Re2x?S-Omepy");

            /*Statement stmt = con.createStatement();

            String SQL = "SELECT TOP " + number + " * FROM dbo.nameTable";
            ResultSet rs = stmt.executeQuery(SQL);

            while (rs.next()) {
                System.out.println(rs.getString("FirstName") + " " + rs.getString("LastName") + " " + rs.getInt("StudentID"));
            }*/
        }catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }



    /**
     * This method is used to start the process of resetting the software settings to default.
     */
    public void resetState(){
        userCourseList.clear();
        groupCourseList.clear();
        userID = "";
        userName = "";
        loader.reset();
        System.out.println("The program status has been reset");
    }

    /**
     * This method builds a 2D array of all of the courses provided by courseList and finds all of the empty time sections that are larger than minimum length.
     * @param courseList This is the list of all the courses that are passed into the method
     */
    public void findCommonTime(ArrayList<Course> courseList){
        int [][] timeTable = new int[7][24];

        for (Course course : courseList){

            int startCoef;
            int midCoef = 0;
            int endCoef;

            startCoef = course.startTime / 100;
            endCoef = course.endTime / 100;
            if (endCoef - startCoef > 1){
                midCoef = (endCoef + startCoef)/2;
            }

            for (String day : course.Days){
                switch (day){
                    case "Monday": timeTable[0][startCoef] = 1;
                        if (midCoef != 0){timeTable[0][midCoef] = 1;}
                        timeTable[0][endCoef] = 1; break;
                    case "Tuesday":timeTable[1][startCoef] = 1;
                        if (midCoef != 0){timeTable[1][midCoef] = 1;}
                        timeTable[1][endCoef] = 1; break;
                    case "Wednesday":timeTable[2][startCoef] = 1;
                        if (midCoef != 0){timeTable[2][midCoef] = 1;}
                        timeTable[2][endCoef] = 1; break;
                    case "Thursday":timeTable[3][startCoef] = 1;
                        if (midCoef != 0){timeTable[3][midCoef] = 1;}
                        timeTable[3][endCoef] = 1; break;
                    case "Friday":timeTable[4][startCoef] = 1;
                        if (midCoef != 0){timeTable[4][midCoef] = 1;}
                        timeTable[4][endCoef] = 1; break;
                    case "Saturday":timeTable[5][startCoef] = 1;
                        if (midCoef != 0){timeTable[5][midCoef] = 1;}
                        timeTable[5][endCoef] = 1; break;
                    case "Sunday":timeTable[6][startCoef] = 1;
                        if (midCoef != 0){timeTable[6][midCoef] = 1;}
                        timeTable[6][endCoef] = 1; break;
                    default: break;
                }
            }

        }

        for (int[] x : timeTable)
        {
            int count = 0;
            for (int y = 0; y < 24; y++)
            {

                if (x[y] == 0){

                    count++;
                    if(count == 1) {
                        tempStartTime = y * 100;
                    }

                    if (y == 23){
                        tempEndTime = 2400;
                        freeTimeList.add(tempStartTime);
                        freeTimeList.add(tempEndTime);
                    }
                } else if (x[y] == 1){

                    if (count >= minimumLength){

                        tempEndTime = y * 100;

                        freeTimeList.add(tempStartTime);
                        freeTimeList.add(tempEndTime);

                        tempStartTime = 0;
                        tempEndTime = 0;


                    }

                    count = 0;
                }


                System.out.print(x[y] + " ");
            }
            System.out.println();
        }

        for (int a = 0; a < freeTimeList.size(); a++){
            System.out.println(freeTimeList.get(a));
        }

    }



}
