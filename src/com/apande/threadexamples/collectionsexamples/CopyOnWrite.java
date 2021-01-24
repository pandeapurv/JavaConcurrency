package com.apande.threadexamples.collectionsexamples;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class CopyOnWrite {
	// each add opertation creates a copy array and put element
	// not good for update operations. use when very few update and lots of read;
	// can use iterator with this. cant use remove method of itr. dont need to use
	// synchronize keyword. is thread safe
	private CopyOnWriteArrayList<Integer> array = new CopyOnWriteArrayList<>();

	public static void main(String[] args) throws InterruptedException {
		CopyOnWrite copyOnWrite = new CopyOnWrite();
		copyOnWrite.start();
	}

	public void start() throws InterruptedException {
		WriterThread writerThread = new WriterThread();

		ReaderThread readerThread = new ReaderThread();

		writerThread.start();
		readerThread.start();

		writerThread.join();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			return;
		}
		readerThread.interrupt();
	}

	class WriterThread extends Thread {
		@Override
		public void run() {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				return;
			}
			for (int i = 0; i < 10000; i++) {
				array.add(i);
			}
			System.out.println("array is filled");
		}
	}

	class ReaderThread extends Thread {
		@Override
		public void run() {
			while (true) {
				int counter = 0;
				Iterator<Integer> itr = array.iterator();

				while (itr.hasNext()) {
					counter++;
					itr.next();
				}

				System.out.println("The size of array is " + counter);
				try {
					Thread.sleep(0, 1000);
				} catch (InterruptedException e) {
					return;
				}

			}

		}
	}

}
