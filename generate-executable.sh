#!/bin/bash

# Fail on error
set -e

echo "Build nicokosi/hubstats JAR file:"
lein uberjar

echo "Install GraalVM via SDKMAN!:"
curl --silent "https://get.sdkman.io" | bash || echo 'SDKMAN! already installed'
source "$HOME/.sdkman/bin/sdkman-init.sh"
GRAALVM_VERSION=21.3.0.r17-grl

sdkman_auto_answer=true \
    sdkman_auto_selfupdate=tr \
    sdk install java $GRAALVM_VERSION > /dev/null \
    || echo "GraalVM $GRAALVM_VERSION already installed."
sdk use java $GRAALVM_VERSION

echo "Build executable from JAR via GraalVM:"
gu install native-image && \
native-image \
    --allow-incomplete-classpath \
    --initialize-at-build-time \
    --no-fallback \
    --no-server \
    --report-unsupported-elements-at-runtime \
    -Dclojure.compiler.direct-linking=true \
    -H:EnableURLProtocols=https \
    -H:+ReportExceptionStackTraces \
    -jar ./target/hubstats-0.1.0-SNAPSHOT-standalone.jar \
    hubstats

echo "Executable has been built! ✅

It can be run this way:

 # Show last 10 days stats for GitHub repository docker/containerd:
 $ ./hubstats --organization docker --repository containerd --since-days 10

 # Show all parameters:
 $ ./hubstats"
