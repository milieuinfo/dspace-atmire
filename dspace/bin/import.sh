#!/bin/bash
PROGR_DIR=`dirname $0`
DSPACE_BIN_DIR=`cd $PROGR_DIR ; pwd`
DSPACE_CONF_DIR=`cd $PROGR_DIR/../config ; pwd`

THREADS_FILE="/tmp/threads.txt"

DMS_EXPORT_DIR="/opt/tomcat/dms-export"
OUTPUT_DIR="/opt/tomcat/data/dms-export/output"
LOG_DIR="/opt/tomcat/data/import-logs/"
HANDLE="acd/1"

cd


function process_year () {
year=$1

echo "start $year"
date

sudo -u tomcat $DSPACE_BIN_DIR/dspace-high-mem dsrun com.atmire.dspace.BulkUploadRecords -d "$DMS_EXPORT_DIR/$year" -v -c $HANDLE -x "$DSPACE_CONF_DIR/bulkuploadRecords.xsl" -o "$OUTPUT_DIR/$year" -r noindex > /tmp/import-$year.log 2>&1

sudo cp /tmp/import-$year.log $LOG_DIR
rm /tmp/import-$year.log


sleep $(( $year - 2000 ))
echo "end $year"
date

}

threads=0

echo $threads > $THREADS_FILE
for (( i=2013; i>=2004; i-- ))
do
	while [ `cat $THREADS_FILE` -ge 3 ]
	do
		sleep 60
		#echo $threads
	done
	threads=`cat $THREADS_FILE`
	threads=$(( $threads + 1 ))
	echo $threads > $THREADS_FILE
	(process_year $i; threads=`cat $THREADS_FILE`; threads=$(( $threads - 1 )); echo $threads > $THREADS_FILE; ) &
	sleep .3
done

rm $THREADS_FILE
