package lpetlinski.pirccar.piserver;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import lpetlinski.pirccar.base.Move;
import lpetlinski.pirccar.base.Turn;

import java.util.HashMap;
import java.util.Map;

public class MotorController {

    private static final Map<Turn, Pin> pinTurnsMap = new HashMap<>();
    private static final Map<Move, Pin> pinMoveMap = new HashMap<>();
    private static final Pin enableMovePin = RaspiPin.GPIO_01;

    static {
        pinMoveMap.put(Move.Backward, RaspiPin.GPIO_02);
        pinMoveMap.put(Move.Forward, RaspiPin.GPIO_03);
        pinTurnsMap.put(Turn.Left, RaspiPin.GPIO_04);
        pinTurnsMap.put(Turn.Right, RaspiPin.GPIO_05);
    }

    private Move moveDirection;
    private Turn turnDirection;
    private PiGpioController controller;

    public MotorController() {
        this(PiGpioController.getInstance());
    }

    protected MotorController(PiGpioController controller) {
        this.moveDirection = Move.None;
        this.turnDirection = Turn.None;
        this.controller = controller;
    }

    public void setState(Move move, Turn turn) {
        if(move != this.moveDirection) {
            this.setMove(move);
        }
        if(turn != this.turnDirection) {
            this.setTurn(turn);
        }

    }

    private void setTurn(Turn turn) {
        this.turnDirection = turn;
        switch(this.turnDirection) {
            case None:
                this.controller.setOutputPinState(pinTurnsMap.get(Turn.Left), PinState.LOW);
                this.controller.setOutputPinState(pinTurnsMap.get(Turn.Right), PinState.LOW);
                break;
            case Left:
                this.controller.setOutputPinState(pinTurnsMap.get(Turn.Left), PinState.HIGH);
                this.controller.setOutputPinState(pinTurnsMap.get(Turn.Right), PinState.LOW);
                break;
            case Right:
                this.controller.setOutputPinState(pinTurnsMap.get(Turn.Left), PinState.LOW);
                this.controller.setOutputPinState(pinTurnsMap.get(Turn.Right), PinState.HIGH);
                break;
        }
    }

    private void setMove(Move move) {
        this.moveDirection = move;
        switch (this.moveDirection) {
            case None:
                this.controller.setOutputPinState(enableMovePin, PinState.LOW);
                this.controller.setOutputPinState(pinMoveMap.get(Move.Backward), PinState.LOW);
                this.controller.setOutputPinState(pinMoveMap.get(Move.Forward), PinState.LOW);
                break;
            case Forward:
                this.controller.setOutputPinState(enableMovePin, PinState.HIGH);
                this.controller.setOutputPinState(pinMoveMap.get(Move.Backward), PinState.LOW);
                this.controller.setOutputPinState(pinMoveMap.get(Move.Forward), PinState.HIGH);
                break;
            case Backward:
                this.controller.setOutputPinState(enableMovePin, PinState.HIGH);
                this.controller.setOutputPinState(pinMoveMap.get(Move.Backward), PinState.HIGH);
                this.controller.setOutputPinState(pinMoveMap.get(Move.Forward), PinState.LOW);
                break;
        }
    }

}
