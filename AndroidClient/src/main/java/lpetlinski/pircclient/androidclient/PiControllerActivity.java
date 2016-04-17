package lpetlinski.pircclient.androidclient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.github.niqdev.mjpeg.DisplayMode;
import com.github.niqdev.mjpeg.Mjpeg;
import com.github.niqdev.mjpeg.MjpegInputStream;
import com.github.niqdev.mjpeg.MjpegSurfaceView;

import java.io.IOException;

import lpetlinski.pirccar.base.Move;
import lpetlinski.pirccar.base.MoveMessage;
import lpetlinski.pirccar.base.Turn;
import lpetlinski.simpleconnection.Client;
import lpetlinski.simpleconnection.ClientConfig;
import lpetlinski.simpleconnection.events.Event;
import lpetlinski.simpleconnection.protocol.JSONProtocol;
import rx.functions.Action1;

public class PiControllerActivity extends Activity {

    private Client client;
    private MoveMessage actualState;
    private MjpegSurfaceView mjpegView = null;
    private String serverIp = null;
    private Integer serverPort = null;
    private String mjpegPort = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pi_controller);

        Intent intent = getIntent();

        this.mjpegView = (MjpegSurfaceView) findViewById(R.id.mjpegView);

        this.serverIp = intent.getStringExtra(MainActivity.SERVER_IP);
        this.serverPort = intent.getIntExtra(MainActivity.SERVER_PORT, 0);
        this.mjpegPort = intent.getStringExtra(MainActivity.MJPEG_SERVER_PORT);

        this.runMjpegView();

        ClientConfig config = new ClientConfig();
        config.setProtocol(new JSONProtocol());
        config.setAddress(this.serverIp);
        config.setPort(this.serverPort);
        this.actualState = new MoveMessage();

        final Handler handler = new Handler(Looper.getMainLooper());

        final AlertDialog connectingDialog = createConnectingDialog();
        connectingDialog.show();

        this.client = new Client(config);
        this.client.onConnectionOpened(new Event() {
            @Override
            public void onEventOccurred() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        connectingDialog.dismiss();
                    }
                });
            }
        });

        this.client.onConnectionClosed(new Event() {
            @Override
            public void onEventOccurred() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        createDisconnectedDialog();
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
                System.out.println("up");
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
                System.out.println("down");
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
                System.out.println("left");
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
                System.out.println("right");
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

    private AlertDialog createConnectingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Connecting");
        return builder.create();
    }

    private AlertDialog createDisconnectedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Disconnected");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        return builder.create();
    }

    private void runMjpegView() {
        Mjpeg.newInstance()
                .open("http://" + this.serverIp + ":" + this.mjpegPort)
                .doOnError(new Action1<Throwable>() {
                               @Override
                               public void call(Throwable throwable) {
                                   PiControllerActivity.this.runMjpegView();
                               }
                           }
                )
                .subscribe(new Action1<MjpegInputStream>() {
                    @Override
                    public void call(MjpegInputStream mjpegInputStream) {
                        mjpegView.setSource(mjpegInputStream);
                        mjpegView.setDisplayMode(DisplayMode.FULLSCREEN);
                        mjpegView.showFps(false);
                    }
                });
    }


    @Override
    protected void onStop() {
        this.stopClients();
        super.onStop();
    }

    @Override
    protected void onPause() {
        stopClients();
        super.onPause();
    }

    @Override
    protected void onResume() {
        if(!this.client.isStarted()) {
            finish();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        stopClients();
        super.onDestroy();
    }

    private void stopClients() {
        if(this.client != null && this.client.isStarted()) {
            this.client.stopClient();
        }
        if(this.mjpegView != null &&this.mjpegView.isStreaming()) {
            this.mjpegView.stopPlayback();
        }
    }
}
