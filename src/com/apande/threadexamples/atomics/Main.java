package com.apande.threadexamples.atomics;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Main {

	private AtomicInteger count = new AtomicInteger();
	private volatile AtomicReference<Set<Integer>> cache = new AtomicReference(new HashSet<>());
	private volatile boolean isStopped = false;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Main main = new Main();
		Thread writerThread1 = new Thread(() -> {
			while (!main.isStopped) {
				int newValue = main.count.incrementAndGet();

				while (true) {
					Set<Integer> currentCache = main.cache.get();
					Set<Integer> tmpCache = new HashSet<>(currentCache);
					tmpCache.add(newValue);
					if (main.cache.compareAndSet(currentCache, tmpCache))
						break;
				}

				System.out.println("The new value of the shared variable is: " + newValue + " thread id is: "
						+ Thread.currentThread().getId());

				Set<Integer> tempCache = main.cache.get();
				System.out.println("The content of the cache is: " + tempCache + " thread id is: "
						+ Thread.currentThread().getId());

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block

				}
			}

		});

		Thread writerThread2 = new Thread(() -> {
			while (!main.isStopped) {
				int newValue = main.count.incrementAndGet();

				while (true) {
					Set<Integer> currentCache = main.cache.get();
					Set<Integer> tmpCache = new HashSet<>(currentCache);
					tmpCache.add(newValue);
					if (main.cache.compareAndSet(currentCache, tmpCache))
						break;
				}

				System.out.println("The new value of the shared variable is: " + newValue + " thread id is: "
						+ Thread.currentThread().getId());

				Set<Integer> tempCache = main.cache.get();
				System.out.println("The content of the cache is: " + tempCache + " thread id is: "
						+ Thread.currentThread().getId());
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block

				}
			}

		});

		Thread readerThread = new Thread(() -> {
			while (!main.isStopped) {
				System.out.println("Current value:" + main.count.get());
				Set<Integer> tempCache = main.cache.get();
				System.out.println("The content of the cache is: " + tempCache + " reader thread id is: "
						+ Thread.currentThread().getId());
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block

				}
			}

		});
		writerThread1.start();
		writerThread2.start();
		readerThread.start();

		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {

		}

		main.isStopped = true;

		try {
			writerThread1.join();
			writerThread2.join();
			readerThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
