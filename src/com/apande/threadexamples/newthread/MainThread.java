package com.apande.threadexamples.newthread;

public class MainThread {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Thread thread = Thread.currentThread();
		System.out.println(thread.getName() +  " : " + thread.getId());
	}
	
	

}
