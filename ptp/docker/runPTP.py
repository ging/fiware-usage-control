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
        flink_endpoint = "138.4.7.94:8082"
        prefix = str(randint(0, 9)+time.time())
        content_length = int(self.headers['Content-Length']) # <--- Gets the size of data
        post_data = self.rfile.read(content_length) # <--- Gets the data itself
        data = json.loads(post_data)
        modified_program = self.generate_cep_code(data)
        self.write_program(modified_program, prefix) # <-- Write to disk instead of print
        directory = self.execute_maven(prefix)
        previous_job_id = data["previousJobId"]
        kill_job = self.kill_job(previous_job_id, flink_endpoint)
        jar_id = self.upload_jar(directory, flink_endpoint)
        job_id = self.run_job(jar_id, flink_endpoint)
        self.delete_jar(jar_id, flink_endpoint)
        self._set_headers()

    def execute_maven(self, prefix):
        mypath = './cep' + prefix
        os.chdir(mypath)
        p = subprocess.Popen(["mvn", "package"], stdout = subprocess.PIPE)
        output, err = p.communicate()
        os.chdir("..")
        print (output)
        return mypath
    
    def upload_jar(self, directory, flink_endpoint):
        mypath = directory+'/target'
        os.chdir(mypath)
        files = os.listdir('./')
        jarName = ""
        for name in files:
            if 'cep' in name and 'original' not in name:
                jarName = name
                print(name)
        FLINK_ENDPOINT = "http://" + flink_endpoint + "/jars/upload"
        file_list = [  
        ('jarfile', (jarName, open(jarName, 'rb'), mypath))]
        r = requests.post(url = FLINK_ENDPOINT,  files = file_list) 
        pastebin_url = json.loads(r.text) 
        args = pastebin_url["filename"].split("/")
        jar_id = args[len(args) - 1]
        print("About Uploaded Jar:%s"%pastebin_url)
        os.chdir('../..')
        shutil.rmtree('./' + directory, ignore_errors=True)
        return jar_id
   
    def run_job(self, jar_id, flink_endpoint):
        FLINK_ENDPOINT = "http://" + flink_endpoint + "/jars/" + jar_id + "/run?allowNonRestoredState=true"
        r = requests.post(url = FLINK_ENDPOINT) 
        pastebin_url = json.loads(r.text)
        print("About the running Job:%s"%pastebin_url) 
        job_id = pastebin_url["jobid"]
        print("About the running Job:%s"%pastebin_url)    
        return job_id  
    
    def delete_jar(self, jar_id, flink_endpoint):
        FLINK_ENDPOINT = "http://" + flink_endpoint + "/jars/" + jar_id
        r = requests.delete(url = FLINK_ENDPOINT)
    
    def generate_cep_code(self, data):
        return cep.createProgram(data)

    def write_program(self, code, prefix):
        os.chdir('./')
        shutil.copytree('./cep', './cep' + prefix)
        f = open(f"./cep{prefix}/src/main/scala/org.fiware.cosmos.orion.flink.cep/CEPMonitoring.scala", "r+")
        print ("Name of the file: ", f.name)
        part1 = ""
        part2 = ""
        count = 0
        lines = f.readlines()
        for x in lines:
            part1 = part1 + x
            if '// TODO' in x:
                break
        for x in lines:
            count = count + 1
            if count >= 42:
                part2 = part2 + x             
        f.close()
        w = open(f"./cep{prefix}/src/main/scala/org.fiware.cosmos.orion.flink.cep/CEPMonitoring.scala", "w+")
        w.write(part1)
        w.close()
        u = open(f"./cep{prefix}/src/main/scala/org.fiware.cosmos.orion.flink.cep/CEPMonitoring.scala", "a")
        u.write(code)
        u.write(part2)
        u.close()	

    def kill_job(self, job_id, flink_endpoint):
        FLINK_ENDPOINT = "http://" + flink_endpoint + "/jobs/" + job_id
        requests.patch(url = FLINK_ENDPOINT)

        
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
