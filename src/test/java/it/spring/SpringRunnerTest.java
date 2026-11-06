package it.spring;

import net.sf.lightair.LightAir;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({SpringExtension.class, LightAir.class})
@ContextConfiguration(locations = {"classpath:spring/test-context.xml"})
public class SpringRunnerTest {

	@Autowired
	private SimpleBean simpleBean;

	@Test
	public void helloTest() {
		assertEquals("hello", simpleBean.hello());
	}
}
