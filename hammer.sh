#!/bin/bash

for F in $(seq 10000); do
    TMP=$(mktemp)
    cp packet.template $TMP
    sed -i "s;{LENGTH};100;g" $TMP
    sed -i "s;{PACKET};$F;g" $TMP

    RESP=$(cat $TMP | netcat localhost 8080)

    echo "RESP: $RESP"
done
