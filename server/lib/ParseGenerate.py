#This will contain the code for parsing pdf file and generating audio utilizing kokoro.
from asyncio import subprocess
import os
import torchaudio as ta

import soundfile as sf
import torch
from PyPDF2 import PdfReader
import numpy as np
from kokoro import KPipeline
from transformers import pipeline
import subprocess
import uuid

available_device = 'cpu' #Must use cpu in order to utilize threading.

pipeline = KPipeline(lang_code='a',device=available_device)

currentDirectory = os.getcwd()
bookDirectory= os.path.join(currentDirectory, "books", "pdf")
audioDirectory = os.path.join(currentDirectory, "books", "audio")

#Placeholder text for quicker test runs
globalText = '''
In my younger and more vulnerable years my father gave 
me some advice that I’ve been turning over in my mind 
ever since.
‘Whenever you feel like criticizing any one,’ he told me, 
‘just remember that all the people in this world haven’t had 
the advantages that you’ve had.’
He didn’t say any more but we’ve always been unusually 
communicative in a reserved way, and I understood that he 
meant a great deal more than that. In consequence I’m in-
clined to reserve all judgments, a habit that has opened up 
many curious natures to me and also made me the victim 
of not a few veteran bores. The abnormal mind is quick to 
detect and attach itself to this quality when it appears in a 
normal  person,  and  so  it  came  about  that  in  college  I  was 
unjustly accused of being a politician, because I was privy 
to the secret griefs of wild, unknown men. Most of the con-
fidences  were  unsought—frequently  I  have  feigned  sleep, 
preoccupation,  or  a  hostile  levity  when  I  realized  by  some 
unmistakable  sign  that  an  intimate  revelation  was  quiver-
ing  on  the  horizon—for  the  intimate  revelations  of  young 
men  or  at  least  the  terms  in  which  they  express  them  are 
usually  plagiaristic  and  marred  by  obvious  suppressions. 
Reserving judgments is a matter of infinite hope. I am still 
a little afraid of missing something if I forget that, as my fa-

'''

replacers = [
    ["\n"," "],#Removes all next lines, helps with better flow
    [".",","] #Replaces all periods with commas to make the pauses shorter.
]


'''
fileName: name of the pdf file (dont include .pdf)
Filters: terms to be filtered out, sometimes pdfs contain watermarks around the pdf such as "Free eBooks at Planet eBook.com"
StartingPage: specifies which page to start reading from useful for skipping title pages or personal notes from the writer
'''
def getTextFromPDF(fileName):
    print(currentDirectory)
    reader = PdfReader(os.path.join(bookDirectory, f"{fileName}.pdf"))
    text = ""
    #Read each page's text
    for i in range(0,len(reader.pages)):
        page = reader.pages[i]
        text+= page.extract_text()
    #Replace various characters in the text for overall flow
    for x in range(0,len(replacers)-1):
        text = text.replace(replacers[x][0],replacers[x][1])
    return text

"""
Future additions: voice choice, language
Generates wav file from text passed in
text: Text to be spoken
"""
def generateTTS(text,fileName):
    generator = pipeline(text, voice='af_heart')
    fullAudio = []
    for i, (gs, ps, audio) in enumerate(generator):
        fullAudio.append(audio)
    print(len(fullAudio))
    path = os.path.join(audioDirectory, f"{fileName}.wav")
    sf.write(path, np.concatenate(fullAudio), 24000)
    return f"{fileName}" #Return filename in string format for sql storage

def convert_to_mp3(wav_path, mp3_path):
    # -ac 1: Mono, -ar 24000: Sample rate, -ab 64k: Bitrate
    try:
        subprocess.run(['ffmpeg', '-i', wav_path, '-ac', '1', '-ar', '24000', '-ab', '64k', mp3_path])
        os.remove(wav_path)  # Remove the original WAV file after conversion
    except Exception as e:
        print(f"Error during wav->mp3 conversion: {e}")

def grabAndGenerate(fileName):
    text = getTextFromPDF(fileName)
    print("Extracted text from pdf:")
    print(text[0:500])#Print first 500 characters for testing
    generateTTS(text,fileName)
    convert_to_mp3(f'{audioDirectory}\\{fileName}.wav',f'{audioDirectory}\\{fileName}.mp3')
   