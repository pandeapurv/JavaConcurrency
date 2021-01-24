package com.apande.threadexamples.interrupt;

public class Sales {

	private static int salesByDay[] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Sales sales = new Sales();

		/*
		 * Thread thread = new Thread(new Runnable() {
		 * 
		 * public void run() { sales.calculateTotal(0, salesByDay.length - 1); } });
		 */

		Thread thread = new Thread(new Runnable() {

			public void run() {
				sales.calculateTotal(0, salesByDay.length - 1);
			}
		}, "MyThread");

		Thread thread2 = new Thread(new Runnable() {

			public void run() {
				sales.createBackup();
			}

		}, "CreateBackupThread");

		thread.start();
		// thread.interrupt();

		thread2.start();
		System.out.println("This can print before MyThread is completed");
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		thread2.interrupt();
		try {
			thread2.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("This will not print till MyThread is completed");
	}

	private void createBackup() {

		// Thread.interrupted();// get the status and clears it
		// Thread.currentThread().isInterrupted();// only get the status
		while (!Thread.currentThread().isInterrupted()) {

			System.out.println("Writing to file");

			// using this we exit gracefully
			if (Thread.currentThread().isInterrupted()) {
				System.out.println("CreateBackupThread was interrupted in if block");
				return;
			}

		}

	}

	private void calculateTotal(int startDay, int endDay) {
		int salesForPeriod = 0;
		for (int i = startDay; i < endDay; i++) {
			salesForPeriod += salesByDay[i];
		}

		Thread currentThread = Thread.currentThread();
		String threadName = currentThread.getName();
		long threadId = currentThread.getId();

		System.out.println("Total sales are: " + salesForPeriod + ", start day is " + startDay + ", end day is "
				+ endDay + ", thread id is " + threadId + ", thread name is " + threadName);

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			System.out.println("Thread was interrupted");
		}

	}

}
