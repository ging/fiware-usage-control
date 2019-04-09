#!/bin/bash
# Start the flink cluster
# jobmanager.sh start cluster


forever start oauth2-example-client/server.js

while [ ! -f /etc/oauth/access_token ]
do
sleep 1
done

source ./etc/oauth/access_token

echo "Configuring Job Manager on this node"
#sed -i -e "s/%jobmanager%/`hostname -i`/g" /usr/local/flink/conf/flink-conf.yaml
sed -i -e "s/jobmanager.rpc.address: localhost/jobmanager.rpc.address: `hostname -i`/g" /usr/local/flink/conf/flink-conf.yaml
echo "blob.server.port: 6124" >> /usr/local/flink/conf/flink-conf.yaml
echo "query.server.port: 6125" >> /usr/local/flink/conf/flink-conf.yaml
echo "env.java.opts: -Dfluent.host=${FLUENT_HOST} -Dfluent.port=${FLUENT_PORT} -Daccess.token=${ACCESS_TOKEN}" >> /usr/local/flink/conf/flink-conf.yaml

/usr/local/flink/bin/jobmanager.sh start #cluster #local
echo "Cluster started."

#sleep infinity
# Start the web client -- for newest version 1.x  of Flink is integrated on Flink Dashboard
#/usr/local/flink/bin/start-webclient.sh

#mkdir -p $FLINK_MASTER_LOG

echo "Config file: " && grep '^[^\n#]' /usr/local/flink/conf/flink-conf.yaml
echo "Sleeping 10 seconds, then start to tail the log file"
sleep 10 && tail -f `ls /usr/local/flink/log/*.log | head -n1`

tail -f /usr/local/flink/log/flink--jobmanager-flink.log
tail -f `find /usr/local/flink/log/ -name *jobmanager*.out`
