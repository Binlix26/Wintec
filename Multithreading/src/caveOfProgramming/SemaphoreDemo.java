package caveOfProgramming;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created by binlix26 on 13/03/17.
 *
 * Here I'm using it to limit how many threads run the contents of a method,
 * but in general the idea of a semaphore is that you specify a number of "permits",
 * a thread can get a permit by calling acquire, but if all the permits have been handed out (with acquire),
 * it will make the thread wait until a permit becomes free
 * because another thread calls "release" to hand the permit back.
 *
 */
public class SemaphoreDemo {

    public static void main(String[] args) throws InterruptedException {

        ExecutorService executor = Executors.newCachedThreadPool();

        for (int i = 0; i < 200; i++) {
            executor.submit(() -> {
                Connection.getInstance().connect();
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.DAYS);
    }
}

class Connection {
    private static Connection instance = new Connection();
    private int connections = 0;
    private Semaphore sem = new Semaphore(10, true);

    private Connection() {
    }

    public static Connection getInstance() {
        return instance;
    }

    public void connect() {
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            doConnect();
        } finally {
            sem.release();
        }
    }

    public void doConnect() {
        synchronized (this) {
            connections++;
            System.out.println("Current Connections: " + connections);
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // what does this actually do?
        synchronized (this) {
            connections--;
        }
    }
}
