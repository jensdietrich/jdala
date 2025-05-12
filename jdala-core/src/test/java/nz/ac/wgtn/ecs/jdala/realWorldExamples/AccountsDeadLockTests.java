package nz.ac.wgtn.ecs.jdala.realWorldExamples;

import nz.ac.wgtn.ecs.jdala.StaticAgentTests;
import org.junit.jupiter.api.Test;
import util.UtilMethods;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AccountsDeadLockTests extends StaticAgentTests {

    @Test
    public void accountsDeadLock() {
        Account account1 = new Account(100);
        Account account2 = new Account(200);

        BlockingQueue<Runnable> transactionQueue = new ArrayBlockingQueue<>(10);
        transactionQueue.add(()-> transfer(account1, account2, 50)); // Account 1: 50, Account 2: 250
        transactionQueue.add(()-> transfer(account2, account1, 80)); // Account 1: 130, Account 2: 170
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 2, 10, TimeUnit.SECONDS, transactionQueue);
        threadPoolExecutor.prestartAllCoreThreads();

        UtilMethods.tryToSleep(10000);

        System.out.println(account1.getBalance() + " " + account2.getBalance());
        System.out.println("Finished waiting. Active threads: " + threadPoolExecutor.getActiveCount());
        threadPoolExecutor.shutdownNow(); // Cleanup
    }

    private void transfer(Account fromAccount, Account toAccount, double amount) {
        synchronized (fromAccount) {
            fromAccount.withdraw(amount);
            UtilMethods.tryToSleep(1000);
            synchronized (toAccount) {
                toAccount.deposit(amount);
            }
        }
    }

    static class Account{
        private double balance = 0;
        public Account(double balance) {this.balance = balance;}
        public void deposit(double amount){balance += amount;}
        public void withdraw(double amount){balance -= amount;}
        public double getBalance(){return balance;}
    }
}
