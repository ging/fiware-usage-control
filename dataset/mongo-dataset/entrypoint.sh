#!/usr/bin/env bash

mongod --bind_ip 172.18.1.12 &
sleep 4
mongoimport --host 172.18.1.12 --db superMarket --collection tickets --drop --file DelightingCustomersBD.json
rm DelightingCustomersBD.json
touch /var/log/mongodb/nn.log

sleep 10 && tail -f `ls /var/log/mongodb/*.log | head -n1`