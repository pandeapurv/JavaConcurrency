package com.apande.threadexamples.synchronizers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;


// exchange task between two parallel threads
public class ExchangerExample {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		ExchangerExample exchangerExample = new ExchangerExample();
		exchangerExample.start();
	}

	public void start() throws InterruptedException {

		Exchanger<SumTask> exchanger = new Exchanger<>();
		SummingThread summingThread = new SummingThread(exchanger);

		summingThread.start();
		SumTask task1 = new SumTask(generateArray());
		SumTask task0 = exchanger.exchange(task1);

		SumTask task2 = new SumTask(generateArray());
		task1 = exchanger.exchange(task2);

		System.out.println("The result of task1 is: " + task1.totalSum);

		task2 = exchanger.exchange(null);

		System.out.println("The result of task2 is: " + task2.totalSum);

	}

	private List<Double> generateArray() {
		int arraySize = 2;
		List<Double> array = new ArrayList<>(arraySize);
		for (int i = 0; i < arraySize; i++) {
			array.add(Math.random());
		}
		System.out.println();
		for(double i : array) {
			System.out.print(i + " ");
		}
		System.out.println();
		return array;
	}

	class SummingThread extends Thread {

		private final Exchanger<SumTask> exchanger;

		public SummingThread(Exchanger<SumTask> exchanger) {
			this.exchanger = exchanger;
		}

		public void run() {
			SumTask previousTask = null;
			while (true) {
				try {
					SumTask newTask = exchanger.exchange(previousTask);
					if (newTask == null)
						return;
					newTask.sum();
					previousTask = newTask;
				} catch (InterruptedException e) {
					interrupt();
				}

				if (isInterrupted())
					return;
			}

		}
	}

	class SumTask {
		private final List<Double> array;
		double totalSum = 0;

		public SumTask(List<Double> array) {
			this.array = array;
		}

		public void sum() {
			for (Double val : array) {
				totalSum += val;
			}
		}
	}

}
