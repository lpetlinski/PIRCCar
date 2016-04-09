package lpetlinski.pirccar.testserver;

import lpetlinski.pirccar.piserver.LedIndicator;

public class TestLedIndicator extends LedIndicator {

    public TestLedIndicator() {
        super(new TestGpioController());
    }
}
