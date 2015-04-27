#!/bin/bash -e
# CAUTION: this script assumes the following:
# 1) You have a postgresql database setup as specified by the lokaal.properties.
#    That is: you have run the following sql statements and scripts.
#               create role dspace with superuser login password 'dspace';
#               create database "dspace" with owner = dspace encoding='utf8' tablespace=pg_default lc_collate = 'C' lc_ctype='en_US.UTF-8' template template0;
# 2) Make sure all the properties in lokaal.properties adhere to your system!
# 3) Make sure you have edited your Tomcat setenv.sh in the bin folder:
#       JAVA_OPTS="-Dfile.encoding=UTF-8 -Xms512m -Xmx512m -XX:MaxPermSize=256m -Dlogging.dir=/wherever/you/want/your/logs $JAVA_OPTS"
#    If so then first run this script with the init arg:
#       ./lokaal-install.sh init
#    Afterwards you can rebuild and restart your DSpace installation with
#       ./lokaal-install.sh restart
#
# For development, send all emails to “mailcatcher”, a dummy email account, that runs on localhost,
# so that real emails don’t get sent out to real people. Therefore the properties of 'mail.server' and 'mail.server.port'
# in the lokaal.properties are set to localhost:1025. Installing “mailcatcher” can be done with gem:
#       > gem install mailcatcher
# Then to start mailcatcher:
#       > mailcatcher
# Starting MailCatcher opens the following two ports
#  Mail interface: smtp://127.0.0.1:1025
#  Web interface:  http://127.0.0.1:1080
# MailCatcher runs as a daemon by default. Go to the web interface to view the mails and to quit.

VERSION=4.2.0-SNAPSHOT
HERE=${PWD}
TARGET_DIR=$HERE/dspace/target
TOMCAT_DIR=/Users/lds/Projects/Dspace/run/tomcat

# Get the Dspace install dir from lokaal.properties file
DSPACE_DIR=$(cat ./lokaal.properties | grep "dspace.install.dir" | cut -d'=' -f2)
# Get the security settings from lokaal.properties file
OPENAM_SERVER_URL=$(cat ./lokaal.properties | grep "openam.server.url" | cut -d'=' -f2)
OPENAM_CONSUMER_TOKEN=$(cat ./lokaal.properties | grep "openam.consumer.token" | cut -d'=' -f2)
OPENAM_CONSUMER_SECRET=$(cat ./lokaal.properties | grep "openam.consumer.secret" | cut -d'=' -f2)
OPENAM_EID_TOKEN_GOTO_URL=$(cat ./lokaal.properties | grep "openam.eid.token.goto.url" | cut -d'=' -f2)
# Get the database settings from lokaal.properties file
DB_USERNAME=$(cat ./lokaal.properties | grep "db.username" | cut -d'=' -f2)
DB_PASSWORD=$(cat ./lokaal.properties | grep "db.password" | cut -d'=' -f2)
DB_URL=$(cat ./lokaal.properties | grep "db.url" | cut -d'=' -f2)

echo "DSpace Install dir: $DSPACE_DIR"

