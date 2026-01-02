
"""
Returns an bits of an audio chunk based on milliseconds input
Used for streaming audio in chunks
Always wav format
Given: wav file is a standardized format with a fixed bitrate
"""
def getChunkFromMS(fileName,ms):
    bitrate = 1411.2  # in kbps for wav
    bytesPerMillisecond = (bitrate * 1000) / 8 / 1000  # Convert kbps to bytes per ms
    bytePosition = int(ms * bytesPerMillisecond)

    with open(fileName, 'rb') as f:
        f.seek(bytePosition)
        chunk = f.read(int(bytesPerMillisecond * 1000))  # Read 1 second worth of data
    return chunk