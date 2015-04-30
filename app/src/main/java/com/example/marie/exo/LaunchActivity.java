package com.example.marie.exo;

import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.MediaFormatHolder;
import com.google.android.exoplayer.SampleHolder;
import com.google.android.exoplayer.SampleSource;
import com.google.android.exoplayer.TrackInfo;
import com.google.android.exoplayer.source.DefaultSampleSource;
import com.google.android.exoplayer.source.FrameworkSampleExtractor;
import com.google.android.exoplayer.upstream.ByteArrayDataSource;
import com.google.android.exoplayer.upstream.FileDataSource;
import com.google.android.exoplayer.upstream.HttpDataSource;
import com.google.android.exoplayer.upstream.TransferListener;

import java.io.File;
import java.io.IOException;

//import static com.example.marie.exo.R.raw.tos_init;


public class LaunchActivity extends ActionBarActivity {
    private static final int RENDERER_COUNT = 5;
    private ExoPlayer player;
    //private SampleSource videoSS;
    //private SampleSource audioSS;
    String filename = "tos_init.segment";
    String path = "/mnt/sdcard/" + filename;
    File f = new File(path);
    Uri imageUri = Uri.fromFile(f);
    //HttpDataSource h;
    FileDataSource h;
    private EcranDessin v;
    DefaultSampleSource sampleSource =
            new DefaultSampleSource(new FrameworkSampleExtractor(this, imageUri, null), 2);

    //private ByteArrayDataSource dataSource = new ByteArrayDataSource((byte[])R.raw.tos_init);
    //private FileDataSource f = new FileDataSource(R.raw.tos_init);
    //private  File file = new File(android.os.Environment.getExternalStorageDirectory(),"tos_init");

    /*private TransferListener transferListener = new TransferListener() {
        @Override
        public void onTransferStart() {

        }

        @Override
        public void onBytesTransferred(int bytesTransferred) {

        }

        @Override
        public void onTransferEnd() {

        }
    };
    private byte[] bytes;*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = new EcranDessin(this);
        setContentView(v);

        setAttributes();
        setPlayer();
    }

    /*private void setAudioSS() {
        audioSS = new SampleSource() {
            @Override
            public boolean prepare() throws IOException {
                return false;
            }

            @Override
            public int getTrackCount() {
                return 0;
            }

            @Override
            public TrackInfo getTrackInfo(int track) {
                return null;
            }

            @Override
            public void enable(int track, long positionUs) {

            }

            @Override
            public void disable(int track) {

            }

            @Override
            public boolean continueBuffering(long positionUs) throws IOException {
                return false;
            }

            @Override
            public int readData(int track, long positionUs, MediaFormatHolder formatHolder, SampleHolder sampleHolder, boolean onlyReadDiscontinuity) throws IOException {
                return 0;
            }

            @Override
            public void seekToUs(long positionUs) {

            }

            @Override
            public long getBufferedPositionUs() {
                return 0;
            }

            @Override
            public void release() {

            }
        };
    }*/

    private void setPlayer() {

        //setAudioSS();
        //setVideoSS();

        // 1. Instantiate the player.
        player = ExoPlayer.Factory.newInstance(RENDERER_COUNT);
        // 2. Construct renderers.
        MediaCodecVideoTrackRenderer videoRenderer = new MediaCodecVideoTrackRenderer(sampleSource, 2);
        MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource);
        // 3. Inject the renderers through prepare.
        player.prepare(videoRenderer, audioRenderer);
        // 4. Pass the surface to the video renderer.
        player.sendMessage(videoRenderer, MediaCodecVideoTrackRenderer.MSG_SET_SURFACE, v.getHolder().getSurface());
        // 5. Start playback.
        player.setPlayWhenReady(true);
        //...
        player.release(); // Don’t forget to release when done!
    }

    /*private void setVideoSS() {
        videoSS = new SampleSource() {
            @Override
            public boolean prepare() throws IOException {
                //bytes = new byte[] {R.raw.tos_init};
                return false;
            }

            @Override
            public int getTrackCount() {
                return 0;
            }

            @Override
            public TrackInfo getTrackInfo(int track) {
                return null;
            }

            @Override
            public void enable(int track, long positionUs) {

            }

            @Override
            public void disable(int track) {

            }

            @Override
            public boolean continueBuffering(long positionUs) throws IOException {
                return false;
            }

            @Override
            public int readData(int track, long positionUs, MediaFormatHolder formatHolder, SampleHolder sampleHolder, boolean onlyReadDiscontinuity) throws IOException {
                return 0;
            }

            @Override
            public void seekToUs(long positionUs) {

            }

            @Override
            public long getBufferedPositionUs() {
                return 0;
            }

            @Override
            public void release() {

            }
        };
    }*/

    private void setAttributes() {
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_launch, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}