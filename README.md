# godot-kotlin-jvm

create jre:  
- navigate to root of sample project  
- run `jlink --add-modules java.base,jdk.jdwp.agent --output jre` for debug and `jlink --add-modules java.base --output jre` for release