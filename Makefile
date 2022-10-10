.PHONY: generator clean

generator:
	cd generator && mvn package && mvn install:install-file -Dfile=target/generator-0-SNAPSHOT.jar -DpomFile=pom.xml

generatedsources: generator
	mvn compile

libnetlibjavajni.dylib: generatedsources
	clang -llapack -lblas -I $(JAVA_HOME)/include/ -I netlib/JNI/ -I $(shell xcrun --show-sdk-path)/System/Library/Frameworks/Accelerate.framework/Versions/A/Frameworks/vecLib.framework/Headers/  -I $(JAVA_HOME)/include/darwin/ -I target/headers/ -shared -undefined dynamic_lookup  -arch arm64 -arch x86_64 native_system/jni-sources/target/netlib-native/com_github_fommil_netlib_NativeSystemBLAS.c native_system/jni-sources/target/netlib-native/com_github_fommil_netlib_NativeSystemLAPACK.osx.c netlib/JNI/netlib-jni.c -o libnetlibjavajni.dylib

libnetlibjavajni.x86_64.so:  generatedsources	
	docker run --rm -v `pwd`:/build $(shell docker build -q .) /bin/sh -c 'cd /build && clang -llapack -lblas -I /usr/lib/jvm/java-8-openjdk-amd64/include/ -I netlib/LAPACKE/ -I netlib/JNI/  -I /usr/lib/jvm/java-8-openjdk-amd64/include/linux/ -I target/headers/ -shared native_system/jni-sources/target/netlib-native/com_github_fommil_netlib_NativeSystemBLAS.c native_system/jni-sources/target/netlib-native/com_github_fommil_netlib_NativeSystemLAPACK.c netlib/JNI/netlib-jni.c -o libnetlibjavajni.x86_64.so'

libnetlibjavajni.aarch64.so: generatedsources	
	docker run --platform linux/arm64 --rm -v `pwd`:/build $(shell docker build --platform aarch64 -q .) /bin/sh -c 'cd /build && clang -llapack -lblas -I /usr/lib/jvm/java-8-openjdk-arm64/include/ -I netlib/LAPACKE/ -I netlib/JNI/  -I /usr/lib/jvm/java-8-openjdk-arm64/include/linux/ -I target/headers/ -shared native_system/jni-sources/target/netlib-native/com_github_fommil_netlib_NativeSystemBLAS.c native_system/jni-sources/target/netlib-native/com_github_fommil_netlib_NativeSystemLAPACK.c netlib/JNI/netlib-jni.c -o libnetlibjavajni.aarch64.so'

publishLocal: libnetlibjavajni.dylib libnetlibjavajni.aarch64.so libnetlibjavajni.x86_64.so
	cp core/target/generated-sources/netlib-java/io/github/pityka/netlib/*java sbt/core/src/main/java/ && \
	cp native_system/java/target/generated-sources/netlib-java/io/github/pityka/netlib/*java sbt/core/src/main/java/ && \
	cp libnetlibjavajni.dylib sbt/jni-osx/src/main/resources/ && \
	cp libnetlibjavajni.x86_64.so sbt/jni-linux/src/main/resources/ && \
	cp libnetlibjavajni.aarch64.so sbt/jni-linux/src/main/resources/ && \
	cd sbt && sbt publishLocal

publish: test
	cd sbt && sbt publishSigned && sbt sonatypeRelease

test-osx: publishLocal	
	cd sbt && sbt test/run

test-linux-arm64: publishLocal	
	docker run --platform linux/arm64 --rm -v `pwd`:/build $(shell docker build --platform aarch64 -q .) /bin/sh -c 'cd /build/sbt && /sbt test/run'

test-linux-amd64:  publishLocal	
	docker run --rm -v `pwd`:/build $(shell docker build -q .) /bin/sh -c 'cd /build/sbt && /sbt test/run'

test: test-linux-amd64 test-osx test-linux-arm64

clean:
	rm -f libnetlibjavajni.dylib
	rm -f libnetlibjavajni.*.so
	mvn clean 
	cd sbt && sbt clean 
