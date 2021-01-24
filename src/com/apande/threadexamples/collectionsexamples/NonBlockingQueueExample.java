package com.apande.threadexamples.collectionsexamples;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NonBlockingQueueExample {

	// private Queue<WorkItem> queue = new LinkedList<>();
	// size method will not same result to all waiting thread which can cause queue
	// to have more items than specified.
	// Very high thruput.
	// no need to use synchronized and lock
	// also there is not wait notify so we have to test and adjust sleep method when
	// queue is full or empty
	private Queue<WorkItem> queue = new ConcurrentLinkedQueue();

	private final int maxQueueSize = 100;
	private Object lock = new Object();

	public static void main(String[] args) {
		NonBlockingQueueExample obj = new NonBlockingQueueExample();
		obj.process();
	}

	public void process() {
		Thread producer1 = new Producer();
		Thread producer2 = new Producer();
		Thread consumer1 = new Consumer();
		Thread consumer2 = new Consumer();
		Thread consumer3 = new Consumer();
		Thread consumer4 = new Consumer();
		producer1.start();
		producer2.start();
		consumer1.start();
		consumer2.start();
		consumer3.start();
		consumer4.start();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

		producer1.interrupt();
		producer2.interrupt();
		consumer1.interrupt();
		consumer2.interrupt();
		consumer3.interrupt();
		consumer4.interrupt();

		try {
			producer1.join();
			producer2.join();
			consumer1.join();
			consumer2.join();
			consumer3.join();
			consumer4.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

	}

	class WorkItem {
		private String name;

		public WorkItem(String name) {
			this.name = name;
		}

		void process() throws InterruptedException {
			System.out.println("Starting to process a work item " + name);

			// Thread.sleep(10);

			System.out
					.println("The work item " + name + " is processed in the thread " + Thread.currentThread().getId());
		}
	}

	class Producer extends Thread {

		@Override
		public void run() {
			int name = 0;
			while (true) {
				// synchronized (lock) {

				if (queue.size() < maxQueueSize) {
					WorkItem workItem = new WorkItem(String.valueOf(name) + "-" + Thread.currentThread().getId());
					queue.add(workItem);
					// lock.notifyAll();
					System.out.println("WorkItem:" + name + " is added by thread " + Thread.currentThread().getId());
					name++;
				} else {
					System.out.println("Queue is full");
					try {
						// lock.wait();
						Thread.sleep(0, 10);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}

				}
				if (Thread.currentThread().isInterrupted())
					return;
				// }
			}

		}
	}

	class Consumer extends Thread {
		@Override
		public void run() {
			while (true) {
				WorkItem workItem;
				// synchronized (lock) {
				while (true) {
					workItem = queue.poll();
					// lock.notifyAll();
					if (workItem == null) {
						try {
							// lock.wait();
							Thread.sleep(0, 10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							Thread.currentThread().interrupt();
							return;
						}
					} else {
						break;
					}
					// }

				}

				try {
					workItem.process();
				} catch (InterruptedException e) {
					return;
				}

				if (Thread.currentThread().isInterrupted())
					return;

			}
		}
	}
}
