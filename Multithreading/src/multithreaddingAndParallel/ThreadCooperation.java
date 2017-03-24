package multithreaddingAndParallel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by binlix26 on 18/03/17.
 */
public class ThreadCooperation {
    private static Account account = new Account();

    public static void main(String[] args) {
        System.out.println("Thread 1\t\tThread 2\t\tBalance");
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(new DepositTask());
        executor.execute(new WithdrawTask());

        executor.shutdown();

//        while(!executor.isTerminated()){}

    }

    public static class DepositTask implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                    account.deposit((int) (Math.random() * 10 + 1));
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class WithdrawTask implements Runnable {

        @Override
        public void run() {
            while (true) {
                account.withdraw((int) (Math.random() * 10 + 1));
            }
        }
    }

    private static class Account {
        private int balance = 0;

        private static Lock lock = new ReentrantLock();
        private static Condition newDeposit = lock.newCondition();

        public int getBalance() {
            return this.balance;
        }

        public void deposit(int amount) {
            lock.lock();

            try {
                balance += amount;
                System.out.println("Deposit " + amount + "\t\t\t\t\t\t\t" + getBalance());

                // signal all the threads waiting on the condition
                newDeposit.signalAll();
            } finally {
                lock.unlock();
            }
        }

        public void withdraw(int amount) {
            lock.lock();

            try {
                while (balance < amount) {
                    System.out.println("\t\t\t\twait for a deposit");
                    newDeposit.await();
                }

                balance -= amount;
                System.out.println("\t\t\t\tWithdraw " + amount + "\t\t\t" + getBalance());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }
}

