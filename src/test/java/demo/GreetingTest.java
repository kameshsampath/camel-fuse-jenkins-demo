package demo;

import org.apache.camel.test.AvailablePortFinder;
import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author kameshs
 */
public class GreetingTest extends CamelBlueprintTestSupport {

    private final AtomicInteger counter = new AtomicInteger(1);

    @Override
    protected String getBlueprintDescriptor() {
        return "OSGI-INF/blueprint/blueprint.xml";
    }

    @Test
    public void testGreetUser() throws Exception {


    }


    private int getAvailablePort() {
        return AvailablePortFinder.getNextAvailable();
    }
}
