package com.apande.threadexamples.waitnotify;

import java.util.LinkedList;
import java.util.Queue;

public class ProducerConsumer {

	private Queue<WorkItem> queue = new LinkedList<>();

	private final int maxQueueSize = 100;
	private Object lock = new Object();

	public static void main(String[] args) {
		ProducerConsumer obj = new ProducerConsumer();
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
			Thread.sleep(400);
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
				synchronized (lock) {

					if (queue.size() < maxQueueSize) {
						WorkItem workItem = new WorkItem(String.valueOf(name) +"-" + Thread.currentThread().getId());
						queue.add(workItem);
						lock.notifyAll();
						System.out.println("WorkItem:" + name + " is added by thread " + Thread.currentThread().getId());
						name++;
					} else {
						System.out.println("Queue is full");
						try {
							lock.wait();
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						}

					}
					if (Thread.currentThread().isInterrupted())
						return;
				}
			}

		}
	}

	class Consumer extends Thread {
		@Override
		public void run() {
			while (true) {
				WorkItem workItem;
				synchronized (lock) {
					while (true) {
						workItem = queue.poll();
						lock.notifyAll();
						if (workItem == null) {
							try {
								lock.wait();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								Thread.currentThread().interrupt();
								return;
							}
						} else {
							break;
						}
					}

				}

				try {
					workItem.process();
				} catch (InterruptedException e) {
					return;
				}

			}
		}
	}

}
