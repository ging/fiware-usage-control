#!/usr/bin/python3
import os
import pymongo
import requests 
import json
import time
import argparse
from datetime import datetime

def generate (startDate, finishDate, frecuency, mongoURI, orionEndpoint):
    count=0
    if orionEndpoint is None:
        orionEndpoint="localhost:1026"
    elif startDate is None or "":
        startDate="2016-01-14"
    elif finishDate is None or "":
        finishDate="2016-01-15"
    elif frecuency is None or "":
        frecuency=5
    elif mongoURI is None or "":
        mongoURI="mongodb://localhost:27017/"
    sDateSplit = startDate.split("-")
    fDateSplit = finishDate.split("-")
    start = datetime(int(sDateSplit[0]), int(sDateSplit[1]), int(sDateSplit[2]))
    end = datetime(int(fDateSplit[0]), int(fDateSplit[1]), int(fDateSplit[2]))
    myclient = pymongo.MongoClient(mongoURI)
    mydb = myclient["superMarket"]
    mycol = mydb["tickets"]

    if count==0:
        ORION_ENDPOINT = "http://"+orionEndpoint+"/v2/entities/ticket"
        headers = {'Content-Type': 'application/json','charset': 'utf-8'}
        r = requests.get(url = ORION_ENDPOINT)
        if r.status_code!=200 or r.status_code!=201 or r.status_code!=202:
            ORION_ENDPOINT = "http://"+orionEndpoint+"/v2/entities/"
            ngsiEvent={
                'id':'ticket',
                'type':'ticket',
                '_id':{
                    'type':'String',
                    'value':0
                },
                'mall': {
                    'type':'String',
                    'value':0
                },
                'date':{
                    'type':'date',
                    'value':0
                },
                'client':{
                    'type':'int',
                    'value':0
                },
                'items':{
                    'type':'object',
                    'value':0
                }
            }
            data = json.dumps(ngsiEvent)
            r = requests.post(url = ORION_ENDPOINT, headers = headers, data = data)
            # defining the api-endpoint
    ORION_ENDPOINT = "http://"+orionEndpoint+"/v2/entities/ticket/attrs"
    headers = {'Content-Type': 'application/json','charset': 'utf-8'}
    myquery =  { 'date': { '$gt': start, '$lt': end } } #{ "_id":14 }
    mydoc = mycol.find(myquery)
    while(True):
    #info = json.loads(json.loads(mydoc))
        for x in mydoc:
            print(x)
            print(x['_id'])
            ngsiEvent={
                       '_id':{
                           'type':'String',
                           'value':x['_id']
                       },
                       'mall': {
                           'type':'String',
                           'value':x['mall']
                       },
                       'date':{
                           'type':'date',
                           'value':x['date'].strftime('%m/%d/%Y')
                       },
                       'client':{
                           'type':'int',
                           'value':x['client']
                       },
                       'items':{
                           'type':'object',
                           'value':x['items']
                       }
                      }
            #print(ngsiEvent)
            print ("Start : %s" % time.ctime())
            time.sleep( frecuency )
            print ("End : %s" % time.ctime())
            data = json.dumps(ngsiEvent)
            #print(json.dumps(ngsiEvent))
            # sending post request and saving response as response object 
            r = requests.post(url = ORION_ENDPOINT, headers = headers, data = data) 

            # extracting response text
            #print(r)
            pastebin_url = r.text 
            print("The pastebin URL is:%s"%pastebin_url)

if __name__== "__main__":
#    parser=argparse.ArgumentParser(
#    description='''Script for collecting tickets from mongoDB ''',
#    epilog="""All's well that ends well.""")
#parser.add_argument('start', type=str, default='2016-01-14', help='Start date for getting tickets!')
#parser.add_argument('end', type=str, default='2016-01-15', help='End date for getting tickets!')
#parser.add_argument('frecuency', type=int, default=5, help='Frecuency for post the tickets to the context broker!')
#parser.add_argument('mongoURI', type=str, default="mongodb://root:example@localhost:27017/", help='mongo uri with format (mongodb://user:password@localhost:27017/) where the dataset is stored!')
#parser.add_argument('orion', type=str, default='localhost:1026', help='host and port of the context broker!')
#args=parser.parse_args()
#os.environ['START_DATE']
    start=os.environ['START_DATE']
    end=os.environ['END_DATE']
    frecuency=os.environ['FRECUENCY']
    mongoURI=os.environ['MONGO_URI']
    orion=os.environ['ORION_ENDPOINT']
    generate(start,end,int(frecuency),mongoURI,orion)
