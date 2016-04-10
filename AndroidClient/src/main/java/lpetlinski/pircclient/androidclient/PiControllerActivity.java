package lpetlinski.pircclient.androidclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

import lpetlinski.pirccar.base.Move;
import lpetlinski.pirccar.base.MoveMessage;
import lpetlinski.pirccar.base.Turn;
import lpetlinski.simpleconnection.Client;
import lpetlinski.simpleconnection.ClientConfig;
import lpetlinski.simpleconnection.events.Event;
import lpetlinski.simpleconnection.protocol.JSONProtocol;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PiControllerActivity extends Activity {

    private Client client;
    private MoveMessage actualState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pi_controller);

        Intent intent = getIntent();

        ClientConfig config = new ClientConfig();
        config.setProtocol(new JSONProtocol());
        config.setAddress(intent.getStringExtra(MainActivity.SERVER_IP));
        config.setPort(intent.getIntExtra(MainActivity.SERVER_PORT, 0));
        this.actualState = new MoveMessage();

        final Handler handler = new Handler(Looper.getMainLooper());

        this.client = new Client(config);
        this.client.onConnectionOpened(new Event() {
            @Override
            public void onEventOccurred() {
                System.out.println("Opened");
            }
        });

        this.client.onConnectionClosed(new Event() {
            @Override
            public void onEventOccurred() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
            }
        });

        addButtonsHandlers();

        try {
            this.client.startClient();
        } catch (IOException e) {
            if(this.client.isStarted()) {
                this.client.stopClient();
            }
            finish();
        }
    }

    private void addButtonsHandlers() {
        Button upButton = (Button) findViewById(R.id.upButton);
        upButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    actualState.setMove(Move.Forward);
                    client.send(actualState);
                    return true;
                } else if(event.getAction() == MotionEvent.ACTION_UP) {
                    actualState.setMove(Move.None);
                    client.send(actualState);
                    return true;
                }
                return false;
            }
        });

        Button downButton = (Button) findViewById(R.id.downButton);
        downButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    actualState.setMove(Move.Backward);
                    client.send(actualState);
                    return true;
                } else if(event.getAction() == MotionEvent.ACTION_UP) {
                    actualState.setMove(Move.None);
                    client.send(actualState);
                    return true;
                }
                return false;
            }
        });

        Button leftButton = (Button) findViewById(R.id.leftButton);
        leftButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    actualState.setTurn(Turn.Left);
                    client.send(actualState);
                    return true;
                } else if(event.getAction() == MotionEvent.ACTION_UP) {
                    actualState.setTurn(Turn.None);
                    client.send(actualState);
                    return true;
                }
                return false;
            }
        });

        Button rightButton = (Button) findViewById(R.id.rightButton);
        rightButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    actualState.setTurn(Turn.Right);
                    client.send(actualState);
                    return true;
                } else if(event.getAction() == MotionEvent.ACTION_UP) {
                    actualState.setTurn(Turn.None);
                    client.send(actualState);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onStop() {
        if(this.client.isStarted()) {
            this.client.stopClient();
        }
        super.onStop();
    }

    @Override
    protected void onPause() {
        if(this.client.isStarted()) {
            this.client.stopClient();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!this.client.isStarted()) {
            finish();
        }
    }
}
