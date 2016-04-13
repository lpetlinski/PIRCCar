package lpetlinski.pircclient.androidclient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

import lpetlinski.pirccar.base.Move;
import lpetlinski.pirccar.base.MoveMessage;
import lpetlinski.pirccar.base.Turn;
import lpetlinski.pircclient.androidclient.mjpeg.MjpegInputStream;
import lpetlinski.pircclient.androidclient.mjpeg.MjpegView;
import lpetlinski.simpleconnection.Client;
import lpetlinski.simpleconnection.ClientConfig;
import lpetlinski.simpleconnection.events.Event;
import lpetlinski.simpleconnection.protocol.JSONProtocol;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PiControllerActivity extends Activity {

    private Client client;
    private MoveMessage actualState;
    private MjpegView mjpegView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pi_controller);

        Intent intent = getIntent();

        this.mjpegView = (MjpegView) findViewById(R.id.mjpegView);
        if (this.mjpegView != null) {
            //this.mjpegView.setResolution(320, 240);
        }

        new DoRead().execute("http://" + intent.getStringExtra(MainActivity.SERVER_IP) + ":" + intent.getStringExtra(MainActivity.MJPEG_SERVER_PORT));

        ClientConfig config = new ClientConfig();
        config.setProtocol(new JSONProtocol());
        config.setAddress(intent.getStringExtra(MainActivity.SERVER_IP));
        config.setPort(intent.getIntExtra(MainActivity.SERVER_PORT, 0));
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
        if(this.mjpegView != null) {
            if(this.mjpegView.isStreaming()) {
                this.mjpegView.stopPlayback();
            }
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

    @Override
    protected void onDestroy() {
        if(this.mjpegView != null) {
            this.mjpegView.freeCameraMemory();
        }
        super.onDestroy();
    }

    private void setMjpegStatus(String status) {
        TextView mjpegStatus = (TextView) findViewById(R.id.viewStatus);
        mjpegStatus.setText(status);
    }

    public void mjpegImageError() {

    }

    public class DoRead extends AsyncTask<String, Void, MjpegInputStream> {
        protected MjpegInputStream doInBackground(String... url) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url[0])
                    .build();
            try {
                Response response = client.newCall(request).execute();
                return new MjpegInputStream(response.body().byteStream());
            } catch (IOException e) {
                return null;
            }
        }

        protected void onPostExecute(MjpegInputStream result) {
            PiControllerActivity.this.mjpegView.setSource(result);
            if (result != null) {
                result.setSkip(1);
                PiControllerActivity.this.setMjpegStatus("");
            } else {
                PiControllerActivity.this.setMjpegStatus("Disconnected");
            }
            PiControllerActivity.this.mjpegView.setDisplayMode(MjpegView.SIZE_BEST_FIT);
            PiControllerActivity.this.mjpegView.showFps(false);
        }
    }
}
