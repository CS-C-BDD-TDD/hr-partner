package gov.dhs.devsecops.hrpartner;

import org.aspectj.lang.JoinPoint;
import org.junit.Test;
import org.mockito.Mockito;

public class TracingAspectTest {
	private JoinPoint mockJoinPoint = Mockito.mock(JoinPoint.class);
	private Object[] objects = { "" };

	@Test
	public void shouldCallBefore() {
		TracingAspect tracingAspect = new TracingAspect();
		Mockito.when(mockJoinPoint.getArgs()).thenReturn(objects);
		tracingAspect.before(mockJoinPoint);

	}

	@Test
	public void shouldCallAfter() {
		TracingAspect tracingAspect = new TracingAspect();
		Mockito.when(mockJoinPoint.getArgs()).thenReturn(objects);
		tracingAspect.after(mockJoinPoint);

	}
}
