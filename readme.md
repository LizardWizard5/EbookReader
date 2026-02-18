# Java Client - Python Server eBook reader

Created with the purpose of escaping the need to actually read my history essay readings, this eBook reader system is designed to be hosted at home. The Server utilizes python to use KokoroTTS for actually synthesizing the text from a PDF and the Java client just because I felt like using Java.

# Server
* Flask API with CORS enforcement
* KokoroTTS to synthesize pdf text contents
* mySQL db
## Things you must do
* Install dependencies specified in **requirements.txt**
* Setup mySQL database using the provided SQL script in **Server/SQLScripts**
* Create a **.env** file containing:
	* HOST: mySQL database hostname
	* USER: mySQL database username
	* PASSWORD: mySQL database password
* Create a **allowedips.txt** file that contains a list of ips you will allow the server to interact with



# Client
* JavaFX
* ApacheHttpClient



