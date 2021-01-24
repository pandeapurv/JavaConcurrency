package com.apande.threadexamples.synchronizers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class SemaphoreExample {

	private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
	
	private final Semaphore semaphore = new Semaphore(NUMBER_OF_CORES,true); 

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		System.out.println("NUMBER_OF_CORES: " + NUMBER_OF_CORES);
		SemaphoreExample obj = new SemaphoreExample();
		obj.start();
	}

	private void start() throws InterruptedException {
		int noOfWorker = NUMBER_OF_CORES + 2;
		List<WorkerThread> list = new ArrayList<>(noOfWorker);
		for (int i = 0; i < noOfWorker; i++) {
			list.add(new WorkerThread());
		}
		for (WorkerThread workerThread : list) {
			workerThread.start();
		}

		for (WorkerThread workerThread : list) {
			workerThread.join();
		}
	}

	class WorkerThread extends Thread {
		@Override
		public void run() {
			try {
			semaphore.acquire();
			} catch (InterruptedException e) {
				return;
			}
			try {
				System.out.println("Doing useful work in the thread " + Thread.currentThread().getId());
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}	
				System.out.println("Work is done in the thread " + Thread.currentThread().getId());
			}finally {
				semaphore.release();
			}
			
			
		}
	}

}
