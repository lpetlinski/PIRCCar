package lpetlinski.pirccar.piserver;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import java.util.HashMap;

public class LedIndicator {

    private static final HashMap<LedState, Pin> pinStatesMap = new HashMap<>();

    static {
        pinStatesMap.put(LedState.Green, RaspiPin.GPIO_00);
        pinStatesMap.put(LedState.Red, RaspiPin.GPIO_07);
    }

    private LedState state;
    private PiGpioController controller;

    public LedIndicator() {
        this(PiGpioController.getInstance());
    }

    protected LedIndicator(PiGpioController controller) {
        this.state = LedState.Red;
        this.controller = controller;
        this.controller.setOutputPinState(pinStatesMap.get(this.state), PinState.HIGH);
    }


    public void setConnected() {
        this.setState(LedState.Green);
    }

    private void setState(LedState state) {
        if(this.state != state) {
            this.controller.setOutputPinState(pinStatesMap.get(this.state), PinState.LOW);
            this.controller.setOutputPinState(pinStatesMap.get(state), PinState.HIGH);
            this.state = state;
        }
    }

    public void setDisconnected() {
        this.setState(LedState.Red);
    }


    private enum LedState {
        Red,
        Green
    }
}
