#!/bin/bash
while true; do 
	iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to 9999
	cd /root/128Project/wizard
	git pull
	mvn jetty:run
	sleep 1200
	killall java
done
