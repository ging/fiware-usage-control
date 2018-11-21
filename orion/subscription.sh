#!/bin/sh
curl -v localhost:1026/v2/subscriptions -s -S --header 'Content-Type: application/json' \
    -d @- <<EOF
{
  "description": "A subscription to get info about Home-01-02-03",
  "subject": {
    "entities": [
      {
        "id": "home-01",
        "type": "home"
      },
      {
        "id": "home-02",
        "type": "home"
      },
      {
        "id": "home-03",
        "type": "home"
      }
    ],
    "condition": {
      "attrs": [
        "presence"
      ]
    }
  },
  "notification": {
    "http": {
      "url": "http://138.4.7.110:9001"
    },
    "attrs": [
      "presence",
      "zip",
      "temperature"
    ]
  },
  "expires": "2040-01-01T14:00:00.00Z",
  "throttling": 5
}
EOF
