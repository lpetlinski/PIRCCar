package lpetlinski.pircclient.androidclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;

import lpetlinski.simpleconnection.Client;
import lpetlinski.simpleconnection.ClientConfig;
import lpetlinski.simpleconnection.events.Event;
import lpetlinski.simpleconnection.protocol.JSONProtocol;

public class MainActivity extends AppCompatActivity {

    public static final String SERVER_IP = "SERVER_IP";
    public static final String SERVER_PORT = "SERVER_PORT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void connectToServer(View view) throws IOException {
        final Intent carControllerIntent = new Intent(this, PiControllerActivity.class);

        EditText ipInput = (EditText)findViewById(R.id.ipInput);
        carControllerIntent.putExtra(SERVER_IP, ipInput.getText().toString());

        EditText portInput = (EditText)findViewById(R.id.portInput);
        carControllerIntent.putExtra(SERVER_PORT, Integer.parseInt(portInput.getText().toString()));

        startActivity(carControllerIntent);
    }
}
