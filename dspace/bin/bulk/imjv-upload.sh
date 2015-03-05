#!/bin/bash

# USAGE: ./bulk-upload.sh <export_dir> <collection_id> <year>
# eg. ./bulk-upload.sh /path/to/dms-export acd/7 2004

%tomcat_data_dir%/dspace/bin/dspace-high-mem dsrun com.atmire.dspace.BulkUploadIMJV -d $1/$3 -s milieuverslag.xsd -v -c $2 > ./import.$3.log
