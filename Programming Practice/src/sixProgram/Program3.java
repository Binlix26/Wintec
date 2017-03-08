package sixProgram;

/**
 * Created by binlix26 on 8/03/17.
 */
public class Program3 {
    private int count = 0;

    public static void main(String[] args) {
        Program3 app = new Program3();
        app.doWork();
    }

    // synchronized is the key word to make this program consistent
    public synchronized void increment() {
        count++;
    }

    public void doWork() {
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    increment();
                }
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    increment();
                }
            }
        });

        t1.start();
        t2.start();

        // let the main threading waiting for t1 and t2 to finish
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(count);
    }
}
