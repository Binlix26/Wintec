package multithreaddingAndParallel;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by binlix26 on 18/03/17.
 */
public class ConsumerProducer {
    private static Buffer buffer = new Buffer();

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(new ProducerTask());
        executor.execute(new ConsumerTask());

        executor.shutdown();
    }

    private static class ProducerTask implements Runnable {

        @Override
        public void run() {
            try {
                int i = 1;
                while (true) {
                    System.out.println("Producer writes " + i);
                    buffer.write(i++);
                    Thread.sleep((int) (Math.random() * 10000));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class ConsumerTask implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                    System.out.println("\t\tConsumer reads " + buffer.read());
                    Thread.sleep((int) (Math.random() * 10000));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class Buffer {
        private final static int CAPACITY = 1;
        private LinkedList<Integer> queue = new LinkedList<>();
        private static Lock lock = new ReentrantLock();
        private static Condition notEmpty = lock.newCondition();
        private static Condition notFull = lock.newCondition();

        public void write(int num) {
            lock.lock();

            try {
                while (queue.size() == CAPACITY) {
                    System.out.println("Wait for notFull condition");
                    notFull.await();
                }

                queue.add(num);
                notEmpty.signalAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

        public int read() {
            int value = 0;
            lock.lock();

            try {
                while (queue.size() == 0) {
                    System.out.println("\t\tWait for notEmpty Condition");
                    notEmpty.await();
                }
                value = queue.remove();
                notFull.signalAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
                return value;
            }
        }
    }
}
