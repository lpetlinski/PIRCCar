package lpetlinski.pirccar.piserver;

import com.pi4j.component.motor.Motor;

import lpetlinski.pirccar.base.Move;
import lpetlinski.pirccar.base.MoveMessage;
import lpetlinski.pirccar.base.Turn;
import lpetlinski.simpleconnection.ServerConfig;
import lpetlinski.simpleconnection.events.Event;
import lpetlinski.simpleconnection.events.EventWithMessage;
import lpetlinski.simpleconnection.events.Message;
import lpetlinski.simpleconnection.protocol.JSONProtocol;

import java.io.IOException;

public class PiServer {

    private final LedIndicator ledIndicator;
    private final MotorController motorController;

    public PiServer() {
        this(new LedIndicator(), new MotorController());
    }

    protected PiServer(LedIndicator ledIndicator, MotorController motorController) {
        this.ledIndicator = ledIndicator;
        this.motorController = motorController;
    }

    public void runServer() throws IOException {
        ServerConfig config = new ServerConfig();
        config.setSynchronous(true);
        config.setProtocol(new JSONProtocol());
        final lpetlinski.simpleconnection.Server server = new lpetlinski.simpleconnection.Server(config);

        server.onReceive(new EventWithMessage<Message>() {
            @Override
            public void onEventOccurred(Message r) {
                System.out.println("Got message from client");
                if (r instanceof MoveMessage) {
                    System.out.println("It is move mesasge: Move: " + ((MoveMessage) r).getMove().name() + ", turn: " + ((MoveMessage) r).getTurn().name());
                    motorController.setState(((MoveMessage) r).getMove(), ((MoveMessage) r).getTurn());
                }
            }
        });
        server.onClientConnected(new Event() {
            @Override
            public void onEventOccurred() {
                ledIndicator.setConnected();
            }
        });
        server.onClientDisconnected(new Event() {
            @Override
            public void onEventOccurred() {
                motorController.setState(Move.None, Turn.None);
                ledIndicator.setDisconnected();
            }
        });
        server.startServer();
    }

    public static void main(String[] args) throws IOException {
        for(String arg : args) {
            if("--debug".equals(arg)) {
                PiGpioController.enableDebug();
            }
        }
        PiServer server = new PiServer();

        server.runServer();
    }
}
