#!/bin/bash -e

# Replace variables
sed -i \
    -e "s/%vlan%/${vlan}/g" \
    -e "s/%ZUIL%/${ZUIL}/g" \
    -e "s/%zuil%/${zuil}/g" \
    -e "s/%ZUILURL%/${ZUILURL}/g" \
    -e "s@%tomcat_apps_dir%@${tomcat_apps_dir}@g" \
    -e "s@%tomcat_data_dir%@${tomcat_data_dir}@g" \
    -e "s@%tomcat_home_dir%@${tomcat_home_dir}@g" \
    -e "s/%db_username%/${db_username}/g" \
    -e "s/%db_password%/${db_password}/g" \
    -e "s/%db_port%/${db_port}/g" \
    -e "s/%mailrelay_host%/${mailrelay_host}/g" \
    -e "s/%mailrelay_port%/${mailrelay_port}/g" \
    -e "s/%dspace.consumer.token%/${dspace_consumer_token}/g" \
    -e "s/%dspace.consumer.secret%/${dspace_consumer_secret}/g" \
    ${tomcat_apps_dir}/*.xml \
    ${tomcat_apps_dir}/cleanup.sh \
    ${tomcat_apps_dir}/setenv.sh \
    ${tomcat_apps_dir}/ansible.properties \
    ${tomcat_apps_dir}/dspace/config/modules/authentication-openam.cfg

# Add the ansible substituted properties file to the head of dspace.cfg
cat ${tomcat_apps_dir}/ansible.properties | cat - ${tomcat_apps_dir}/dspace/config/dspace.cfg > ${tomcat_apps_dir}/dspace/config/dspace.cfg.tmp && mv -f ${tomcat_apps_dir}/dspace/config/dspace.cfg.tmp ${tomcat_apps_dir}/dspace/config/dspace.cfg

# Replacing dspace.dir in dspace.cfg file.
# The Ant script won't run properly as it loads this property through regexp property filtering.
sed -i -e "s|^\(dspace.dir[[:blank:]]*=[[:blank:]]*\).*$|\1${tomcat_data_dir}/dspace|g" ${tomcat_apps_dir}/dspace/config/dspace.cfg

# Symlinks into tomcat
#ln -f -s ${tomcat_apps_dir}/jspui.xml ${tomcat_home_dir}/conf/Catalina/localhost/jspui.xml
#ln -f -s ${tomcat_apps_dir}/lni.xml ${tomcat_home_dir}/conf/Catalina/localhost/lni.xml
#ln -f -s ${tomcat_apps_dir}/sword.xml ${tomcat_home_dir}/conf/Catalina/localhost/sword.xml
#ln -f -s ${tomcat_apps_dir}/oai.xml ${tomcat_home_dir}/conf/Catalina/localhost/oai.xml
ln -f -s ${tomcat_apps_dir}/rest.xml ${tomcat_home_dir}/conf/Catalina/localhost/rest.xml
ln -f -s ${tomcat_apps_dir}/solr.xml ${tomcat_home_dir}/conf/Catalina/localhost/solr.xml
ln -f -s ${tomcat_apps_dir}/swordv2.xml ${tomcat_home_dir}/conf/Catalina/localhost/swordv2.xml
ln -f -s ${tomcat_apps_dir}/xmlui.xml ${tomcat_home_dir}/conf/Catalina/localhost/xmlui.xml

ln -f -s ${tomcat_apps_dir}/setenv.sh ${tomcat_home_dir}/bin/setenv.sh

if [ ! -d "${tomcat_data_dir}/dspace/bin" ]; then
    # Install the dspace configs, code and webapps into the '${tomcat_data_dir}/dspace' location
    cd ${tomcat_apps_dir}/dspace && ant init_installation init_configs test_database load_registries install_code update_webapps clean_backups

    # Create administrator
    ${tomcat_data_dir}/dspace/bin/dspace create-administrator -e 'dspace@milieuinfo.be' -f 'admin' -l 'dspace' -c 'en' -p 'DspacE'

    # (Create Communities, groups and policies)
    ${tomcat_apps_dir}/import-structure-policies.py -x -b ${tomcat_apps_dir}/dspace/bin/dspace -f ${tomcat_apps_dir}/dspace/config/community-tree.xml
else
    cd ${tomcat_apps_dir}/dspace && ant update clean_backups
fi

chown -R tomcat:tomcat ${tomcat_data_dir}
