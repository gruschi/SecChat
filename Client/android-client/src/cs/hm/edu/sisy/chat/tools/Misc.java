package cs.hm.edu.sisy.chat.tools;

import java.util.Calendar;

public class Misc {
     
     public static int getCurrentDate() //int like 20141212 for 12.12.2014
     {
       return Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + 
           Calendar.getInstance().get(Calendar.MONTH)*100 + 
           Calendar.getInstance().get(Calendar.YEAR)*100*100;
     }
     
     public static boolean isPinCurrent(int storagedDate, int currentDate)
     {
       return storagedDate == currentDate;
     }
}
