perl -p -i -e 's/package="com.rj.processing.plasmasounddonate"/package="com.rj.processing.plasmasound"/g' AndroidManifest.xml
perl -p -i -e 's/com.rj.processing.plasmasounddonate.R/com.rj.processing.plasmasound.R/g' src/com/rj/processing/*/*.java
perl -p -i -e 's/com.rj.processing.plasmasounddonate.R/com.rj.processing.plasmasound.R/g' src/com/rj/processing/*/*/*.java
perl -p -i -e 's/com.rj.processing.plasmasounddonate.R/com.rj.processing.plasmasound.R/g' src/com/rj/processing/*/*/*/*.java
perl -p -i -e 's/com.rj.processing.plasmasounddonate.R/com.rj.processing.plasmasound.R/g' src/amir/android/icebreaking/*.java
perl -p -i -e 's/<bool name="should_bug_about_donate">false<\/bool>/<bool name="should_bug_about_donate">true<\/bool>/g' res/values/properties.xml

./swapassets.sh nondonate
