package lpetlinski.pirccar.testserver;

import lpetlinski.pirccar.piserver.MotorController;

public class TestMotorController extends MotorController {

    public TestMotorController() {
        super(new TestGpioController());
    }
}
