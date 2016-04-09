package lpetlinski.pirccar.piserver;

import com.pi4j.io.gpio.*;

import java.util.HashMap;
import java.util.Map;

public class PiGpioController {

    private static PiGpioController instance;

    private GpioController controller;
    private Map<Pin, GpioPinDigitalOutput> outputPinsMap;

    protected PiGpioController() {
        this.controller = GpioFactory.getInstance();
        this.outputPinsMap = new HashMap<Pin, GpioPinDigitalOutput>();
    }

    public void setOutputPinState(Pin pin, PinState state) {
        if(!outputPinsMap.containsKey(pin)) {
            GpioPinDigitalOutput outPin = this.controller.provisionDigitalOutputPin(pin, state);
            outPin.setShutdownOptions(true, PinState.LOW);
            this.outputPinsMap.put(pin, outPin);
        } else {
            GpioPinDigitalOutput outPin = this.outputPinsMap.get(pin);
            if(outPin.getState() != state) {
                outPin.setState(state);
            }
        }
    }

    public static final PiGpioController getInstance() {
        if(instance == null) {
            instance = new PiGpioController();
        }
        return instance;
    }

}
