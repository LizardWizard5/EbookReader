import wave
chunk_size = 300 #Number of seconds per chunk (5minutes)
"""
Returns byte position range for given ms in audio file.
MS: Milliseconds represents start
"""
def getRange(audioFile,ms):
    #Kokoro should output the same wav format but for the sake of dynamic code the data is extracted manually.
    audio = wave.open(audioFile,'rb')
    #Audio params
    sample_rate = audio.getframerate()
    bits_per_sample = audio.getsampwidth() * 8
    num_channels = audio.getnchannels()
    timeSeconds = ms / 1000
    bytes_per_frame = (bits_per_sample / 8) * num_channels
    
    audio.close()

    StartPos = int((sample_rate * timeSeconds) * bytes_per_frame)
    EndPos = StartPos + (chunk_size * sample_rate * bytes_per_frame)
    return (StartPos, EndPos)