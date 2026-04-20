package ca.lizardwizard.ebookclient.Lib;

public class AudioChunk {
    private final byte[] bytes;
    private final long requestedStartMs;
    private final long chunkStartMs;
    private final long chunkEndMs;
    private final long totalDurationMs;

    public AudioChunk(byte[] bytes, long requestedStartMs, long chunkStartMs, long chunkEndMs, long totalDurationMs) {
        this.bytes = bytes;
        this.requestedStartMs = requestedStartMs;
        this.chunkStartMs = chunkStartMs;
        this.chunkEndMs = chunkEndMs;
        this.totalDurationMs = totalDurationMs;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public long getRequestedStartMs() {
        return requestedStartMs;
    }

    public long getChunkStartMs() {
        return chunkStartMs;
    }

    public long getChunkEndMs() {
        return chunkEndMs;
    }

    public long getTotalDurationMs() {
        return totalDurationMs;
    }
}
