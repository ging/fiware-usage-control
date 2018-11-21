#!/bin/sh
while true
do
	sleep 5
	curl localhost:1026/v2/entities/home-03/attrs -s -S --header 'Content-Type: application/json' \
	     -X PATCH -d '{
	  "presence": {
		"type": "Boolean",
		"value": "true"
	    }
	}'
	sleep 5
	curl localhost:1026/v2/entities/home-03/attrs -s -S --header 'Content-Type: application/json' \
	     -X PATCH -d '{
	  "presence": {
		"type": "Boolean",
		"value": "false"
	    }
	}'
done
