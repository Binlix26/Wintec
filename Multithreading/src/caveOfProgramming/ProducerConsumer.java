package caveOfProgramming;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by binlix26 on 9/03/17.
 */
public class ProducerConsumer {
    private static BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    producer();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    consumer();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();
    }

    public static void producer() throws InterruptedException {
        Random random = new Random();

        while(true) {
            queue.put(random.nextInt(100));
        }
    }

    public static void consumer() throws InterruptedException {
        Random random = new Random();

        while(true) {
            //Thread.sleep(100);

            if (random.nextInt(10) == 0) {
                int value = queue.take();
                System.out.println("Taken value: "+value + "; ArrayBlockingQueue size: "+queue.size());
            }
        }
    }
}
