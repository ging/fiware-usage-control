#!/usr/bin/env python

from pathlib import Path   
from random import randint
from http.server import BaseHTTPRequestHandler, HTTPServer
import socketserver,subprocess,os,requests,json,time,shutil
import cep

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
    
    def do_DELETE (self):
        self._set_headers()
        
    def do_POST(self):
        # Doesn't do anything with posted data
        flink_endpoint="138.4.7.94:8082"
        content_length = int(self.headers['Content-Length']) # <--- Gets the size of data
        post_data = self.rfile.read(content_length) # <--- Gets the data itself
        print(post_data)
        modified_program = self.generate_cep_code(json.loads(post_data))
        print(modified_program) # <-- Write to disk instead of print
        directory=self.execute_maven()
        jarId=self.upload_jar(flink_endpoint,directory)
        jobId=self.run_job(jarId,flink_endpoint)
        self.delete_jar(jarId,flink_endpoint)
        self._set_headers()

    def execute_maven(self):
        os.chdir('./')
        prefix=str(randint(0, 9)+time.time())
        shutil.copytree('./cep', './cep'+prefix)
        mypath = './cep'+prefix
        os.chdir(mypath)
        p = subprocess.Popen(["mvn", "package"], stdout=subprocess.PIPE)
        output, err = p.communicate()
        os.chdir("..")
        print (output)
        return mypath
    
    def upload_jar(self,flink_endpoint,directory):
        mypath = directory+'/target'
        os.chdir(mypath)
        files = os.listdir('./')
        jarName = ""
        for name in files:
            if 'cep' in name and 'original' not in name:
                jarName = name
                print(name)
        FLINK_ENDPOINT = "http://"+flink_endpoint+"/jars/upload"
        file_list = [  
        ('jarfile', (jarName, open(jarName, 'rb'), mypath))]
        r = requests.post(url = FLINK_ENDPOINT,  files=file_list) 
        pastebin_url = json.loads(r.text) 
        args=pastebin_url["filename"].split("/")
        jarId=args[len(args)-1]
        print("About Uploaded Jar:%s"%pastebin_url)
        os.chdir('../..')
        shutil.rmtree('./'+directory, ignore_errors=True)
        return jarId
   
    def run_job(self,jarId,flink_endpoint):
        FLINK_ENDPOINT = "http://"+flink_endpoint+"/jars/"+jarId+"/run?allowNonRestoredState=true"
        r = requests.post(url = FLINK_ENDPOINT) 
        pastebin_url = json.loads(r.text)
        jobID=pastebin_url.["id"]
        print("About the running Job:%s"%pastebin_url)    
        return jobID  
    
    def delete_jar(self,jarId,flink_endpoint):
        FLINK_ENDPOINT = "http://"+flink_endpoint+"/jars/"+jarId
        r = requests.delete(url = FLINK_ENDPOINT)
    
    def generate_cep_code(self, data):
        return cep.createProgram(json.loads(data))

        
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
