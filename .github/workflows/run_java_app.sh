#!/bin/bash

cd server
nohup java -jar target/hangingpot-0.0.1-SNAPSHOT.jar --spring.config.location=file:config/ci.properties > app.log 2>&1 &

sleep 30

echo "Contents of app.log:"
cat app.log

if grep -q "Started HangingpotApplication" app.log; then
    echo "Application started successfully."
    exit 0
else
    echo "Application did not start successfully."
    exit 1
fi
