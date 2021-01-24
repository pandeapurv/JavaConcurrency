package com.apande.threadexamples.synchronizers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CountDownLatchAndMergeSort {

	private static final int BATCH_SIZE = 5;
	private static final int NO_OF_THREADS = 4;

	private final List<Thread> workingThreads = new ArrayList<>(NO_OF_THREADS);

	private final int QUEUE_SIZE = 100;
	private final Lock sortingqueueLock = new ReentrantLock();
	private final Condition sortingqueueCondidition = sortingqueueLock.newCondition();

	private final Queue<SortingTask> sortingQueue = new LinkedList<>();

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub

		CountDownLatchAndMergeSort countDownLatchAndMergeSort = new CountDownLatchAndMergeSort();

		for (int i = 0; i < 10; i++) {
			int arraySize = 10;
			List<Integer> arr = new ArrayList<>();

			for (i = 0; i < arraySize; i++) {
				arr.add((int) (100.0 * Math.random()));
			}
			
			

			MergeTask mergeTask = countDownLatchAndMergeSort.sort(arr);
			List<Integer> sortedArray = mergeTask.merge();
			if (checkSortedArray(arr, sortedArray))
                System.out.println("Array is successfully sorted");
            else
                System.out.println("Array is not sorted...");
			
			printArray(arr);
			printArray(sortedArray);
			
		}
	}
	
	private static void printArray(List<Integer> sortedArray) {
		System.out.println("");
		for(int i : sortedArray) {
			System.out.print(i + " ");
		}
	}

	private static boolean checkSortedArray(List<Integer> baseArray, List<Integer> sortedArray) {
		if (baseArray.size() != sortedArray.size()) {
			System.out.println("Array sizes are not equal, baseArray size: " + baseArray.size()
					+ ",  sortedArray size: " + sortedArray.size());
			return false;
		}

		for (int i = 1; i < sortedArray.size(); i++) {
			if (sortedArray.get(i) < sortedArray.get(i - 1)) {
				return false;
			}
		}
		return true;
	}

	public CountDownLatchAndMergeSort() {
		for (int i = 0; i < NO_OF_THREADS; i++) {
			SortTask workerThread = new SortTask();
			workingThreads.add(workerThread);
			workerThread.setDaemon(true);
			workerThread.start();
		}
	}

	private MergeTask sort(List<Integer> arr) throws InterruptedException {
		List<List<Integer>> splittedArray = splitArray(arr, BATCH_SIZE);
		CountDownLatch latch = new CountDownLatch(splittedArray.size());
		MergeTask mergeTask = new MergeTask(latch, splittedArray);
		sortingqueueLock.lock();
		try {
			for (List<Integer> partialArray : splittedArray) {
				while (sortingQueue.size() >= QUEUE_SIZE) {
					sortingqueueCondidition.await();
				}
				sortingQueue.add(new SortingTask(latch, partialArray));
				sortingqueueCondidition.signalAll();
			}
		} finally {
			sortingqueueLock.unlock();
		}

		return mergeTask;

	}

	private class SortingTask {
		private final CountDownLatch latch;
		private final List<Integer> array;

		public SortingTask(CountDownLatch latch, List<Integer> array) {
			this.latch = latch;
			this.array = array;
		}

		public void process() {
			Collections.sort(array);
			latch.countDown();
		}
	}

	private static List<List<Integer>> splitArray(List<Integer> array, int bySize) {
		List<List<Integer>> splittedArray = new ArrayList<>(array.size() / bySize);
		List<Integer> partialArrays = new ArrayList<>(bySize);
		for (Integer partialArray : array) {
			partialArrays.add(partialArray);
			if (partialArrays.size() == bySize) {
				splittedArray.add(partialArrays);
				partialArrays = new ArrayList<>(bySize);
			}
		}

		if (partialArrays.size() > 0)
			splittedArray.add(partialArrays);

		return splittedArray;
	}

	private class SortTask extends Thread {
		@Override
		public void run() {
			while (true) {
				SortingTask sortingTask;
				sortingqueueLock.lock();
				try {
					sortingTask = sortingQueue.poll();
					if (sortingTask == null) {
						try {
							sortingqueueCondidition.await();
						} catch (InterruptedException e) {
							interrupt();
						}
					}
				} finally {
					sortingqueueLock.unlock();
				}

				if (isInterrupted())
					return;

				if (sortingTask != null) {
					sortingTask.process();
				}

			}
		}
	}

	private class MergeTask {
		private final CountDownLatch latch;
		private final List<List<Integer>> arrays;

		public MergeTask(CountDownLatch latch, List<List<Integer>> arrays) {
			this.latch = latch;
			this.arrays = arrays;
		}

		public List<Integer> merge() throws InterruptedException {
			latch.await();
			List<Integer> res = new ArrayList<>();
			List<Integer> arraysIndexes = new ArrayList<>(arrays.size());
			for (int i = 0; i < arrays.size(); i++) {
				arraysIndexes.add(0);
			}

			boolean haveValues = true;
			while (haveValues) {
				haveValues = false;
				Integer minValue = null;
				int arrayIndexWithMinValue = 0;
				for (int arrayIndex = 0; arrayIndex < arrays.size(); arrayIndex++) {
					if (arraysIndexes.get(arrayIndex) >= arrays.get(arrayIndex).size()) {
						continue;
					}
					haveValues = true;

					Integer arrayValue = arrays.get(arrayIndex).get(arraysIndexes.get(arrayIndex));
					if (minValue == null || arrayValue <= minValue) {
						arrayIndexWithMinValue = arrayIndex;
						minValue = arrayValue;
					}
				}
				if (haveValues) {
					res.add(minValue);
					arraysIndexes.set(arrayIndexWithMinValue, arraysIndexes.get(arrayIndexWithMinValue) + 1);
				}
			}
			return res;
		}
	}

}
