#!/bin/sh
#JAVA_OPTS="-Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=4000,suspend=y -Dlogging.dir=/Users/lds/Projects/Dspace/run/tomcat/logs"
#JAVA_OPTS="-Dlogging.dir=/Users/lds/Projects/Dspace/run/tomcat/logs"
###########################################################################
#
# dspace
#
# Version: $Revision$
#
# Date: $Date$
#
# Copyright (c) 2002-2009, Duraspace.  All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are
# met:
#
# - Redistributions of source code must retain the above copyright
# notice, this list of conditions and the following disclaimer.
#
# - Redistributions in binary form must reproduce the above copyright
# notice, this list of conditions and the following disclaimer in the
# documentation and/or other materials provided with the distribution.
#
# - Neither the name of the Duraspace nor the names of its
# contributors may be used to endorse or promote products derived from
# this software without specific prior written permission.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
# ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
# LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
# A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
# HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
# INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
# BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
# OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
# ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
# TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
# USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
# DAMAGE.
#
###########################################################################

#
# This is a simple shell script for running a command-line DSpace tool.
# It sets the CLASSPATH appropriately before invoking Java.

BINDIR=`dirname $0`
DSPACEDIR=`cd "$BINDIR/.." ; pwd`

# Get the JARs in $DSPACEDIR/jsp/WEB-INF/lib, separated by ':'
JARS=`echo $DSPACEDIR/lib/*.jar | sed 's/ /\:/g'`

# Class path for DSpace will be:
#   Any existing classpath
#   The JARs (WEB-INF/lib/*.jar)
#   The WEB-INF/classes directory
if [ "$CLASSPATH" = "" ]; then
    FULLPATH=$JARS:$DSPACEDIR/config
else
    FULLPATH=$CLASSPATH:$JARS:$DSPACEDIR/config
fi


# If the user only wants the CLASSPATH, just give it now.
if [ "$1" = "classpath" ]; then
  echo $FULLPATH
  exit 0
fi



#Allow user to specify java options through JAVA_OPTS variable
if [ "$JAVA_OPTS" = "" ]; then
  #Default Java to use 256MB of memory
  JAVA_OPTS=-Xmx256m
fi

# Now invoke Java

EXTRA_OPTIONS="-Dlog4j.configuration=$DSPACEDIR/config/log4j.properties -Ddspace.log.init.disable=true -Ddspace.configuration=$DSPACEDIR/config/dspace.cfg" 

JAVA_OPTS="$JAVA_OPTS $EXTRA_OPTIONS"

java $JAVA_OPTS -classpath $FULLPATH org.dspace.administer.RegistryLoader -bitstream $DSPACEDIR/config/registries/bitstream-formats.xml

java $JAVA_OPTS -classpath $FULLPATH org.dspace.administer.MetadataImporter -f $DSPACEDIR/config/registries/dublin-core-types.xml

java $JAVA_OPTS -classpath $FULLPATH org.dspace.administer.MetadataImporter -f $DSPACEDIR/config/registries/dcterms-types.xml

java $JAVA_OPTS -classpath $FULLPATH org.dspace.administer.MetadataImporter -f $DSPACEDIR/config/registries/sword-metadata.xml

java $JAVA_OPTS -classpath $FULLPATH org.dspace.administer.MetadataImporter -f $DSPACEDIR/config/registries/imjv-types.xml

java $JAVA_OPTS -classpath $FULLPATH org.dspace.administer.MetadataImporter -f $DSPACEDIR/config/registries/kbo-types.xml


