package lpetlinski.pirccar.piserver;

import com.pi4j.io.gpio.*;

import java.util.HashMap;
import java.util.Map;

public class PiGpioController {

    private static boolean debug = false;

    private static PiGpioController instance;

    private GpioController controller;
    private Map<Pin, GpioPinDigitalOutput> outputPinsMap;

    protected PiGpioController() {
        this(GpioFactory.getInstance());
    }

    protected PiGpioController(GpioController controller) {
        this.controller = controller;
        this.outputPinsMap = new HashMap<>();
    }

    public void setOutputPinState(Pin pin, PinState state) {
        debugMessage("Setting pin " + pin.getName() + " to state " + state.getName());
        if(!outputPinsMap.containsKey(pin)) {
            debugMessage("Pin not added. Adding it.");
            GpioPinDigitalOutput outPin = this.controller.provisionDigitalOutputPin(pin, state);
            outPin.setShutdownOptions(true, PinState.LOW);
            this.outputPinsMap.put(pin, outPin);
        } else {
            GpioPinDigitalOutput outPin = this.outputPinsMap.get(pin);
            if(outPin.getState() != state) {
                debugMessage("Pin state changed");
                outPin.setState(state);
            } else {
                debugMessage("Pin state not changed");
            }
        }
    }

    private void debugMessage(String msg) {
        if(debug) {
            System.out.println(msg);
        }
    }

    public static final PiGpioController getInstance() {
        if(instance == null) {
            instance = new PiGpioController();
        }
        return instance;
    }

    public static final void enableDebug() {
        debug = true;
    }

}
