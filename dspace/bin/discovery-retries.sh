PROGR_DIR=`dirname $0`
DSPACE_BIN_DIR=`cd $PROGR_DIR ; pwd`

LOGFILE="/tmp/discovery.out"

echo "Logs are written to $LOGFILE"

sudo $DSPACE_BIN_DIR/dspace-high-mem index-discovery > $LOGFILE 2>&1
date >> $LOGFILE
sudo -u tomcat /etc/init.d/acdtomcat7 stop
sleep 15
sudo -u tomcat /etc/init.d/acdtomcat7 start
sleep 300
sudo $DSPACE_BIN_DIR/dspace-high-mem index-discovery >> $LOGFILE 2>&1
date >> $LOGFILE
sudo -u tomcat /etc/init.d/acdtomcat7 stop
sleep 15
sudo -u tomcat /etc/init.d/acdtomcat7 start
sleep 300
sudo $DSPACE_BIN_DIR/dspace-high-mem index-discovery >> $LOGFILE 2>&1
date >> $LOGFILE
sudo -u tomcat /etc/init.d/acdtomcat7 stop
sleep 15
sudo -u tomcat /etc/init.d/acdtomcat7 start
sleep 300
sudo $DSPACE_BIN_DIR/dspace-high-mem index-discovery >> $LOGFILE 2>&1
date >> $LOGFILE
sudo -u tomcat /etc/init.d/acdtomcat7 stop
sleep 15
sudo -u tomcat /etc/init.d/acdtomcat7 start
sleep 300
sudo $DSPACE_BIN_DIR/dspace-high-mem index-discovery >> $LOGFILE 2>&1
date >> $LOGFILE
sudo -u tomcat /etc/init.d/acdtomcat7 stop
sleep 15
sudo -u tomcat /etc/init.d/acdtomcat7 start
sleep 300
sudo $DSPACE_BIN_DIR/dspace-high-mem index-discovery >> $LOGFILE 2>&1
date >> $LOGFILE
sudo -u tomcat /etc/init.d/acdtomcat7 stop
sleep 15
sudo -u tomcat /etc/init.d/acdtomcat7 start
sleep 300
sudo $DSPACE_BIN_DIR/dspace-high-mem index-discovery >> $LOGFILE 2>&1
date >> $LOGFILE
sudo -u tomcat /etc/init.d/acdtomcat7 stop
sleep 15
sudo -u tomcat /etc/init.d/acdtomcat7 start
sleep 300
sudo $DSPACE_BIN_DIR/dspace-high-mem index-discovery >> $LOGFILE 2>&1
date >> $LOGFILE
sudo -u tomcat /etc/init.d/acdtomcat7 stop
sleep 15
sudo -u tomcat /etc/init.d/acdtomcat7 start
sleep 300
sudo $DSPACE_BIN_DIR/dspace-high-mem index-discovery >> $LOGFILE 2>&1
date >> $LOGFILE
sudo -u tomcat /etc/init.d/acdtomcat7 stop
sleep 15
sudo -u tomcat /etc/init.d/acdtomcat7 start
sleep 300
sudo $DSPACE_BIN_DIR/dspace-high-mem index-discovery >> $LOGFILE 2>&1
date >> $LOGFILE
sudo -u tomcat /etc/init.d/acdtomcat7 stop
sleep 15
sudo -u tomcat /etc/init.d/acdtomcat7 start
sleep 300
sudo $DSPACE_BIN_DIR/dspace-high-mem index-discovery >> $LOGFILE 2>&1
date >> $LOGFILE
sudo -u tomcat /etc/init.d/acdtomcat7 stop
sleep 15
sudo -u tomcat /etc/init.d/acdtomcat7 start
sleep 300
sudo $DSPACE_BIN_DIR/dspace-high-mem index-discovery >> $LOGFILE 2>&1
date >> $LOGFILE
sudo -u tomcat /etc/init.d/acdtomcat7 stop
sleep 15
sudo -u tomcat /etc/init.d/acdtomcat7 start
sleep 300
sudo $DSPACE_BIN_DIR/dspace-high-mem index-discovery >> $LOGFILE 2>&1
date >> $LOGFILE
sudo -u tomcat /etc/init.d/acdtomcat7 stop
sleep 15
sudo -u tomcat /etc/init.d/acdtomcat7 start
sleep 300
sudo $DSPACE_BIN_DIR/dspace-high-mem index-discovery >> $LOGFILE 2>&1
date >> $LOGFILE
sudo -u tomcat /etc/init.d/acdtomcat7 stop
sleep 15
sudo -u tomcat /etc/init.d/acdtomcat7 start
sleep 300
sudo $DSPACE_BIN_DIR/dspace-high-mem index-discovery >> $LOGFILE 2>&1
date >> $LOGFILE
sudo -u tomcat /etc/init.d/acdtomcat7 stop
sleep 15
sudo -u tomcat /etc/init.d/acdtomcat7 start
sleep 300
sudo $DSPACE_BIN_DIR/dspace-high-mem index-discovery >> $LOGFILE 2>&1
date >> $LOGFILE
sudo -u tomcat /etc/init.d/acdtomcat7 stop
sleep 15
sudo -u tomcat /etc/init.d/acdtomcat7 start
sleep 300
sudo $DSPACE_BIN_DIR/dspace-high-mem index-discovery >> $LOGFILE 2>&1
date >> $LOGFILE
sudo -u tomcat /etc/init.d/acdtomcat7 stop
sleep 15
sudo -u tomcat /etc/init.d/acdtomcat7 start
sleep 300
sudo $DSPACE_BIN_DIR/dspace-high-mem index-discovery >> $LOGFILE 2>&1
date >> $LOGFILE
sudo -u tomcat /etc/init.d/acdtomcat7 stop
sleep 15
sudo -u tomcat /etc/init.d/acdtomcat7 start
sleep 300
sudo $DSPACE_BIN_DIR/dspace-high-mem index-discovery >> $LOGFILE 2>&1
date >> $LOGFILE
sudo -u tomcat /etc/init.d/acdtomcat7 stop
sleep 15
sudo -u tomcat /etc/init.d/acdtomcat7 start
sleep 300
