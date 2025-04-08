package com.member.model;
import java.util.Random;

public class RandomPhoneNumber {
    public static void main(String[] args) {
        Random random = new Random();
        StringBuilder phoneNumber = new StringBuilder();

        // 生成10位隨機數字
        for (int i = 0; i < 10; i++) {
            int digit = random.nextInt(10); // 生成0-9之間的隨機數字
            phoneNumber.append(digit);
        }

        System.out.println("隨機生成的10位數字：" + phoneNumber.toString());
    }
}
