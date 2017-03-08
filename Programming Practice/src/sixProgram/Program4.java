package sixProgram;

import java.util.Random;

/**
 * Created by binlix26 on 8/03/17.
 */
public class Program4 {
    public static void main(String[] args) {
        int[] arr = getArray(1000000);

        /*QuickSort quick = new QuickSort();
        quick.run(arr);*/
//        print(arr);

//        print(arr);

        BubbleSort bubble = new BubbleSort();
        arr = bubble.run(arr);
        print(arr);


    }

    public static int[] getArray(int size) {
        Random random = new Random();

        int[] arr = new int[size];

        for (int i = 0; i < arr.length; i++) {
            arr[i] = random.nextInt(size);
        }

        return arr;
    }

    public static void print(int[] arr) {
        for (int num : arr
                ) {
            System.out.print(num + ", ");
        }

        System.out.println();
    }
}

class BubbleSort {

    public int[] run(int[] arr) {
        long start = System.currentTimeMillis();
        bubbleSort(arr);
        long end = System.currentTimeMillis();
        System.out.println("The execution time for Bubble Sort is: " + (end - start));

        return arr;
    }

    public void bubbleSort(int[] arr) {
        // condition to stop
        Boolean needToPass = true;
        for (int i = 1; i < arr.length && needToPass; i++) {
            // assume the array is already sorted
            needToPass = false;
            for (int j = 0; j < arr.length - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    // swap happened, need to be going on
                    needToPass = true;
                }
            }
        }
    }
}

class QuickSort {
    public int[] run(int[] arr) {
        long start = System.currentTimeMillis();
        quickSort(arr);
        long end = System.currentTimeMillis();
        System.out.println("The execution time for Quick Sort is: " + (end - start));

        return arr;
    }

    private void quickSort(int[] list) {
        quickSort(list, 0, list.length - 1);
    }

    private void quickSort(int[] list, int first, int last) {
        if (last > first) {
            int pivotIndex = partition(list, first, last);
            quickSort(list, first, pivotIndex - 1);
            quickSort(list, pivotIndex + 1, last);
        }
    }

    private int partition(int[] list, int first, int last) {
        int pivot = list[first]; // Choose the first element as the pivot
        int low = first + 1;// Index for forward search
        int high = last;// Index for backward search

        while (high > low) {
            //search forward from left
            while (low <= high && list[low] <= pivot)
                low++;

            //search backward from right
            while (low <= high && list[high] > pivot)
                high--;

            //Swap two elements in the list
            if (high > low) {
                int temp = list[high];
                list[high] = list[low];
                list[low] = temp;
            }
        }

        while (high > first && list[high] >= pivot)
            high--;

        //Swap pivot with list[high]
        if (pivot > list[high]) {
            list[first] = list[high];
            list[high] = pivot;
            return high;
        } else {
            return first;
        }
    }
}
