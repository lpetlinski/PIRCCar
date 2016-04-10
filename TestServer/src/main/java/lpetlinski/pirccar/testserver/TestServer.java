package lpetlinski.pirccar.testserver;

import lpetlinski.pirccar.piserver.PiServer;

import java.io.IOException;

public class TestServer extends PiServer {

    public TestServer () {
        super(new TestLedIndicator(), new TestMotorController());
    }

    public static void main(String[] args) throws IOException {
        TestServer server = new TestServer();
        server.runServer();
    }
}
