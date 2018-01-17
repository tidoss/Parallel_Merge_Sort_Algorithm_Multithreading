import java.lang.reflect.Array;
import java.util.*;
import java.util.Arrays;

public class ParallelMergeSort<T> {

    static Class<?> component_type;

    public static void main(String args[]) {
	System.out.println("\n\nParallel sorting:");
	System.out.println("-----------------");

	List<Integer> arr = new ArrayList<>(Arrays.asList(0,4,3,-3,1,-2,2,-4,-1));
	component_type = Integer.class;

	Thread main_thread = new Thread(new Runnable() {
		public void run() {
		    ParallelMergeSort.merge(arr, arr.size());
		}
	    });
	main_thread.start();

	// Wait for main thread to finish
	try {
	    main_thread.join();
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
	System.out.println("\n\n-- END --");
	display(arr);
    }

    public static <T extends Comparable<? super T>> void merge(List<T> v, int size) {
	// Show info
	synchronized (System.out) {
	    System.out.println("\n--- THREAD ID : " + Thread.currentThread().getId() + " -- (#" + Thread.activeCount() + " total) ---");
	    System.out.println("Treating : ");
	    display(v);
	}
	if (v.size() > 1) {
	    final int half_point = size / 2;
	    List<T> left = v.subList(0, half_point);
	    List<T> right = v.subList(half_point, size);

	    // Create two new threads for each side
	    Thread left_thread = new Thread(new Runnable() {
		    public void run() {
			ParallelMergeSort.merge(left, half_point);
		    }
		});
	    left_thread.start();

	    Thread right_thread = new Thread(new Runnable() {
		    public void run() {
			ParallelMergeSort.merge(right, size - half_point);
		    }
		});
	    right_thread.start();

	    // Wait for them to finish
	    try {
		right_thread.join();
		left_thread.join();
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }


	    // This is the auxiliary space needed for mergesort : O(n).
	    T[] working_list = (T[]) Array.newInstance(component_type, half_point);

	    int i = 0, j = 0, k = 0;

	    for (; i < half_point; ++i) working_list[i] = v.get(i);

            while (j < half_point && i < size)
		v.set(k++, v.get(i).compareTo(working_list[j]) < 0 ? v.get(i++) : working_list[j++]);

	    while (j < half_point)
		v.set(k++, working_list[j++]);
        }
    }

    public static <T extends Comparable<? super T>> void display(List<T> arr) {
    	System.out.print("[ ");
	for (T number : arr)
	    System.out.print(number + " ");
    	System.out.println("]");
    }
}