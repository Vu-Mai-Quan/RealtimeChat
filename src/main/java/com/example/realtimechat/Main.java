package com.example.realtimechat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.instrument.Instrumentation;

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
