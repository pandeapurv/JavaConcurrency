package com.apande.threadexamples.synchronizers;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockExample {

	private int counter1 = 0;
	private Lock lock1 = new ReentrantLock();

	private int counter2 = 0;
	private Lock lock2 = new ReentrantLock(true);

	public static void main(String[] args) throws InterruptedException {
		ReentrantLockExample obj = new ReentrantLockExample();
		obj.start();
	}

	public void start() throws InterruptedException {
		Thread writerThread = new WriterThread();
		Thread readerThread = new ReaderThread();

		writerThread.start();
		readerThread.start();

		Thread.sleep(4000);

		writerThread.interrupt();
		readerThread.interrupt();

		writerThread.join();
		readerThread.join();
	}

	class ReaderThread extends Thread {
		@Override
		public void run() {
			while (true) {
				lock1.lock();
				try {
					
					System.out.println("Counter value is: " + counter1);
				} finally {

					try {
						lock2.lockInterruptibly();
						System.out.println("The counter1 + counter2 is: " + counter1 + counter2);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					} finally {
						lock1.unlock();
						try {
							if (Thread.currentThread().isInterrupted())
								System.out.println("The counter2 is: " + counter2);
						} finally {
							lock2.unlock();
						}
					}

				}

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
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
				lock1.lock();
				try {
					
					counter1++;
				} finally {
					lock1.unlock();
				}

				try {
					if (lock2.tryLock(2, TimeUnit.MILLISECONDS)) {
						try {							
							counter2++;
						} finally {
							lock2.unlock();
						}
					} else {
						System.out.println("The writer is unable to to get the lock2");
					}
				} catch (InterruptedException e1) {
					Thread.currentThread().interrupt();
				}

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
				if (Thread.currentThread().isInterrupted())
					return;
			}
		}
	}

}
