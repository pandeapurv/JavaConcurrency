package com.apande.threadexamples.volatilevariables;

public class Main {

	// provides HappensBefore between one write and read operation. if we want
	// happensbefore for multiple writes then use atomic variables
	private volatile int count = 0;
	private volatile boolean isStopped = false;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Main main = new Main();
		Thread writerThread = new Thread(() -> {
			while (!main.isStopped) {
				main.count += 1;
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block

				}
			}

		});

		Thread readerThread = new Thread(() -> {
			while (!main.isStopped) {
				System.out.println("Current value:" + main.count);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block

				}
			}

		});
		writerThread.start();
		readerThread.start();

		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {

		}

		main.isStopped = true;

		try {
			writerThread.join();
			readerThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
