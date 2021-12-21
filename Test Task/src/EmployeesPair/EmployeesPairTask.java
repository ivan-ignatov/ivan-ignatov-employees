package EmployeesPair;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;


/*  This class has methods for reading a text file, storing the data from the file into lists and then outputs data.
    The text file is located in the res folder of the project and has the name "SampleData.txt".

    The file contains data of employee IDs, project IDs and start and end dates.

    The problem is finding the two employees who have worked together the longest.

    There are two lists used to solve this problem: the data on each line and
    a list of each existing pair of employees who have ever worked together.

    Some of the End Dates in the file have the value "NULL", DateTimeFormatter and LocalDateTime are used to get today's date in the same format.
* */
public class EmployeesPairTask {
    private static List<DataLine> objectsList = new ArrayList<>();
    private static List<String> pairsList = new ArrayList<>();
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /*  A Method for opening and reading each line in the data text file.

        Removes whitespaces from each line and splits it into four parts, using the comma as a delimiter.

        The current date is stored if the End Date has a NULL value.

        The data from each line is then stored in a new DataLine Object using the setters for the fields in the object,
        The object in turn is added to a list.
    * */
    public static void getFileData() throws FileNotFoundException {
        DataLine dataLineObject;

        LocalDateTime now = LocalDateTime.now();

        File textFile = new File("res/SampleData.txt");
        Scanner fileScanner = new Scanner(textFile);
        String fileLine;


        while (fileScanner.hasNextLine()) {
            fileLine = fileScanner.nextLine();
            fileLine.replace(" ", "");
            String[] lineFields = fileLine.split(",");

            if(lineFields[3].equals("NULL")) {
                lineFields[3] = dtf.format(now);
            }

            dataLineObject = new DataLine();
            dataLineObject.setEmployeeID(lineFields[0]);
            dataLineObject.setProjectID(lineFields[1]);
            dataLineObject.setDateStart(lineFields[2]);
            dataLineObject.setDateEnd(lineFields[3]);

            objectsList.add(dataLineObject);
        }
    }

    /*  This Method sorts the objects list
        first by lowest employee id, then by lowest project id
        each project section of the data is sorted with the lowest employee id first

        this ensures that when adding data for employee pairs, the data in the pairs list will always have the lowest employee id first,
        which eases finding existing pairs in it
    * */
    public static void sortObjectList() {
        Collections.sort(objectsList, new Comparator<DataLine>() {
            @Override
            public int compare(final DataLine object1, final DataLine object2) {
                return object1.getEmployeeID().compareTo(object2.getEmployeeID());
            }
        });

        Collections.sort(objectsList, new Comparator<DataLine>() {
            @Override
            public int compare(final DataLine object1, final DataLine object2) {
                return object1.getProjectID().compareTo(object2.getProjectID());
            }
        });
        //System.out.println(objectsList + "\n");// PRINT OBJECT LIST    <= REMOVE

    }

    /*  This method is used to store data in the Pairs Data List, which will contain all the pairs and their corresponding number of days they worked together.
        Iterates through the objects list until it reaches the second to last element.

        Checks if the next element has the same project id, if they are different, the iteration is skipped
        Otherwise, checks if the two time periods are overlapping and then calculates the number of overlapping days if they do.

        Checks the employees' IDs if they exist in the Pairs Data List. If they do, the number of days are added to the current one.
        If they do not, a new element is added to the list with the current pair's IDs and the number of days they worked together.
        The Object List is sorted, meaning that the pairs will always have the smaller ID written first.
    * */
    public static void loadPairsData() {
        String currentPersonID;
        String nextPersonID;

        String currentProjectID;
        String nextProjectID;

        LocalDate currentStart;
        LocalDate  currentEnd;
        LocalDate  nextStart;
        LocalDate  nextEnd;

        long daysWorkedTogether;

        for (int i = 0; i < objectsList.size() - 1; i++) {
            for (int j = i + 1; j < objectsList.size(); j++) {
                currentProjectID = objectsList.get(i).getProjectID();
                nextProjectID = objectsList.get(j).getProjectID();

                currentPersonID = objectsList.get(i).getEmployeeID();
                nextPersonID = objectsList.get(j).getEmployeeID();

                if (Objects.equals(currentProjectID, nextProjectID) && !Objects.equals(currentPersonID, nextPersonID)) {

                    currentStart = LocalDate.parse(objectsList.get(i).getDateStart());
                    currentEnd = LocalDate.parse(objectsList.get(i).getDateEnd());
                    nextStart = LocalDate.parse(objectsList.get(j).getDateStart());
                    nextEnd = LocalDate.parse(objectsList.get(j).getDateEnd());

                    if (currentEnd.isBefore(nextStart) || nextEnd.isBefore(currentStart)) {
                        continue;
                    } else {

                        daysWorkedTogether = daysWorkedTogether(currentStart, currentEnd, nextStart, nextEnd);
                        replaceInList(currentPersonID, nextPersonID, daysWorkedTogether);

                    }
                }
            }
        }
    }


