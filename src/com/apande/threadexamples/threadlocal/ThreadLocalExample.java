package com.apande.threadexamples.threadlocal;

public class ThreadLocalExample {

	private final ThreadLocal<Integer> counter = ThreadLocal.withInitial(() -> 0);

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ThreadLocalExample threadLocalExample = new ThreadLocalExample();
		threadLocalExample.process();
	}
	
	 private void process() {
		Worker worker1 = new Worker();
		Worker worker2 = new Worker();
		
		worker1.start();
		worker2.start();
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			
		}
		
		worker1.interrupt();
		worker2.interrupt();
		
		try {
			worker1.join();
			worker2.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
		
		}
		
	}

	class Worker extends Thread {
		@Override
		public void run() {
			while(true) {
				int currentValue = counter.get();
				System.out.println("Current value is " + currentValue + " in thread " + Thread.currentThread().getId());
				counter.set(currentValue+1);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
				
				if(Thread.currentThread().isInterrupted())
					return;
			}
			
		}
	}

}
