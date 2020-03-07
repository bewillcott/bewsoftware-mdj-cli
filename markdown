#!/usr/bin/bash
#
# Script to run markdownj-cli.
# Put this script into a directory in your PATH.  A good one
# might be "~/bin". 
#
#
# Auth: Brad Willcott - 26/02/2020
#

# Set this to the base directory for the svnkit package
#MARKDOWNJ-CLI="/home/bwillcott/NetBeansProjects/markdownj/markdownj-cli/target"

#
# The work horse part
#
#PATH="$MARKDOWNJ-CLI:$PATH"

java --enable-preview -jar /home/bwillcott/NetBeansProjects/markdownj-cli/target/markdownj-cli-*-SNAPSHOT.jar $@
# Done

