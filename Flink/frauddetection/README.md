Running the project in an IDE may result in a java.lang.NoClassDefFoundError exception. This is probably because you do not have all required Flink dependencies implicitly loaded into the classpath.

IntelliJ IDEA: Go to Run > Edit Configurations > Modify options > Select include dependencies with "Provided" scope. This run configuration will now include all required classes to run the application from within the IDE.

勾选对应的 provided scope