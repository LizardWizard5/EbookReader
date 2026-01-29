#Testing mysql connection
from lib.db import connection
import os
from dotenv import load_dotenv
from flask import jsonify
import wave
import lib.ParseGenerate
import contextlib
load_dotenv()  # reads variables from a .env file and sets them in os.environ

audioDirectory = os.getcwd() + "\\books\\audio"

lib.ParseGenerate.convert_to_mp3(f'{audioDirectory}\\7f70b24a-1398-43f4-8361-3a71dff77382.wav',f'{audioDirectory}\\7f70b24a-1398-43f4-8361-3a71dff77382.mp3')