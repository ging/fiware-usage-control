curl -v http://138.4.22.138:1026/v2/subscriptions -s -S -H 'Content-Type: application/json' -d @- <<EOF
{
  "description": "A subscription to get info about the supermarket",
  "subject": {
	"entities": [
  	{
    	"id": "ticket",
    	"type": "ticket"
  	}
	],
	"condition": {
  	"attrs": [
      "_id",
      "mall",
      "date",
      "client",
      "items"
  	]
	}
  },
  "notification": {
	"http": {
  	"url": "http://138.4.7.94:9001/notify"
	},
	"attrs": [
      "_id",
      "mall",
      "date",
      "client",
      "items"
	]
  },
  "expires": "2040-01-01T14:00:00.00Z",
  "throttling": 5
}
EOF
