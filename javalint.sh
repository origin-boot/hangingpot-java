#!/bin/bash

CS_VERSION="10.17.0"
CS_JAR="checkstyle-${CS_VERSION}-all.jar"
CS_CONFIG="google_checks.xml"

if [ ! -f "$CS_CONFIG" ]; then
    echo "Checked that ${CS_CONFIG} does not exist and is downloading..."
    wget -q https://raw.githubusercontent.com/checkstyle/checkstyle/checkstyle-"$CS_VERSION"/src/main/resources/"$CS_CONFIG"
    if [ $? -ne 0 ]; then
        echo "Download failed, please check whether the network connection or URL is correct."
        exit 1
    fi
fi


if [ ! -f "$CS_JAR" ]; then
    echo "Checked that ${CS_JAR} does not exist and is downloading..."
    wget -q https://github.com/checkstyle/checkstyle/releases/download/checkstyle-"$CS_VERSION"/"$CS_JAR"
    if [ $? -ne 0 ]; then
        echo "Download failed, please check whether the network connection or URL is correct."
        exit 1
    fi
fi

params=("$@")

if [ ${#params[@]} -eq 0 ]; then
    echo "No parameters are provided, please specify the Java source file to be checked."
    exit 1
fi

LOGS=$( java -jar "$CS_JAR" -c "$CS_CONFIG" "${params[@]}" )
echo "$LOGS"

if echo "$LOGS" | grep -q "\["; then
    echo "Error or warning detected in the logs."
    exit 1
else
    echo "No errors or warnings detected."
fi
