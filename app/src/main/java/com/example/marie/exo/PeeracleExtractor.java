package com.example.marie.exo;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.MediaExtractor;
import android.net.Uri;

import com.google.android.exoplayer.C;
import com.google.android.exoplayer.MediaFormat;
import com.google.android.exoplayer.MediaFormatHolder;
import com.google.android.exoplayer.SampleHolder;
import com.google.android.exoplayer.SampleSource;
import com.google.android.exoplayer.TrackInfo;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.source.SampleExtractor;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.FileDataSource;
import com.google.android.exoplayer.util.Assertions;
import com.google.android.exoplayer.util.Util;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * Created by marie on 4/23/15.
 */
@TargetApi(16)
public class PeeracleExtractor implements SampleExtractor {
    // Parameters for a Uri data source.
    private final Context context;
    private final Uri uri;
    private final Map<String, String> headers;

    // Parameters for a FileDescriptor data source.
    private final FileDescriptor fileDescriptor;
    private final long fileDescriptorOffset;
    private final long fileDescriptorLength;

    //private final MediaExtractor mediaExtractor;

    private TrackInfo[] trackInfos;

    /**
     * Instantiates a new sample extractor reading from the specified {@code uri}.
     *
     * @param context Context for resolving {@code uri}.
     * @param uri The content URI from which to extract data.
     * @param headers Headers to send with requests for data.
     */
    public PeeracleExtractor(Context context, Uri uri, Map<String, String> headers) {
        Assertions.checkState(Util.SDK_INT >= 16);

        this.context = Assertions.checkNotNull(context);
        this.uri = Assertions.checkNotNull(uri);
        this.headers = headers;

        fileDescriptor = null;
        fileDescriptorOffset = 0;
        fileDescriptorLength = 0;

        //mediaExtractor = new MediaExtractor();
    }

    /**
     * Instantiates a new sample extractor reading from the specified seekable {@code fileDescriptor}.
     * The caller is responsible for releasing the file descriptor.
     *
     * @param fileDescriptor File descriptor from which to read.
     * @param offset The offset in bytes into the file where the data to be extracted starts.
     * @param length The length in bytes of the data to be extracted.
     */
    public PeeracleExtractor(FileDescriptor fileDescriptor, long offset, long length) {
        Assertions.checkState(Util.SDK_INT >= 16);

        context = null;
        uri = null;
        headers = null;

        this.fileDescriptor = Assertions.checkNotNull(fileDescriptor);
        fileDescriptorOffset = offset;
        fileDescriptorLength = length;

        //mediaExtractor = new MediaExtractor();
    }

    @Override
    public boolean prepare() throws IOException {


        int trackCount = mediaExtractor.getTrackCount();
        trackInfos = new TrackInfo[trackCount];
        for (int i = 0; i < trackCount; i++) {
            android.media.MediaFormat format = mediaExtractor.getTrackFormat(i);
            long durationUs = format.containsKey(android.media.MediaFormat.KEY_DURATION)
                    ? format.getLong(android.media.MediaFormat.KEY_DURATION) : C.UNKNOWN_TIME_US;
            String mime = format.getString(android.media.MediaFormat.KEY_MIME);
            trackInfos[i] = new TrackInfo(mime, durationUs);
        }

        return true;
    }

    @Override
    public TrackInfo[] getTrackInfos() {
        return trackInfos;
    }

    @Override
    public void selectTrack(int index) {
        //mediaExtractor.selectTrack(index);
    }

    @Override
    public void deselectTrack(int index) {
        //mediaExtractor.unselectTrack(index);
    }

    @Override
    public long getBufferedPositionUs() {
        long bufferedDurationUs = mediaExtractor.getCachedDuration();
        if (bufferedDurationUs == -1) {
            return TrackRenderer.UNKNOWN_TIME_US;
        } else {
            long sampleTime = mediaExtractor.getSampleTime();
            return sampleTime == -1 ? TrackRenderer.END_OF_TRACK_US : sampleTime + bufferedDurationUs;
        }
    }

    @Override
    public void seekTo(long positionUs) {
        mediaExtractor.seekTo(positionUs, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);
    }

    @Override
    public void getTrackMediaFormat(int track, MediaFormatHolder mediaFormatHolder) {
        mediaFormatHolder.format =
                MediaFormat.createFromFrameworkMediaFormatV16(mediaExtractor.getTrackFormat(track));
        mediaFormatHolder.drmInitData = Util.SDK_INT >= 18 ? getPsshInfoV18() : null;
    }

    @Override
    public int readSample(int track, SampleHolder sampleHolder) {
        int sampleTrack = mediaExtractor.getSampleTrackIndex();
        if (sampleTrack != track) {
            return sampleTrack < 0 ? SampleSource.END_OF_STREAM : SampleSource.NOTHING_READ;
        }

        if (sampleHolder.data != null) {
            int offset = sampleHolder.data.position();
            sampleHolder.size = mediaExtractor.readSampleData(sampleHolder.data, offset);
            sampleHolder.data.position(offset + sampleHolder.size);
        } else {
            sampleHolder.size = 0;
        }
        sampleHolder.timeUs = mediaExtractor.getSampleTime();
        sampleHolder.flags = mediaExtractor.getSampleFlags();
        if ((sampleHolder.flags & MediaExtractor.SAMPLE_FLAG_ENCRYPTED) != 0) {
            sampleHolder.cryptoInfo.setFromExtractorV16(mediaExtractor);
        }

        mediaExtractor.advance();

        return SampleSource.SAMPLE_READ;
    }

    @Override
    public void release() {
        mediaExtractor.release();
    }

    @TargetApi(18)
    private Map<UUID, byte[]> getPsshInfoV18() {
        Map<UUID, byte[]> psshInfo = mediaExtractor.getPsshInfo();
        return (psshInfo == null || psshInfo.isEmpty()) ? null : psshInfo;
    }
}
