package multithreaddingAndParallel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by binlix26 on 17/03/17.
 */
public class AcountWithoutSync {
    private static Account account = new Account();

    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();

        for (int i = 0; i < 100; i++) {
            executor.execute(new AddAPenyTask());
        }

        executor.shutdown();

        while(!executor.isTerminated()){}

        System.out.println("What is balance? "+ account.getBalance());
    }

    private static class AddAPenyTask implements Runnable {

        @Override
        public void run() {
            account.deposit(1);
        }
    }

    private static class Account {
        private int balance = 0;

        public int getBalance() {
            return balance;
        }

        public void deposit(int num) {
            balance += num;
        }
    }
}
