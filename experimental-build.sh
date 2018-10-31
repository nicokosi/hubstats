#!/bin/bash

# Fail on error
set -e

echo ' => Build the JAR file'
lein uberjar

echo ' => Build the executable from JAR inside a Docker container'
docker pull danny02/graalvm
docker run \
    --rm \
    --interactive \
    --volume $(pwd):/app \
    danny02/graalvm \
    sh -c "native-image \
            -jar /app/target/hubstats-0.1.0-SNAPSHOT-standalone.jar \
            -H:+ReportUnsupportedElementsAtRuntime \
            hubstats.core && \
        echo ' => Check the executable: ' && ./hubstats.core || \
        echo ' => Copy it to current directory: ' && cp hubstats.core /app/"