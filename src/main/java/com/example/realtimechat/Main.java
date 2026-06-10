package com.example.realtimechat;

import java.lang.instrument.Instrumentation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    public static void premain(String args, Instrumentation  instrumentation){
        System.out.println(args);
        System.out.println("premain init");
    }
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
