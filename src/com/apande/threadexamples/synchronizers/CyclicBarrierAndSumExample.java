package com.apande.threadexamples.synchronizers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;


public class CyclicBarrierAndSumExample {

	private static final int NO_OF_THREADS = Runtime.getRuntime().availableProcessors();

	private final List<Integer> soldItems = new ArrayList<>();
	private final List<Double> soldItemsPercents = new ArrayList<>();

	private int totalSum = 0;
	private final List<Worker> workers = new ArrayList<>();

	private final CyclicBarrier barrierForSum = new CyclicBarrier(NO_OF_THREADS, () -> {
		for (Worker worker : workers) {
			totalSum += worker.sum;
		}
	});

	private final CyclicBarrier barrierForPercent = new CyclicBarrier(NO_OF_THREADS, () -> {
		for (Worker worker : workers) {
			soldItemsPercents.addAll(worker.partialArrayPercents);
		}
	});

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		CyclicBarrierAndSumExample obj = new CyclicBarrierAndSumExample();
		obj.start();

	}

	private void start() throws InterruptedException {
		for (int i = 0; i < 1000; i++) {
			soldItems.add((int) (100.0 * Math.random()));
		}

		List<List<Integer>> partialArrays = splitArray(soldItems, (soldItems.size() / NO_OF_THREADS) + 1);
		
		for(int i=0;i<NO_OF_THREADS;i++) {
			Worker worker = new Worker(partialArrays.get(i));
			workers.add(worker);
			worker.start();
		}
		
		for (Worker worker : workers) {
            worker.join();
        }
		
		 double sum = 0;
	        for (Double soldItemsPercent : soldItemsPercents) {
	            sum += soldItemsPercent;
	        }
	        System.out.println(sum);

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

	class Worker extends Thread {

		final List<Integer> partialArray;
		final List<Double> partialArrayPercents = new ArrayList<>();
		int sum = 0;

		Worker(List<Integer> partialArray) {
			this.partialArray = partialArray;
		}

		public void run() {
			for (int partialSum : partialArray) {
				sum += partialSum;
			}

			try {
				barrierForSum.await();
			} catch (InterruptedException e) {
				interrupt();
			} catch (BrokenBarrierException e) {
				interrupt();
			}

			if (isInterrupted())
				return;

			for (Integer partialSum : partialArray) {
				partialArrayPercents.add((double) partialSum * 100 / (double) totalSum);
			}

			try {
				barrierForPercent.await();
			} catch (InterruptedException e) {
				interrupt();
			} catch (BrokenBarrierException e) {
				interrupt();
			}

			if (isInterrupted())
				return;

		}
	}

}
