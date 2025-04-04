import java.util.Arrays;
public class Slargest
{
    static int getSecondLargest(int[] arr) {
        int n = arr.length;

        int largest = Integer.MIN_VALUE;
        int Secondlargest = Integer.MIN_VALUE;

        for (int i = 0; i < n; i++) {
            if (arr[i] > largest) {
                Secondlargest = largest;
                largest = arr[i];
            } else if (arr[i] < largest && arr[i] > Secondlargest) {
                Secondlargest = arr[i];
            }
        }

        return Secondlargest;
    }

    public static void main(String[] args) {
        int [] arr={20,65,45,85,36,41,75};
        int result = getSecondLargest(arr); // store the result
        System.out.println("Second largest element is: " + result); // print it
    }
}
