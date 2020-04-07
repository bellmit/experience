#!/bin/bash
cd ..
LIB=./lib
CLASSPATH=$CLASSPATH:./etc
for jar in $LIB/*.*
do
CLASSPATH=$CLASSPATH:$jar
done
echo $CLASSPATH
OPTS_1=-server -Xms256m -Xmx512m
OPTS_2=-Dcom.sun.management.jmxremote.port=1099
OPTS_3=-Dcom.sun.management.jmxremote.ssl=false
OPTS_4=-Dcom.sun.management.jmxremote.authenticate=false
OPTS_5=-Djava.rmi.server.hostname=localhost
OPTS=$OPTS_1 $OPTS_2 $OPTS_3 $OPTS_4 $OPTS_5
nohup java -cp ${CLASSPATH} ${OPTS} com.excenergy.tagdataserv.Bootstrap ./log/main.log &
