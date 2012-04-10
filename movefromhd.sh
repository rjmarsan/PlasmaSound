perl -p -i -e 's/package="com.rj.processing.plasmasoundhd"/package="com.rj.processing.plasmasound"/g' AndroidManifest.xml
perl -p -i -e 's/com.rj.processing.plasmasoundhd.R/com.rj.processing.plasmasound.R/g' src/com/rj/processing/*/*.java
perl -p -i -e 's/com.rj.processing.plasmasoundhd.R/com.rj.processing.plasmasound.R/g' src/com/rj/processing/*/*/*.java
perl -p -i -e 's/com.rj.processing.plasmasoundhd.R/com.rj.processing.plasmasound.R/g' src/com/rj/processing/*/*/*/*.java
perl -p -i -e 's/com.rj.processing.plasmasoundhd.R/com.rj.processing.plasmasound.R/g' src/amir/android/icebreaking/*.java
