#!/bin/sh

set -e

lein uberjar
cat stub.sh target/hubstats-*-standalone.jar > $1
chmod +x $1
echo "Generated executable: $PWD/$1"
