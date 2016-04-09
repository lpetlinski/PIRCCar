package lpetlinski.pirccar.testserver;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import lpetlinski.pirccar.piserver.PiGpioController;

public class TestGpioController extends PiGpioController {

    public TestGpioController() {
    }

    @Override
    public void setOutputPinState(Pin pin, PinState state) {
        System.out.println("Pin: " + pin.getName() + " state: " + state.getName());
    }
}
