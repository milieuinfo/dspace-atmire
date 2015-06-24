PROGR_DIR=`dirname $0`
DSPACE_BIN_DIR=`cd $PROGR_DIR ; pwd`

$DSPACE_BIN_DIR/import.sh
$DSPACE_BIN_DIR/discovery-retries.sh
