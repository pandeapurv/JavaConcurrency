package com.apande.threadexamples.collectionsexamples;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConcurrentMapExample {

	ConcurrentMap<SubscriptionId, SubscriptionContext> map = new ConcurrentHashMap<>();

	public static void main(String[] args) throws InterruptedException{
		// TODO Auto-generated method stub
		ConcurrentMapExample concurrentMapExample = new ConcurrentMapExample();
		concurrentMapExample.start();
	}
	
	private void start() throws InterruptedException {
		UserThread user1 = new UserThread("user-1");
		UserThread user2 = new UserThread("user-2");
		UserThread user3 = new UserThread("user-3");
		
		UpdatingThread update1 = new UpdatingThread();
        UpdatingThread update2 = new UpdatingThread();
        
        user1.start();
        user2.start();
        user3.start();

        update1.start();
        update2.start();

        user1.join();
        user2.join();
        user3.join();

        update1.interrupt();
        update2.interrupt();
		
	}

	private class SubscriptionId {
		final String id;

		private SubscriptionId(String id) {
			this.id = id;
		}
	}

	private class SubscriptionContext {
		final String name;
		final int version;

		private SubscriptionContext(String name, int version) {
			this.name = name;
			this.version = version;
		}
	}

	private void createSubscription(SubscriptionId subscriptionId) {
		SubscriptionContext context = new SubscriptionContext("context-" + subscriptionId.id, 0);
		map.put(subscriptionId, context);
	}

	private void closeSubscription(SubscriptionId subscriptionId) {
		map.remove(subscriptionId);
	}

	private void updateSubscription(SubscriptionId subscriptionId) {
		map.compute(subscriptionId, (id, context)->{
			if(context == null)
				return null;
			SubscriptionContext sc = new SubscriptionContext(context.name, context.version + 1);
			System.out.println("The subscription " + id.id
                    + " is updated, the new version of the context is " + sc.name + "#" + sc.version);
			return sc;
		});
	}
	
	class UpdatingThread extends Thread {
		@Override
		public void run() {
			while(!isInterrupted()) {
				map.forEach((key, value) -> updateSubscription(key));
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					return;
				}
			}
		}
		
	}

	class UserThread extends Thread {
		private String id;

		public UserThread(String id) {
			this.id = id;
		}

		@Override
		public void run() {
			SubscriptionId subscriptionId = new SubscriptionId(id);
			createSubscription(subscriptionId);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				return;
			}
			closeSubscription(subscriptionId);
		}

	}
}
