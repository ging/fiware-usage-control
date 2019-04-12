#!/usr/bin/env bash

mongod &
sleep 4
mongo use superMarket exit;
mongoimport --db superMarket --collection tickets --drop --file DelightingCustomersBD.json
rm DelightingCustomersBD.json
sleep 10 && tail -f `ls /var/log/mongodb/mongodb*.log | head -n1`