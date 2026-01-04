
"""
Returns an bits of an audio chunk based on microseconds input
Used for streaming audio in chunks
Always wav format
Given: wav file is a standardized format with a fixed bitrate
"""
def getChunkFromMS(fileName,ms):
    bitrate = 1411.2  # in kbps for wav
    bytesPerMicrosecond = (bitrate * 1000) / 8 / 1000000  # Convert kbps to bytes per microsecond
    bytePosition = int(ms * bytesPerMicrosecond)

    with open(fileName, 'rb') as f:
        f.seek(bytePosition)
        chunk = f.read(int(bytesPerMicrosecond * 	300000000))  # Read 5 minute worth of data
    return chunk