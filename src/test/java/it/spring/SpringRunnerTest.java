package it.spring;

import static org.junit.Assert.assertEquals;

import net.sf.lightair.LightAirSpringRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@RunWith(LightAirSpringRunner.class)
@ContextConfiguration(locations = { "classpath:spring/test-context.xml" })
public class SpringRunnerTest {

    @Autowired
	private SimpleBean simpleBean;

	@Test
	public void helloTest() {
		assertEquals("hello", simpleBean.hello());
	}
}
