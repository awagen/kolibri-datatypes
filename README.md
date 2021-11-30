# DEPRECATED - Kolibri Datatypes

Note: the current repository was moved to ```https://github.com/awagen/kolibri``` to have one repo for all
Kolibri projects. Please check the new repo location for updates.

![Alt text](images/kolibri.svg?raw=true "Kolibri Datatypes")

# Documentation
Documentation of the provided structures can be found here: \
https://awagen.github.io/kolibri/kolibri-datatypes/

# Build
- ```sbt clean assembly```
  - this puts the respective jar within target/scala-x.xx/ (with the name as provided in 'assemblyJarName in assembly' 
  property in sbt) (https://github.com/sbt/sbt-assembly)
  
# Publish Locally
- ```sbt publishLocal```