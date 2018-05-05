#!/bin/bash

TEMP_DIR="tmp/hubstats"

cleanup() {
   if [ -e ${TEMP_DIR} ]; then
      rm -rf ${TEMP_DIR}
   fi
   exit
}
trap cleanup INT TERM EXIT

mkdir -p ${TEMP_DIR}/classes
unzip -q target/hubstats-*-standalone.jar -d ${TEMP_DIR}/classes

for archive in \
  org/clojure/clojure/1.9.0/clojure-1.9.0.jar \
  org/clojure/core.specs.alpha/0.1.24/core.specs.alpha-0.1.24.jar \
  org/clojure/spec.alpha/0.1.143/spec.alpha-0.1.143.jar ; do
  (
    cd ${TEMP_DIR}/classes && \
    jar xf ~/.m2/repository/$archive
  )
done

docker build -t "nicokosi/hubstats" .

docker run --rm -it "nicokosi/hubstats"

docker inspect "nicokosi/hubstats" | jq .[0].Size
