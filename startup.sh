#! /bin/sh

files=$(ls sm-*)
for file in $files
do
  echo "Start: " $file
  out=${file}.out
  nohup java -XX:+UseSerialGC -jar $file > $out &
done
