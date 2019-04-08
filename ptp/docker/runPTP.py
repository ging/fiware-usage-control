from pathlib import Path    
from http.server import BaseHTTPRequestHandler, HTTPServer
import socketserver,subprocess,os,requests,json,time

class S(BaseHTTPRequestHandler):
    def _set_headers(self):
        self.send_response(200)
        self.send_header('Content-type', 'text/html')
        self.end_headers()

    def do_GET(self):
        self._set_headers()
        self.wfile.write("<html><body><h1>hi!</h1></body></html>")

    def do_HEAD(self):
        self._set_headers()
        
    def do_POST(self):
        # Doesn't do anything with posted data
        flinkEndpoint="138.4.7.94:8082"
        entryClass=""
        content_length = int(self.headers['Content-Length']) # <--- Gets the size of data
        post_data = self.rfile.read(content_length) # <--- Gets the data itself
        print(post_data)
        ##Call function to generate cep code then call to execute maven, teh code must be in the 
        #current directory
        self.execute_maven()
        jarId=self.upload_jar(flinkEndpoint)
        jobId=self.run_job(jarId,flinkEndpoint)
        self._set_headers()

    
    def execute_maven(self):
        mypath = './cep'
        os.chdir(mypath)
        p = subprocess.Popen(["mvn", "package"], stdout=subprocess.PIPE)
        output, err = p.communicate()
        print (output)
    
    def upload_jar(self,flinkEndpoint):
        mypath = Path().absolute()/'target'
        os.chdir(mypath)
        files = os.listdir(mypath)
        jarName = ""
        for name in files:
            if 'cep' in name and 'original' not in name:
                jarName = name
                print(name)
        FLINK_ENDPOINT = "http://"+flinkEndpoint+"/jars/upload"
        file_list = [  
        ('jarfile', (jarName, open(jarName, 'rb'), mypath))]
        r = requests.post(url = FLINK_ENDPOINT,  files=file_list) 
        pastebin_url = json.loads(r.text) 
        args=pastebin_url.get("filename").split("/")
        jarId=args[len(args)-1]
        print("About Uploaded Jar:%s"%pastebin_url)
        os.chdir('../..')
        return jarId
   
    def run_job(self,jarId,flinkEndpoint):
        FLINK_ENDPOINT = "http://"+flinkEndpoint+"/jars/"+jarId+"/run?allowNonRestoredState=true"
        r = requests.post(url = FLINK_ENDPOINT) 
        pastebin_url = json.loads(r.text)
        jobID=pastebin_url.get("id")
        print("About the running Job:%s"%pastebin_url)    
        return jobID        
        
def run(server_class=HTTPServer, handler_class=S, port=8092):
    server_address = ('', port)
    httpd = server_class(server_address, handler_class)
    print ('Starting httpd...')
    httpd.serve_forever()

if __name__ == "__main__":
    from sys import argv

    if len(argv) == 2:
        run(port=int(argv[1]))
    else:
        run()
