FROM danny02/graalvm

COPY target/classes /target
WORKDIR /target
RUN native-image hubstats.core

FROM scratch

COPY --from=BASE /lib64/libc.so.6 /lib64/libc.so.6
COPY --from=BASE /lib64/libdl.so.2 /lib64/libdl.so.2
COPY --from=BASE /lib64/libpthread.so.0 /lib64/libpthread.so.0
COPY --from=BASE /lib64/libz.so.1 /lib64/libz.so.1
COPY --from=BASE /lib64/librt.so.1 /lib64/librt.so.1
COPY --from=BASE /lib64/libcrypt.so.1 /lib64/libcrypt.so.1
COPY --from=BASE /lib64/ld-linux-x86-64.so.2 /lib64/ld-linux-x86-64.so.2
COPY --from=BASE /lib64/libfreebl3.so /lib64/libfreebl3.so

COPY --from=BASE /target/hubstats.core /

CMD ["/hubstats.core"]
