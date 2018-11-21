#!/bin/sh
curl -X POST localhost:1026/v2/entities -s -S --header 'Content-Type: application/json' -d '{
    "id": "home-01",
    "type": "home",
    "presence": {
        "type": "Boolean",
        "value": "false"
    },
    "zip": {
        "type": "String",
        "value": "28035"
    },
    "address": {
        "type": "String",
        "value": "Goya 25"
    },
    "Contact-info": {
        "type": "Object",
        "value": {
                  "name": {
                  "type": "String",
                  "value": "Rafael Rodriguez"
                  },
                 "phone": {
                  "type": "int",
                  "value": "6785924682"
                  }
         }
    },
    "temperature": {"type": "Float","value": 50,"metadata": {}}
}'

curl -X POST localhost:1026/v2/entities -s -S --header 'Content-Type: application/json' -d '{
    "id": "home-02",
    "type": "home",
    "presence": {
        "type": "Boolean",
        "value": "false"
    },
    "zip": {
        "type": "String",
        "value": "28035"
    },
    "address": {
        "type": "String",
        "value": "Goya 28"
    },
    "Contact-info": {
        "type": "Object",
        "value": {
                  "name": {
                  "type": "String",
                  "value": "Daniel Jimenez"
                  },
                 "phone": {
                  "type": "int",
                  "value": "6782923681"
                  }
         }
    },
    "temperature": {"type": "Float","value": 60,"metadata": {}}
}' 

curl -X POST localhost:1026/v2/entities -s -S --header 'Content-Type: application/json' -d '{
    "id": "home-03",
    "type": "home",
    "presence": {
        "type": "Boolean",
        "value": "false"
    },
    "zip": {
        "type": "String",
        "value": "28026"
    },
    "address": {
        "type": "String",
        "value": "Embajadores 25"
    },
    "Contact-info": {
        "type": "Object",
        "value": {
                  "name": {
                  "type": "String",
                  "value": "Lucía Lopez"
                  },
                 "phone": {
                  "type": "int",
                  "value": "6883924632"
                  }
         }
    },
    "temperature": {"type": "Float","value": 70,"metadata": {}}
}'

