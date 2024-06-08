#!/bin/bash

MODULE=""
PROJECT=""

while getopts ":m:p:" opt; do
  case $opt in
    m)
      MODULE=$OPTARG
      ;;
    p)
      PROJECT=$OPTARG
      ;;
    \?)
      echo "Invalid option: -$OPTARG" >&2
      exit 1
      ;;
    :)
      echo "Option -$OPTARG requires an argument." >&2
      exit 1
      ;;
  esac
done

if [ -z "$MODULE" ]; then
    echo "Error: Module name is required."
    exit 1
fi

if [ -z "$PROJECT" ]; then
    echo "Error: Project name is required."
    exit 1
fi


cd $MODULE
nohup java -jar target/"${PROJECT}-0.0.1-SNAPSHOT.jar" --spring.config.location=file:config/ci.properties > app.log 2>&1 &

sleep 30

echo "Contents of app.log:"
cat app.log

if grep -i -q "Started ${PROJECT}Application" app.log; then
    echo "Application started successfully."
    exit 0
else
    echo "Application did not start successfully."
    exit 1
fi
