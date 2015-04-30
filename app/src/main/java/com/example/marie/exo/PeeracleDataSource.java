package com.example.marie.exo;

import android.net.Uri;

import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DataSpec;
import com.google.android.exoplayer.upstream.FileDataSource;
import com.google.android.exoplayer.upstream.TransferListener;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by marie on 4/17/15.
 */
public class PeeracleDataSource implements DataSource {
    private final TransferListener listener;
    private long bytesRemaining;
    private boolean opened;
    private RandomAccessFile file;

    public PeeracleDataSource(TransferListener listener) {
        this.listener = listener;
    }

    public PeeracleDataSource() {
    this.listener = null;
    }

    @Override
    public long open(DataSpec dataSpec) throws IOException {
        String filename = "tos_init.segment";
        String path = "/mnt/sdcard/" + filename;
        File file = new File(path);
        Uri imageUri = Uri.fromFile(file);

        opened = true;
        if (listener != null) {
            listener.onTransferStart();
        }

        return 0;
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public int read(byte[] buffer, int offset, int readLength) throws IOException {
        if (bytesRemaining == 0) {
            return -1;
        } else {
            int bytesRead = 0;
            try {
                bytesRead = file.read(buffer, offset, (int) Math.min(bytesRemaining, readLength));
            } catch (IOException e) {
                throw new FileDataSource.FileDataSourceException(e);
            }

            if (bytesRead > 0) {
                bytesRemaining -= bytesRead;
                if (listener != null) {
                    listener.onBytesTransferred(bytesRead);
                }
            }

            return bytesRead;
        }
    }
}
