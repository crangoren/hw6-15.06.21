
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ArrayCheckTest {
    private ArrayCheck arrayCheck = new ArrayCheck();


    @Test
    public void testGetPartAfterFour() {
        int[] arrayToTest = {1, 2, 4, 4, 2, 3, 4, 1, 7, 8};
        int[] expected = {1, 7, 8};
        int[] arrayToTest1 = {0, 2, 2, 4, 1, 4, 9};
        int[] expected1 = {9};
        int[] arrayToTest2 = {4, 4, 4, 4, 2, 3, 2, 1, 7, 8};
        int[] expected2 = {2, 3, 2, 1, 7, 8};
        Assertions.assertArrayEquals(expected, arrayCheck.getPartAfterFour(arrayToTest));
        Assertions.assertArrayEquals(expected1, arrayCheck.getPartAfterFour(arrayToTest1));
        Assertions.assertArrayEquals(expected2, arrayCheck.getPartAfterFour(arrayToTest2));
    }

    @Test
    public void testCheckArrayForNums() {
        int[] array1 = {1, 2, 0, 4, 0, 3, 1, 5, 7, 9};
        int[] array2 = {3, 2, 3, 5, 0};
        int[] array3 = {1, 2, 9};
        Assertions.assertEquals(true, arrayCheck.checkArrayForNums(array1));
        Assertions.assertEquals(false, arrayCheck.checkArrayForNums(array2));
        Assertions.assertEquals(true, arrayCheck.checkArrayForNums(array3));
    }
}