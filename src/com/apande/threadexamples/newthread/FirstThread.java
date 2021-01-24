package com.apande.threadexamples.newthread;

public class FirstThread {

	private static int salesByDay[] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		FirstThread sales = new FirstThread();

		/*Thread thread = new Thread(new Runnable() {

			public void run() {
				sales.calculateTotal(0, salesByDay.length - 1);
			}
		});*/

		Thread thread = new Thread(new Runnable() {

			public void run() {
				sales.calculateTotal(0, salesByDay.length - 1);
			}
		}, "MyThread");

		thread.start();
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
	}

}
