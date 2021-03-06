package com.apande.threadexamples.synchronization;

import java.time.LocalDateTime;

public class Sales {

	private static int salesByDay[] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
	private long totalSales = 0;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long startTime = System.currentTimeMillis();
		Sales sales = new Sales();
		int startDay = 0;
		int endDay = salesByDay.length - 1;
		int daysPerThread = (int) Math.ceil((endDay - startDay) / 2.0);

		Thread calcThread1 = new CalculationThread(startDay, startDay + daysPerThread, sales, "calcThread1");
		Thread calcThread2 = new CalculationThread(startDay + daysPerThread, endDay, sales, "calcThread2");

		Thread thread2 = new Thread(new Runnable() {
			public void run() {
				sales.createBackup();
			}

		}, "CreateBackupThread");

		calcThread1.start();
		calcThread2.start();
		//thread2.start();

		try {
			calcThread1.join();
			calcThread2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//thread2.interrupt();
//		try {
//			thread2.join();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		long totalTime = System.currentTimeMillis() - startTime;
		System.out.println("Total sales are: " + sales.totalSales
                + ", execution of the program took: " + totalTime + " ms.");
	}

	private void createBackup() {

		// Thread.interrupted();// get the status and clears it
		// Thread.currentThread().isInterrupted();// only get the status
		while (!Thread.currentThread().isInterrupted()) {

			// System.out.println("Writing to file");

			// using this we exit gracefully
			if (Thread.currentThread().isInterrupted()) {
				System.out.println("CreateBackupThread was interrupted in if block");
				return;
			}

		}

	}

	// lock is obtained on object. so even if we call two different synchronized methods by two thread they will run sequentially 
	synchronized private void addPartialSales(long partialSales) {
		/*long currentSales = totalSales;
		long newTotalSales = currentSales + partialSales;
		totalSales = newTotalSales; here data race will happen*/
		
		// this.monitor.lock
        System.out.println("A monitor of the object " + this + " is locked by the thread "
                + Thread.currentThread().getName());

        totalSales += partialSales;
        printCurrentDate(); // rentrant lock

        System.out.println("A monitor of the object " + this + " is unlocked by the thread "
                + Thread.currentThread().getName());
        // this.monitor.unlock
	}
	
	synchronized private void printCurrentDate() {
		 System.out.println("A monitor of the object " + this + " is locked by the thread "
	                + Thread.currentThread().getName());

	        System.out.println(LocalDateTime.now());

	        System.out.println("A monitor of the object " + this + " is unlocked by the thread "
	                + Thread.currentThread().getName());
	}

	 private void calculateTotal(int startDay, int endDay) {
		int salesForPeriod = 0;
		for (int i = startDay; i < endDay; i++) {
			salesForPeriod += salesByDay[i];
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				System.out.println("Thread was interrupted");
			}
		}

		addPartialSales(salesForPeriod);
		
		
		Thread currentThread = Thread.currentThread();
		String threadName = currentThread.getName();
		long threadId = currentThread.getId();

		System.out.println("Partial sales are: " + salesForPeriod + ", start day is " + startDay + ", end day is "
				+ endDay + ", thread id is " + threadId + ", thread name is " + threadName);

	}

	static class CalculationThread extends Thread {

		private int startDay;
		private int endDay;
		private Sales sales;

		public CalculationThread(int startDay, int endDay, Sales sales, String threadName) {
			super(threadName);
			this.startDay = startDay;
			this.endDay = endDay;
			this.sales = sales;
		}

		@Override
		public void run() {
			sales.calculateTotal(startDay, endDay);
		}
	}

}
