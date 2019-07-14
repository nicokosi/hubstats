#!/bin/bash

# Fail on error
set -e

echo "Build nicokosi/hubstats JAR file:"
lein uberjar

echo "Install GraalVM via SDKMAN!:"
curl --silent "https://get.sdkman.io" | bash || echo 'SDKMAN! already installed'
source "$HOME/.sdkman/bin/sdkman-init.sh"
GRAALVM_VERSION=19.1.0-grl
sdkman_auto_answer=true sdk install java $GRAALVM_VERSION > /dev/null
sdk use java $GRAALVM_VERSION

echo "Build executable from JAR via GraalVM:"
gu install native-image && \
native-image \
    --allow-incomplete-classpath \
    --no-fallback \
    --initialize-at-build-time \
    --no-server \
    --report-unsupported-elements-at-runtime \
    -Dclojure.compiler.direct-linking=true \
    -H:+ReportExceptionStackTraces \
    -jar ./target/hubstats-0.1.0-SNAPSHOT-standalone.jar \
    hubstats.core

echo "Executable has been built! ✅

It can be run this way:

 # Show last 10 days stats for GitHub repository docker/containerd:
 $ ./hubstats.core --organization docker --repository containerd --since-days 10

 # Show all parameters:
 $ ./hubstats.core"