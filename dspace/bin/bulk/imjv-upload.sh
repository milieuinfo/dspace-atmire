#!/bin/bash

# USAGE: ./bulk-upload.sh <export_dir> <collection_id>
# eg. ./bulk-upload.sh /path/to/dms-export acd/7

for i in 2004 2005 2006 2007 2008 2009 2010 2011 2012 2013
do
    %tomcat_data_dir%/dspace/bin/dspace-high-mem dsrun com.atmire.dspace.BulkUploadIMJV -d $1/$i -s milieuverslag.xsd -v -c $2 > ./import.$i.log
done