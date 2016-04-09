package lpetlinski.pirccar.testclient;

import lpetlinski.pirccar.base.Move;
import lpetlinski.pirccar.base.MoveMessage;
import lpetlinski.pirccar.base.Turn;
import lpetlinski.simpleconnection.Client;
import lpetlinski.simpleconnection.ClientConfig;
import lpetlinski.simpleconnection.events.Event;
import lpetlinski.simpleconnection.events.EventWithMessage;
import lpetlinski.simpleconnection.events.Message;
import lpetlinski.simpleconnection.protocol.JSONProtocol;

import java.io.IOException;

public class TestClient {

    public static void main(String[] args) throws IOException {
        ClientConfig config = new ClientConfig();
        config.setAddress("192.168.0.10");
        config.setProtocol(new JSONProtocol());
        Client client = new Client(config);

        client.onReceive(new EventWithMessage<Message>() {
            @Override
            public void onEventOccurred(Message r) {
                System.out.println("Client receiver: " + r.toString());
            }
        });
        client.onConnectionClosed(new Event() {
            @Override
            public void onEventOccurred() {
                System.out.println("Connection closed.");
            }
        });
        client.startClient();

        MoveMessage state = new MoveMessage();
        state.setMove(Move.None);
        state.setTurn(Turn.None);
        int read = 0;

        while(read != Character.valueOf('e')) {
            read = System.in.read();
            if(read == Character.valueOf('a')) {
                state.setTurn(Turn.Left);
            } else if(read == Character.valueOf('d')) {
                state.setTurn(Turn.Right);
            } else if(read == Character.valueOf('x')) {
                state.setTurn(Turn.None);
            } else if(read == Character.valueOf('w')) {
                state.setMove(Move.Forward);
            } else if(read == Character.valueOf('s')) {
                state.setMove(Move.Backward);
            } else if(read == Character.valueOf('z')) {
                state.setMove(Move.None);
            } else
            client.send(state);
        }

        client.stopClient();
    }

}
