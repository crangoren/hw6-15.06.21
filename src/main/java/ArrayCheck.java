import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ArrayCheck {
//    public static void main(String[] args) {
//
//    }

    public static int[] getPartAfterFour(int[] arr) throws RuntimeException{
        if (arr.length == 0) {
            throw new NullPointerException();
        }

        int lastIndexOfFour = -1;

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == 4) lastIndexOfFour = i + 1;
        }

        if (lastIndexOfFour == -1) {
            throw new RuntimeException();
        }
         else {
            return Arrays.copyOfRange(arr, lastIndexOfFour, arr.length);

         }

    }

    boolean checkArrayForNums(int[] inputArray){
        for (int i : inputArray) {

            if (i == 1 || i == 4) {
                return true;

            }

        }
        return false;

    }



}
