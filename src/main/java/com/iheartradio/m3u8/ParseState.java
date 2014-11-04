package com.iheartradio.m3u8;

import com.iheartradio.m3u8.data.Playlist;

class ParseState implements IParseState<Playlist> {
    static final int NONE = -1;

    public final Encoding encoding;

    private MasterParseState mMasterParseState;
    private MediaParseState mMediaParseState;
    private boolean mIsExtended;
    private int mCompatibilityVersion = NONE;

    public ParseState(Encoding encoding) {
        this.encoding = encoding;
    }

    public boolean isMaster() {
        return mMasterParseState != null;
    }

    public MasterParseState getMaster() {
        return mMasterParseState;
    }

    public void setMaster() throws ParseException {
        if (isMedia()) {
            throw new ParseException(ParseExceptionType.MASTER_IN_MEDIA);
        }

        if (mMasterParseState == null) {
            mMasterParseState = new MasterParseState();
        }
    }

    public boolean isMedia() {
        return mMediaParseState != null;
    }

    public MediaParseState getMedia() {
        return mMediaParseState;
    }

    public void setMedia() throws ParseException {
        if (mMediaParseState == null) {
            mMediaParseState = new MediaParseState();
        }
    }

    public boolean isExtended() {
        return  mIsExtended;
    }

    public void setExtended() {
        mIsExtended = true;
    }

    public int getCompatibilityVersion() {
        return mCompatibilityVersion;
    }

    public void setCompatibilityVersion(int compatibilityVersion) {
        mCompatibilityVersion = compatibilityVersion;
    }

    @Override
    public Playlist buildPlaylist() throws ParseException {
        final Playlist.Builder builder = new Playlist.Builder();

        if (isMaster()) {
            builder.withMasterPlaylist(mMasterParseState.buildPlaylist());
        } else if (isMedia()) {
            builder.withMediaPlaylist(mMediaParseState.buildPlaylist()).withExtended(mIsExtended);
        } else {
            throw new ParseException(ParseExceptionType.UNKNOWN_PLAYLIST_TYPE);
        }

        return builder
                .withCompatibilityVersion(mCompatibilityVersion == NONE ? Playlist.MIN_COMPATIBILITY_VERSION : mCompatibilityVersion)
                .build();
    }
}
