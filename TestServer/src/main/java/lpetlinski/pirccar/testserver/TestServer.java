package lpetlinski.pirccar.testserver;

import java.io.IOException;

import lpetlinski.pirccar.piserver.PiServer;

public class TestServer extends PiServer {

    public TestServer () {
        super(new TestLedIndicator(), new TestMotorController());
    }

    public static void main(String[] args) {
        TestServer server = new TestServer();
        try {
            server.runServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
