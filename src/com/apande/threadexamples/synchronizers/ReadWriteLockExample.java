package com.apande.threadexamples.synchronizers;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockExample {

	private int counter = 0;
	private ReadWriteLock lock = new ReentrantReadWriteLock(true);

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		ReadWriteLockExample obj = new ReadWriteLockExample();
		obj.start();

	}

	public void start() throws InterruptedException {
		Thread writerThread1 = new WriterThread();
		Thread readerThread1 = new ReaderThread();
		
		Thread writerThread2 = new WriterThread();
		Thread readerThread2 = new ReaderThread();

		writerThread1.start();
		readerThread1.start();
		
		writerThread2.start();
		readerThread2.start();

		Thread.sleep(4000);

		writerThread1.interrupt();
		readerThread1.interrupt();
		
		writerThread2.interrupt();
		readerThread2.interrupt();

		writerThread1.join();
		readerThread1.join();
		
		writerThread2.join();
		readerThread2.join();
	}

	class ReaderThread extends Thread {
		@Override
		public void run() {
			while (true) {
				lock.readLock().lock();
				try {

					System.out.println("The read lock is acquired");

                    System.out.println("The counter is: " + counter);
                    try {
    					Thread.sleep(500);
    				} catch (InterruptedException e) {
    					Thread.currentThread().interrupt();
    				}
                    
                    System.out.println("The read lock is released");
				} finally {
					lock.readLock().unlock();

				}

				
				if (Thread.currentThread().isInterrupted())
					return;
			}
		}
	}

	class WriterThread extends Thread {
		@Override
		public void run() {
			while (true) {
				
				lock.writeLock().lock();
				try {
					System.out.println("The write lock is acquired");
					counter++;
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
					System.out.println("The write lock is released");
				} finally {
					lock.writeLock().unlock();
				}

				
				if (Thread.currentThread().isInterrupted())
					return;
			}
		}
	}

}
