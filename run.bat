@echo off
cls

java -Xss2M -jar ${project.build.finalName}-with-deps.jar