if [ "$1" = "restart" ]; then
    echo " ---------------------------------------------------------------------"
    echo " --------------------- Shutting down Tomcat! -------------------------"
    echo " ---------------------------------------------------------------------"
    $TOMCAT_DIR/bin/shutdown.sh
    rm -rf $TOMCAT_DIR/work/*
    rm -rf $TOMCAT_DIR/temp/*
    rm -rf $TOMCAT_DIR/conf/Catalina/localhost/*
fi

echo " ---------------------------------------------------------------------"
echo " ---------------------- Building project! ----------------------------"
echo " ---------------------------------------------------------------------"
mvn clean package -Denv=lokaal

if [ ! -d "$TARGET_DIR/dspace-$VERSION" ]; then
    echo " ---------------------------------------------------------------------"
    echo " ----------- Unzipping application.zip into $TARGET_DIR --------------"
    echo " ---------------------------------------------------------------------"
    tar -xvf $TARGET_DIR/dspace-$VERSION-application.zip -C $TARGET_DIR
fi

APPS_DIR=$TARGET_DIR/dspace-$VERSION/apps

cd $APPS_DIR/dspace

echo " ---------------------------------------------------------------------"
echo " ----------------- Adding ctx files into Tomcat! ---------------------"
echo " ---------------------------------------------------------------------"
#    sed "s#%tomcat_data_dir%/dspace#$DSPACE_DIR#g" $APPS_DIR/jspui.xml > $TOMCAT_DIR/conf/Catalina/localhost/jspui.xml
#    sed "s#%tomcat_data_dir%/dspace#$DSPACE_DIR#g" $APPS_DIR/lni.xml > $TOMCAT_DIR/conf/Catalina/localhost/lni.xml
#    sed "s#%tomcat_data_dir%/dspace#$DSPACE_DIR#g" $APPS_DIR/oai.xml > $TOMCAT_DIR/conf/Catalina/localhost/oai.xml
#    sed "s#%tomcat_data_dir%/dspace#$DSPACE_DIR#g" $APPS_DIR/sword.xml > $TOMCAT_DIR/conf/Catalina/localhost/sword.xml
sed "s#%tomcat_data_dir%/dspace#$DSPACE_DIR#g" $APPS_DIR/rest.xml > $TOMCAT_DIR/conf/Catalina/localhost/archief#rest.xml
sed "s#%tomcat_data_dir%/dspace#$DSPACE_DIR#g" $APPS_DIR/solr.xml > $TOMCAT_DIR/conf/Catalina/localhost/archief#solr.xml
sed -e "s#%tomcat_data_dir%/dspace#$DSPACE_DIR#g" -e "s#%db_username%#$DB_USERNAME#g" -e "s#%db_password%#$DB_PASSWORD#g" -e "s#url=\"[^\"]*#url=\"$DB_URL#g" $APPS_DIR/swordv2.xml > $TOMCAT_DIR/conf/Catalina/localhost/archief#swordv2.xml
sed -e "s#%tomcat_data_dir%/dspace#$DSPACE_DIR#g" -e "s#%db_username%#$DB_USERNAME#g" -e "s#%db_password%#$DB_PASSWORD#g" -e "s#url=\"[^\"]*#url=\"$DB_URL#g" $APPS_DIR/xmlui.xml > $TOMCAT_DIR/conf/Catalina/localhost/archief#xmlui.xml

echo " ---------------------------------------------------------------------"
echo " ---------------- Overwrite Security settings! -----------------------"
echo " ---------------------------------------------------------------------"
sed -i -e "s|^\(openam.server.url[[:blank:]]*=[[:blank:]]*\).*$|\1$OPENAM_SERVER_URL|g" $APPS_DIR/dspace/config/modules/authentication-openam.cfg
sed -i -e "s|^\(openam.consumer.token[[:blank:]]*=[[:blank:]]*\).*$|\1$OPENAM_CONSUMER_TOKEN|g" $APPS_DIR/dspace/config/modules/authentication-openam.cfg
sed -i -e "s|^\(openam.consumer.secret[[:blank:]]*=[[:blank:]]*\).*$|\1$OPENAM_CONSUMER_SECRET|g" $APPS_DIR/dspace/config/modules/authentication-openam.cfg
sed -i -e "s|^\(openam.eid.token.goto.url[[:blank:]]*=[[:blank:]]*\).*$|\1$OPENAM_EID_TOKEN_GOTO_URL|g" $APPS_DIR/dspace/config/modules/authentication-openam.cfg

if [ "$1" = "init" ]; then
    echo " ---------------------------------------------------------------------"
    echo " --------------------- Initializing Dspace! --------------------------"
    echo " ---------------------------------------------------------------------"
    ant init_installation init_configs test_database setup_database load_registries install_code update_webapps

    echo " ---------------------------------------------------------------------"
    echo " --------------------- Creating Administrator! -----------------------"
    echo " ---------------------------------------------------------------------"
    $APPS_DIR/dspace/bin/dspace create-administrator -e 'dspace@milieuinfo.be' -f 'admin' -l 'dspace' -c 'en' -p 'DspacE'

    echo " ---------------------------------------------------------------------"
    echo " ----------- Importing communities, groups and policies! -------------"
    echo " ---------------------------------------------------------------------"
    $APPS_DIR/import-structure-policies.py -x -b $APPS_DIR/dspace/bin/dspace -f $APPS_DIR/community-tree.xml

    echo " ---------------------------------------------------------------------"
    echo " --------------------- Starting up Tomcat! ---------------------------"
    echo " ---------------------------------------------------------------------"
    $TOMCAT_DIR/bin/startup.sh

else
    echo " ---------------------------------------------------------------------"
    echo " ----------------------- Updating Dspace! ----------------------------"
    echo " ---------------------------------------------------------------------"
    ant update clean_backups
fi

if [ "$1" = "restart" ]; then
    echo " ---------------------------------------------------------------------"
    echo " --------------------- Starting up Tomcat! ---------------------------"
    echo " ---------------------------------------------------------------------"
    $TOMCAT_DIR/bin/startup.sh
fi

# Mac OSX Notification
osascript -e 'display notification "Done doing installation stuff!" with title "DSpace"'
