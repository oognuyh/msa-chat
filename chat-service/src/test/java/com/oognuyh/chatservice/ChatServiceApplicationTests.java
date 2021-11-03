package com.oognuyh.chatservice;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ChatServiceApplicationTests {

	@Test
	void contextLoads() {
		System.out.println(List.of("a").stream().collect(Collectors.joining()));
	}
}
