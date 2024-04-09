package rhizome.services;

import io.activej.async.function.AsyncRunnable;
import io.activej.eventloop.Eventloop;
import io.activej.promise.Promise;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;
import java.util.List;

class BaseServiceTest {

    @Mock
    private Eventloop eventloop;

    private BaseService baseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        baseService = new BaseService(eventloop) {
            @Override
            public Promise<?> start() {
                return null;
            }

            @Override
            public Promise<?> stop() {
                return null;
            }
        };
    }

    @Test
    void testGetEventloop() {
        // Call the method under test
        Eventloop result = baseService.getEventloop();

        // Verify the result
        assertEquals(eventloop, result);
    }

    @Test
    void testAddRoutine() {
        // Prepare test data
        AsyncRunnable routine = mock(AsyncRunnable.class);

        // Call the method under test
        BaseService result = baseService.addRoutine(routine);

        // Verify the behavior
        assertEquals(baseService, result);
        assertEquals(1, baseService.getRoutines().size());
        assertEquals(routine, baseService.getRoutines().get(0));
    }

    @Test
    void testBuild() {
        // Prepare test data
        AsyncRunnable routine = mock(AsyncRunnable.class);
        baseService.addRoutine(routine);

        // Call the method under test
        BaseService result = baseService.build();

        // Verify the behavior
        assertEquals(baseService, result);
        assertEquals(1, baseService.getRoutines().size());
        assertNotEquals(routine, baseService.getRoutines().get(0));
        verify(routine, times(1)).run();
    }

    @Test
    void testAsyncRun() {
        // Prepare test data
        AsyncRunnable runnable1 = mock(AsyncRunnable.class);
        AsyncRunnable runnable2 = mock(AsyncRunnable.class);
        AtomicBoolean flag1 = new AtomicBoolean(false);
        AtomicBoolean flag2 = new AtomicBoolean(false);

        // Mock behavior
        when(runnable1.run()).thenReturn(Promise.ofCallback(callback -> {
            flag1.set(true);
            callback.accept(null, null);
        }));
        when(runnable2.run()).thenReturn(Promise.ofCallback(callback -> {
            flag2.set(true);
            callback.accept(null, null);
        }));

        // Call the method under test
        Promise<Void> result = BaseService.asyncRun(List.of(runnable1, runnable2));

        // Verify the behavior
        assertEquals(Promise.complete(), result);
        assertEquals(true, flag1.get());
        assertEquals(true, flag2.get());
        verify(runnable1, times(1)).run();
        verify(runnable2, times(1)).run();
    }
}