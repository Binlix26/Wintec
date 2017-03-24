package caveOfProgramming;

import java.util.Random;
import java.util.concurrent.*;

/**
 * Created by binlix26 on 13/03/17.
 */
public class Interrupted {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Start...");

        ExecutorService executor = Executors.newCachedThreadPool();
        Future<?> future = executor.submit(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                Random random = new Random();

                for (int i = 0; i < 1E8; i++) {
                    if (Thread.currentThread().isInterrupted()) { // this flag can be affected by three methods below
                        System.out.println("Interrupted!");
                        break;
                    }

                    Math.sin(random.nextDouble());
                }


                return null;
            }
        });

        executor.shutdown();
        Thread.sleep(500);
        //future.cancel(true); // first method to make the 'flag' true
        executor.shutdownNow(); // second

        executor.awaitTermination(1, TimeUnit.DAYS);

        System.out.println("Finished...");
        //t.interrupt(); // third, Thread.currentThread().isInterrupted() change to 'true' by this
    }
}
