#This will contain the code for parsing pdf file and generating audio utilizing kokoro.
import os
import torchaudio as ta
from IPython.display import display, Audio
import soundfile as sf
import torch
from PyPDF2 import PdfReader
import numpy as np
from kokoro import KPipeline
from transformers import pipeline
available_device = 'cpu' #Must use cpu in order to utilize threading.

pipeline = KPipeline(lang_code='a',device=available_device)

currentDirectory = os.getcwd()
bookDirectory = currentDirectory + "\\books\\pdf"
audioDirectory = currentDirectory + "\\books\\audio"

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
def getTextFromPDF(fileName,filters):
    print(currentDirectory)
    reader = PdfReader(bookDirectory + "\\" +fileName+".pdf")
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
fileName: name of the final outputted file (Dont include and extension, outputs.wav)
"""
def generateTTS(text,fileName):
    generator = pipeline(text, voice='af_heart')
    fullAudio = []
    for i, (gs, ps, audio) in enumerate(generator):
        fullAudio.append(audio)
    print(len(fullAudio))

    print(fullAudio)
    sf.write(f'{audioDirectory}\\{fileName}.wav', np.concatenate(fullAudio), 24000)
    return



def grabAndGenerate(pdfName):
    text = getTextFromPDF(pdfName,"")
    generateTTS(text,pdfName)
    return True