    /*
        This method searches for a pair in the pairs data list. If the pair is found, the days are added to the old value.
        If the pair is not found, the found Flag is not set to true and the pair is added as a new element in the list.

        Since the pairs always need to start with the lower ID, a check before the loop ensures that the firstPersonID variable will have the lower value.
    / */
    private static void replaceInList(String firstPersonID, String secondPersonID, long daysWorkedTogether) {
        String[] arrayElement;
        long updatedDays;
        boolean foundFlag = false;
        String tempVar;

        if(Integer.parseInt(firstPersonID) > Integer.parseInt(secondPersonID)){
            tempVar = firstPersonID;
            firstPersonID = secondPersonID;
            secondPersonID = tempVar;
        }

        for (int i = 0; i < pairsList.size(); i++) {
            arrayElement = pairsList.get(i).split(",");
            if(Objects.equals(arrayElement[0], firstPersonID) && Objects.equals(arrayElement[1], secondPersonID)){
                updatedDays = Long.parseLong(arrayElement[2]) + daysWorkedTogether;
                foundFlag = true;
                pairsList.set(i, firstPersonID + "," + secondPersonID + "," + Long.toString(updatedDays));
            }
        }

        if(!foundFlag) {
            pairsList.add(firstPersonID + "," + secondPersonID + "," + Long.toString(daysWorkedTogether));

        }
    }

    /*  A method for finding the number of overlapping days in the given time periods.
        There are 8 cases in which the time periods can overlap. 4 for when either the beginning or end are the same date.
    * */

    public static long daysWorkedTogether(LocalDate firstStart, LocalDate firstEnd, LocalDate secondStart, LocalDate secondEnd) {
        long daysBetween = 0;

        if(firstStart.isBefore(secondStart) && firstEnd.isBefore(secondEnd)) {
            daysBetween = ChronoUnit.DAYS.between(secondStart, firstEnd);

        } else if(secondStart.isBefore(firstStart) && secondEnd.isBefore(firstEnd)) {
            daysBetween =  ChronoUnit.DAYS.between(firstStart, secondEnd);

        } else if(firstStart.isBefore(secondStart) && secondEnd.isBefore(firstEnd)) {
            daysBetween =  ChronoUnit.DAYS.between(secondStart, secondEnd);

        } else if(secondStart.isBefore(firstStart) && firstEnd.isBefore(secondEnd)) {
            daysBetween =  ChronoUnit.DAYS.between(firstStart, firstEnd);

        } else if(firstStart.isEqual(secondStart) && firstEnd.isBefore(secondEnd)) {
            daysBetween = ChronoUnit.DAYS.between(secondStart, firstEnd);

        } else if(secondStart.isEqual(firstStart) && secondEnd.isBefore(firstEnd)) {
            daysBetween =  ChronoUnit.DAYS.between(firstStart, secondEnd);

        } else if(firstStart.isBefore(secondStart) && secondEnd.isEqual(firstEnd)) {
            daysBetween =  ChronoUnit.DAYS.between(secondStart, secondEnd);

        } else if(secondStart.isBefore(firstStart) && firstEnd.isEqual(secondEnd)) {
            daysBetween =  ChronoUnit.DAYS.between(firstStart, firstEnd);

        }

        return daysBetween;
    }

    /*
        A method for iterating the pairs list and finding the longest days a pair has worked.

    * */
    public static void printAnswerPair() {
        String currentFirstEmployee = " ";
        String currentSecondEmployee = " ";
        long currentMostDays = 0;

        String[] arrayElement;

        for (int i = 0; i < pairsList.size(); i++) {
            arrayElement = pairsList.get(i).split(",");
            if (Long.parseLong(arrayElement[2]) > currentMostDays) {
                currentMostDays = Long.parseLong(arrayElement[2]);
                currentFirstEmployee = arrayElement[0];
                currentSecondEmployee = arrayElement[1];
            }
        }

        System.out.println(currentFirstEmployee + "," + currentSecondEmployee);

    }

    /*  A method for printing the elements in the pairs list.
     * */
   public static void printPairsArray () {
        for (int i = 0; i < pairsList.size(); i++) {
            System.out.println(pairsList.get(i) + "\n");
        }
    }

    /*  The Main Method, which calls all other methods.
     * */
    public static void main(String[] args) throws FileNotFoundException {
        getFileData();
        sortObjectList();
        loadPairsData();
        printAnswerPair();
    }
}
