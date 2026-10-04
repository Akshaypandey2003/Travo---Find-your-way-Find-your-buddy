package com.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "jwt.secret=test-only-secret-for-jwt-context-test-32-bytes")
class ApiGatewayApplicationTests {

	@Test
	void contextLoads() {
	}

}
