#!bin/sh
#git clone
kill -9 `ps x | grep java | grep spring-boot | grep food | grep -v grep | awk '{print $1}'`
nohup /usr/bin/mvn spring-boot:run &