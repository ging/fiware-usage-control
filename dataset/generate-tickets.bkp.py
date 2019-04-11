#!/usr/bin/python3
import sys
import pymongo
import requests
import json
import time
import argparse
from datetime import datetime

def generate (startDate, finishDate, frecuency, orionEndpoint):
    if orionEndpoint is None:
        orionEndpoint="localhost:1026"
    elif startDate is None:
        startDate="2016-01-14"
    elif finishDate is None:
        finishDate="2016-01-15"
    elif frecuency is None:
        frecuency=5
    sDateSplit = startDate.split("-")
    fDateSplit = finishDate.split("-")
    start = datetime(int(sDateSplit[0]), int(sDateSplit[1]), int(sDateSplit[2]))
    end = datetime(int(fDateSplit[0]), int(fDateSplit[1]), int(fDateSplit[2]))
    myclient = pymongo.MongoClient("mongodb://root:example@localhost:27017/")
    mydb = myclient["superMarket"]
    mycol = mydb["tickets"]
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
    parser=argparse.ArgumentParser(
        description='''Script for collecting tickets from mongoDB ''',
        epilog="""All's well that ends well.""")
parser.add_argument('start', type=str, default='2016-01-14', help='Start date for getting tickets!')
parser.add_argument('end', type=str, default='2016-01-15', help='End date for getting tickets!')
parser.add_argument('frecuency', type=int, default=5, help='Frecuency for post the tickets to the context broker!')
parser.add_argument('--orion', type=str, default='localhost:1026', help='host and port of the context broker!')
args=parser.parse_args()
generate(args.start,args.end,args.frecuency,args.orion)