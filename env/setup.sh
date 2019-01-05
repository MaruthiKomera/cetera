#!/bin/bash
set -vx

if [ "$#" -ne 2 ]; then
    echo "usage $(basename $0): <oracle-rpm> <oracle-jar>"
    exit 1
fi

if [ ! -s "$1" ] || [ ! -s "$2" ]; then
    echo "$1 or $2 don't exist"
    exit 2
fi

rpm=$1
jdbc=$2

ln -f $rpm ora/modules/oracle/files || ls -l ora/modules/oracle/files/$(basename $rpm)

ln -f $jdbc ora/oracle-jdbc || ls -l ora/oracle-jdbc/$(basename $jdbc)


