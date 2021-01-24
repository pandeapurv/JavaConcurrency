package com.apande.threadexamples.collectionsexamples;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MapReduce {

	private final BlockingQueue<String> linesQueue = new LinkedBlockingQueue<>();
	private final BlockingQueue<KeyValues> keyValuesQueue = new LinkedBlockingQueue<>();
	private final ConcurrentHashMap<String, List<Integer>> keyValuesMap = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, Integer> reduceResultMap = new ConcurrentHashMap<>();

	public static void main(String[] args) throws IOException, InterruptedException {
		MapReduce mapReduce = new MapReduce();
		mapReduce.start();
	}

	void start() throws IOException, InterruptedException {
		MapThread mapThread1 = new MapThread();
		MapThread mapThread2 = new MapThread();
		
		ReduceThread reduceThread1 = new ReduceThread();
		ReduceThread reduceThread2 = new ReduceThread();
		
		mapThread1.start();
		mapThread2.start();
		reduceThread1.start();
		reduceThread2.start();
		
		BufferedReader reader = new BufferedReader(new FileReader(
				"/Users/Apurv/workstation/neweclipse202008/JavaConcurrency/resources/LordOfTheRings.txt"));

		String line;
		while ((line = reader.readLine()) != null) {
			//System.out.println(line);
			linesQueue.put(line);
		}
		
		isReady();
		mapThread1.interrupt();
		mapThread2.interrupt();
		reduceThread1.interrupt();
		reduceThread2.interrupt();
		
		reader.close();
		
		 List<Map.Entry<String, Integer>> theMostPopularWords = reduceResultMap.entrySet()
	                .stream()
	                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
	                .limit(100)
	                .collect(Collectors.toList());

	        theMostPopularWords.forEach(System.out::println);
	}
	
	void isReady() throws InterruptedException {
		while(!linesQueue.isEmpty() || !keyValuesQueue.isEmpty()) {
			Thread.sleep(3000);
		}
	}

	List<KeyValue> map(String line) {
		String[] words = line.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
		List<KeyValue> keyValueList = new ArrayList<>();
		for (String word : words) {
			keyValueList.add(new KeyValue(word, 1));
		}
		return keyValueList;
	}

	Integer reduce(KeyValues keyValues) {
		if (keyValues.key.length() < 4)
            return 0;
		int sum = 0;
		for (Integer value : keyValues.values) {
			sum += value;
		}
		return sum;
	}

	void prepareForReduce(KeyValue keyValue) {
		String key = keyValue.key;

		keyValuesMap.compute(key, (word, values) -> {
			if (values == null) {
				List<Integer> count = new ArrayList<>();
				count.add(keyValue.value);
				return count;
			} else {
				values.add(keyValue.value);
				if (values.size() > 100) {
					publishForReduce(key, values);
					return new ArrayList<>();
				} else {
					return values;
				}
			}
		});

	}
	
	void setResult(String key, Integer result) {
		reduceResultMap.compute(key, (k, currentValue)->{
			if(currentValue == null) {
				return result;
			}else {
				return reduce(new KeyValues(key, Arrays.asList(currentValue,result)));
			}
		});
	}

	private void publishForReduce(String key, List<Integer> values) {
		if (!values.isEmpty()) {
			try {
				keyValuesQueue.put(new KeyValues(key, values));
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	static class KeyValue {
		final String key;
		final Integer value;

		KeyValue(String key, Integer value) {
			this.key = key;
			this.value = value;
		}

	}

	static class KeyValues {
		final String key;
		final List<Integer> values;

		KeyValues(String key, List<Integer> values) {
			this.key = key;
			this.values = values;
		}

	}

	class MapThread extends Thread {
		@Override
		public void run() {
			while (!isInterrupted()) {
				String line;
				try {
					line = linesQueue.poll(1, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					return;
				}
				if (line == null) {
					keyValuesMap.replaceAll((key, currentValues)->{
						publishForReduce(key, currentValues);
						return new ArrayList<>();
					});
					continue;
				}
					

				List<KeyValue> keyValuesList = map(line);

				keyValuesList.forEach(k -> prepareForReduce(k));
			}
		}
	}

	class ReduceThread extends Thread {
		@Override
		public void run() {
			while (!isInterrupted()) {
				KeyValues keyValues;
				try {
					keyValues = keyValuesQueue.take();
				} catch (InterruptedException e) {
					return;
				}
				Integer result = reduce(keyValues);
				setResult(keyValues.key, result);
			}
		}

	}

}
