#!/usr/bin/bash
#
# Script to run markdownj-cli.
# Put this script into a directory in your PATH.  A good one
# might be "~/bin". 
#
#
# Auth: Brad Willcott - 9/03/2020
#

java --enable-preview -jar /home/bwillcott/NetBeansProjects/markdownj-cli/target/markdownj-cli-*SNAPSHOT.jar "$@"
# Done
