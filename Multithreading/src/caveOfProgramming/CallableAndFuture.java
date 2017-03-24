package caveOfProgramming;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.*;

/**
 * Created by binlix26 on 13/03/17.
 */
public class CallableAndFuture {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();

        // this is when you want to use the method in Future without actually returning a value
        /*Future<?> future = executor.submit(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                return null;
            }
        });*/

        Future<Integer> future = executor.submit(new Callable<Integer>(){ // generic is the type will be returned by the call() method

            @Override
            public Integer call() throws Exception {
                Random random = new Random();
                int duration = random.nextInt(4000);

                if (duration > 2000)
                    throw new IOException("Sleep for too long!");

                System.out.println("Startgin...");

                try {
                    Thread.sleep(duration);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("Finished...");
                return duration;
            }
        });

        executor.shutdown();

        try {
            System.out.println(future.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            //e.printStackTrace();
//            System.out.println(e.getMessage());

            IOException ex = (IOException) e.getCause();
            System.out.println(ex.getMessage());
        }
    }
}
