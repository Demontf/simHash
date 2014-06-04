package com.tf.datamine.simHash;

public class T {

	 public static void main(String[] args) {
	        String s1 = "This is a test string for testing";
	        String s2 = "This is a test string for testing, This is a test string for testing abcd";
	        String s3 = "This is a test string for alssss testing,This is a test string for alssss testing ";
	        String s4 = "This is a s test for string testing";

	        SimHash hash1 = new SimHash(s1, 64);
	        System.out.println(hash1.intSimHash + "  " + hash1.intSimHash.bitLength());
	        SimHash hash2 = new SimHash(s2, 64);
	        System.out.println(hash2.intSimHash+ "  " + hash2.intSimHash.bitCount());
	        SimHash hash3 = new SimHash(s3, 64);
	        SimHash hash4 = new SimHash(s4, 64);
	        System.out.println(hash3.intSimHash+ "  " + hash3.intSimHash.bitCount());
	        //hash1.subByDistance(hash3, 3);
	        System.out.println("============================");
	        
	        System.out.println(hash1.hammingDistance(hash2));
	        
	        System.out.println(hash1.hammingDistance(hash3));
	        
	        System.out.println(hash1.hammingDistance(hash4));
	    }
}
