#!/bin/bash
docker build -t "nicokosi/hubstats" .
docker run --rm -it "nicokosi/hubstats"
docker inspect "nicokosi/hubstats" | jq .[0].Size
