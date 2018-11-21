#!/bin/sh
while true
do
    temp=$(shuf -i 18-53 -n 1)
	sleep 2
	curl localhost:1026/v2/entities/home-01/attrs -s -S --header 'Content-Type: application/json' \
	     -X PATCH -d '{
	  "presence": {
		"type": "Boolean",
		"value": "true"
	    },
	  "temperature": {
      	"type": "float",
      	"value": '$temp'
        }
	}'
	sleep 2
	curl localhost:1026/v2/entities/home-01/attrs -s -S --header 'Content-Type: application/json' \
	     -X PATCH -d '{
	  "presence": {
		"type": "Boolean",
		"value": "false"
	    },
                  	  "temperature": {
                        	"type": "float",
                        	"value": '$temp'
                          }
	}'
done
