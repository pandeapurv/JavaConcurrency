package com.apande.threadexamples.collectionsexamples;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BlockingQueueExample {

	private final int queueSize = 100;
	private BlockingQueue<WorkItem> queue = new LinkedBlockingQueue<>(queueSize);

	// private Object lock = new Object();

	public static void main(String[] args) {
		BlockingQueueExample obj = new BlockingQueueExample();
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
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			e.printStackTrace();
		}

	}

	class WorkItem {
		private String name;

		public WorkItem(String name) {
			this.name = name;
		}

		void process() throws InterruptedException {
			System.out.println("Starting to process a work item " + name);

			Thread.sleep(10);

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

				// if (queue.size() < maxQueueSize) {
				WorkItem workItem = new WorkItem(String.valueOf(name) + "-" + Thread.currentThread().getId());
				// queue.add(workItem);
				try {
					queue.put(workItem);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
				// lock.notifyAll();
				System.out.println("WorkItem:" + name + " is added by thread " + Thread.currentThread().getId());
				name++;
				/*
				 * } else { System.out.println("Queue is full"); try { lock.wait(); } catch
				 * (InterruptedException e) { Thread.currentThread().interrupt(); }
				 * 
				 * }
				 */
				if (Thread.currentThread().isInterrupted())
					return;
			}
		}

	}

	class Consumer extends Thread {
		@Override
		public void run() {
			WorkItem workItem;
			while (true) {

				// synchronized (lock) {
				// while (true) {
				//workItem = queue.poll();
				// lock.notifyAll();
				/*
				 * if (workItem == null) { try { lock.wait(); } catch (InterruptedException e) {
				 * // TODO Auto-generated catch block Thread.currentThread().interrupt();
				 * return; } } else { break; }
				 */
				try {
					workItem = queue.take();
				} catch (InterruptedException e1) {
					return;
				}
				
				
				try {
					workItem.process();
				} catch (InterruptedException e) {
					return;
				}
			}

			// }

		}
	}
}
