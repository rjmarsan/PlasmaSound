perl -p -i -e 's/com.rj.processing.plasmasounddonate.R/com.rj.processing.plasmasound.R/g' src/com/rj/processing/*/*.java
perl -p -i -e 's/com.rj.processing.plasmasounddonate.R/com.rj.processing.plasmasound.R/g' src/com/rj/processing/*/*/*.java
perl -p -i -e 's/com.rj.processing.plasmasounddonate.R/com.rj.processing.plasmasound.R/g' src/com/rj/processing/*/*/*/*.java
perl -p -i -e 's/com.rj.processing.plasmasounddonate.R/com.rj.processing.plasmasound.R/g' src/com/rj/processing/*/*/*/*/*.java
perl -p -i -e 's/com.rj.processing.plasmasounddonate.R/com.rj.processing.plasmasound.R/g' src/com/rj/processing/*/*/*/*/*/*.java
perl -p -i -e 's/com.rj.processing.plasmasounddonate.R/com.rj.processing.plasmasound.R/g' src/amir/android/icebreaking/*.java

./swapassets.sh nondonate
