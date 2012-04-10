perl -p -i -e 's/package="com.rj.processing.plasmasound"/package="com.rj.processing.plasmasounddonate"/g' AndroidManifest.xml
perl -p -i -e 's/com.rj.processing.plasmasound.R/com.rj.processing.plasmasounddonate.R/g' src/com/rj/processing/*/*.java
perl -p -i -e 's/com.rj.processing.plasmasound.R/com.rj.processing.plasmasounddonate.R/g' src/com/rj/processing/*/*/*.java
perl -p -i -e 's/com.rj.processing.plasmasound.R/com.rj.processing.plasmasounddonate.R/g' src/com/rj/processing/*/*/*/*.java
perl -p -i -e 's/com.rj.processing.plasmasound.R/com.rj.processing.plasmasounddonate.R/g' src/amir/android/icebreaking/*.java
perl -p -i -e 's/<bool name="should_bug_about_donate">true<\/bool>/<bool name="should_bug_about_donate">false<\/bool>/g' res/values/properties.xml

./swapassets.sh donate
