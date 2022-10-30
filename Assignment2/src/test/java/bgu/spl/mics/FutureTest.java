package bgu.spl.mics;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class FutureTest {

    private Future<String> future;

    @Before
    public void setUp(){//setting up a new future before each test
        future = new Future<>();
    }

    @Test
    public void testAll(){
        assertFalse(future.isDone());//checking that the future isn't done
        String str = future.get(100, TimeUnit.MILLISECONDS);
        assertEquals(str, null); //should return null(future isn't- resolved)
        str = "Complete";
        future.resolve(str);//asserting the string "complete"
        assertTrue(future.isDone()); //should return true--future is resolved
        assertEquals("Complete", future.get()); //expecting the string "complete"
        assertEquals("Complete", future.get(1000, TimeUnit.MILLISECONDS));
    }
}
