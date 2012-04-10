perl -p -i -e 's/com.rj.processing.plasmasound.R/com.rj.processing.plasmasounddonate.R/g' src/com/rj/processing/*/*.java
perl -p -i -e 's/com.rj.processing.plasmasound.R/com.rj.processing.plasmasounddonate.R/g' src/com/rj/processing/*/*/*.java
perl -p -i -e 's/com.rj.processing.plasmasound.R/com.rj.processing.plasmasounddonate.R/g' src/com/rj/processing/*/*/*/*.java
perl -p -i -e 's/com.rj.processing.plasmasound.R/com.rj.processing.plasmasounddonate.R/g' src/com/rj/processing/*/*/*/*/*.java
perl -p -i -e 's/com.rj.processing.plasmasound.R/com.rj.processing.plasmasounddonate.R/g' src/com/rj/processing/*/*/*/*/*/*.java
perl -p -i -e 's/com.rj.processing.plasmasound.R/com.rj.processing.plasmasounddonate.R/g' src/amir/android/icebreaking/*.java

./swapassets.sh donate
