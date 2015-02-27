#!/bin/bash

# USAGE: ./bulk-upload.sh <export_dir> <collection_id>
# eg. ./bulk-upload.sh /path/to/dms-export acd/7
# ./bulk-upload.sh /Users/janlievens/Code/LNE/dspace-atmire/data acd/1

# TODO jalie ansiblize the path/to/dspace-high-mem

for i in 2004 2005 2006 2007 2008 2009 2010 2011 2012 2013
do
    /Users/janlievens/Programs/apache-tomcat-7.0.55-dspace/dspace/bin/dspace-high-mem dsrun com.atmire.dspace.BulkUploadIMJV -d $1/$i -s milieuverslag.xsd -v -c $2 > ./import.$i.log
done