curl -v -s -S -X POST http://localhost:8092 \
--header 'Content-Type: application/json; charset=utf-8' \
--header 'Accept: application/json' \
--header 'User-Agent: orion/0.10.0' \
--header "Fiware-Service: demo" \
--header "Fiware-ServicePath: /test" \
-d  '
    {
      "id": "alex",
      "previousJobId": "9dd4362a8149234ecc2c7654ac1657ac",
      "policies": [
           {
            "rule": {
              "type": "COUNT_POLICY" ,
                "numMaxEvents": 200,   
                "eventWindow": 15000
            },
            "punishment": {
              "type": "KILL_JOB"
            },
            "from": "12:00", 
            "to": "14:00",
            "within":true
     
            },
            {
            "rule": {
              "type": "AGGREGATION_POLICY" ,

                "aggregateTime": 10000 
            },
            "punishment": {
              "type": "UNSUBSCRIBE"
            } 
             
            }

        ]
    }
'