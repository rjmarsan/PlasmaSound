perl -p -i -e 's/package="com.rj.processing.plasmasound"/package="com.rj.processing.plasmasoundhd"/g' AndroidManifest.xml
perl -p -i -e 's/com.rj.processing.plasmasound.R/com.rj.processing.plasmasoundhd.R/g' src/com/rj/processing/*/*.java
perl -p -i -e 's/com.rj.processing.plasmasound.R/com.rj.processing.plasmasoundhd.R/g' src/com/rj/processing/*/*/*.java
perl -p -i -e 's/com.rj.processing.plasmasound.R/com.rj.processing.plasmasoundhd.R/g' src/com/rj/processing/*/*/*/*.java
perl -p -i -e 's/com.rj.processing.plasmasound.R/com.rj.processing.plasmasoundhd.R/g' src/amir/android/icebreaking/*.java
