package com.company;

public class Main {
    public static void main(String[] args) throws Exception{
        Server server = new Server("localhost", 5000);
        Thread.currentThread().join();
    }
}
