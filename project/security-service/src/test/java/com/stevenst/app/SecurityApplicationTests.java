package com.stevenst.app;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;

@EntityScan("com.stevenst.lib.model")
@SpringBootTest
class SecurityApplicationTests {

}
