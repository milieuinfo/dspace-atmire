<xsl:stylesheet
        version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:redirect="http://xml.apache.org/xalan/redirect"
        xmlns:utils="com.atmire.util.LneUtils"
        extension-element-prefixes="redirect" exclude-result-prefixes="redirect">

    <xsl:output encoding="UTF-8" indent="yes" omit-xml-declaration="yes"/>

    <xsl:param name="directory" />
    


    <xsl:template match="@* | node()">
        <xsl:apply-templates select="*"/>
    </xsl:template>

    <xsl:template match="text()"/>
    <xsl:template match="text()" mode="dc"/>
    <xsl:template match="text()" mode="imjv"/>
    <xsl:variable name="cbbnr">
        <xsl:value-of select="//MilieuVerslagMetaData/Cbbnummer/text()"/>
    </xsl:variable>


    <!-- Dossier -->
    <xsl:template match="IdentificatieMetaData">
        <redirect:write select="concat('IdentificatieMetaData',position(),'/dublin_core.xml')">
            <dublin_core schema="dc">
                <xsl:call-template name="dossier-source"/>

                <xsl:call-template name="dossier-title">
                    <xsl:with-param name="type"/>
                </xsl:call-template>

                <xsl:call-template name="dc-type">
                    <xsl:with-param name="type">
                        <xsl:text>Dossier</xsl:text>
                    </xsl:with-param>
                </xsl:call-template>

                <xsl:call-template name="title-alternative">
                    <xsl:with-param name="title" select="'IMJV'"/>
                </xsl:call-template>

                <xsl:call-template name="dossier-identifier">
                    <xsl:with-param name="type"/>
                </xsl:call-template>
                <xsl:call-template name="document-author"/>
                <xsl:call-template name="document-publisher"/>
                <xsl:call-template name="dossier-date-issued"/>

                <xsl:apply-templates select="." mode="dc"/>
            </dublin_core>
        </redirect:write>
        <redirect:write select="concat('IdentificatieMetaData',position(),'/metadata_imjv.xml')">
            <dublin_core schema="imjv">
                <xsl:apply-templates select="." mode="imjv"/>
                <xsl:call-template name="dossier-dossiernummer"/>
                <xsl:apply-templates select="//MilieuVerslagMetaData" mode="imjv"/>
            </dublin_core>
        </redirect:write>
        <redirect:write select="concat('IdentificatieMetaData',position(),'/metadata_kbo.xml')">
            <dublin_core schema="kbo">
                <xsl:copy-of select="utils:getExtraMetaData($directory,'kbo')"/>  
            </dublin_core>
        </redirect:write>
        
        <xsl:call-template name="aangifte">
            <xsl:with-param name="root-directory">
                <xsl:value-of select="concat('IdentificatieMetaData',position())"/>
            </xsl:with-param>
        </xsl:call-template>

        <xsl:call-template name="IdentificatieMetaData-source">
            <xsl:with-param name="root-directory">
                <xsl:value-of select="concat('IdentificatieMetaData',position())"/>
            </xsl:with-param>
        </xsl:call-template>

        <redirect:write select="concat('IdentificatieMetaData',position(), '/contents')">
            <xsl:text></xsl:text>
        </redirect:write>

    </xsl:template>


    <xsl:template name="IdentificatieMetaData-source">
        <xsl:param name="root-directory"/>
        <redirect:write select="concat('source',position(),'/dublin_core.xml')">
            <dublin_core schema="dc">
                <xsl:call-template name="dossier-source"/>

                <xsl:call-template name="source-title"/>

                <xsl:call-template name="dc-type">
                    <xsl:with-param name="type">
                        <xsl:text>METADATA</xsl:text>
                    </xsl:with-param>
                </xsl:call-template>

                <xsl:call-template name="title-alternative">
                    <xsl:with-param name="title" select="'IMJV'"/>
                </xsl:call-template>

                <xsl:call-template name="dossier-identifier">
                    <xsl:with-param name="type">METADATA</xsl:with-param>
                </xsl:call-template>
                <xsl:call-template name="document-author"/>
                <xsl:call-template name="document-publisher"/>
                <xsl:call-template name="dossier-date-issued"/>

                <xsl:apply-templates select="." mode="dc"/>
            </dublin_core>
        </redirect:write>
        <redirect:write select="concat('source',position(),'/metadata_imjv.xml')">
            <dublin_core schema="imjv">
                <xsl:apply-templates select="." mode="imjv"/>
                <xsl:call-template name="dossier-dossiernummer"/>
                <xsl:apply-templates select="//MilieuVerslagMetaData" mode="imjv"/>
                <dcvalue element="document" qualifier="title">
                    <xsl:text>METADATA</xsl:text>
                </dcvalue>
            </dublin_core>
        </redirect:write>
        <redirect:write select="concat('source',position(),'/metadata_kbo.xml')">
            <dublin_core schema="kbo">
                <xsl:copy-of select="utils:getExtraMetaData($directory,'kbo')"/>  
            </dublin_core>
        </redirect:write>
        
        <redirect:write select="concat('source',position(),'/relations.xml')">
            <dublin_core schema="relation">
                <dcvalue element="hasParent">
                    <xsl:value-of select="$root-directory"/>
                </dcvalue>
            </dublin_core>
        </redirect:write>

        <redirect:write select="concat('source',position(), '/contents')">
            <xsl:value-of select="$directory"/>
            <xsl:text>/</xsl:text>
            <xsl:value-of select="utils:getMetadataFilename($directory)" disable-output-escaping="yes"/>
        </redirect:write>

    </xsl:template>

    <!-- Documenten -->
    <xsl:template name="aangifte">
        <xsl:param name="root-directory"/>

        <xsl:for-each select="../AangiftenMetaData/Aangifte">
            <redirect:write select="concat('aangifte',position(), '/dublin_core.xml')">
                <dublin_core schema="dc">
                    <xsl:call-template name="dossier-source"/>
                    <xsl:call-template name="document-title">
                        <xsl:with-param name="type"/>
                        <xsl:with-param name="level">
                            <xsl:text>0</xsl:text>
                        </xsl:with-param>
                    </xsl:call-template>
                    <xsl:call-template name="dc-type">
                        <xsl:with-param name="type">
                            <xsl:text>Aangifte</xsl:text>
                        </xsl:with-param>
                    </xsl:call-template>
                    <xsl:call-template name="title-alternative">
                        <xsl:with-param name="title" select="'IMJV'"/>
                    </xsl:call-template>
                    <xsl:call-template name="document-date-issued">
                        <xsl:with-param name="level">
                            <xsl:text>0</xsl:text>
                        </xsl:with-param>
                    </xsl:call-template>
                    <xsl:call-template name="document-publisher"/>
                    <xsl:call-template name="document-author"/>
                    <xsl:apply-templates mode="dc"/>
                    <xsl:apply-templates select="//IdentificatieMetaData" mode="dc"/>
                    <xsl:call-template name="aangifte-identifier"/>
                </dublin_core>
            </redirect:write>
            <redirect:write select="concat('aangifte',position(), '/metadata_imjv.xml')">
                <dublin_core schema="imjv">
                    <xsl:apply-templates mode="imjv"/>
                    <xsl:call-template name="dossier-dossiernummer"/>
                    <xsl:apply-templates select="//IdentificatieMetaData" mode="imjv"/>
                    <xsl:apply-templates select="//MilieuVerslagMetaData" mode="imjv"/>
                    <dcvalue element="aangiftetype">
                        <xsl:value-of select="AangifteType"/>
                    </dcvalue>
                </dublin_core>
            </redirect:write>
            
            <redirect:write select="concat('aangifte',position(),'/metadata_kbo.xml')">
                <dublin_core schema="kbo">
                    <xsl:copy-of select="utils:getExtraMetaData($directory,'kbo')"/>  
                </dublin_core>
            </redirect:write>
            
            
            <redirect:write select="concat('aangifte',position(),'/relations.xml')">
                <dublin_core schema="relation">
                    <dcvalue element="hasParent">
                        <xsl:value-of select="$root-directory"/>
                    </dcvalue>
                </dublin_core>
            </redirect:write>

            <!--<xsl:call-template name="aangiftePdf">-->
                <!--<xsl:with-param name="aangifte-directory">-->
                    <!--<xsl:value-of select="concat('aangifte',position())"/>-->
                <!--</xsl:with-param>-->
            <!--</xsl:call-template>-->

            <!--<xsl:call-template name="aangifte-source">-->
                <!--<xsl:with-param name="root-directory">-->
                    <!--<xsl:value-of select="concat('aangifte',position())"/>-->
                <!--</xsl:with-param>-->
            <!--</xsl:call-template>-->

            <xsl:call-template name="ProcesSchema">
                <xsl:with-param name="root-directory">
                    <xsl:value-of select="concat('aangifte',position())"/>
                </xsl:with-param>
            </xsl:call-template>

            <xsl:call-template name="Bijlage">
                <xsl:with-param name="root-directory">
                    <xsl:value-of select="concat('aangifte',position())"/>
                </xsl:with-param>
            </xsl:call-template>

            <xsl:call-template name="Aanvulling">
                <xsl:with-param name="root-directory">
                    <xsl:value-of select="concat('aangifte',position())"/>
                </xsl:with-param>
            </xsl:call-template>

            <redirect:write select="concat('aangifte',position(), '/contents')">
                <xsl:if test="AangiftePdf">
                    <xsl:value-of select="$directory"/>
                    <xsl:text>/</xsl:text>
                    <xsl:value-of select="AangiftePdf" disable-output-escaping="yes"/>
                    <xsl:text>&#10;</xsl:text>
                </xsl:if>
            </redirect:write>

        </xsl:for-each>

    </xsl:template>


    <xsl:template name="ProcesSchema">
        <!--2013/00112120000187-->
        <xsl:param name="root-directory"/>

        <xsl:variable name="count">
            <xsl:value-of select="position()"/>
        </xsl:variable>

        <xsl:variable name="aangifteType">
            <xsl:value-of select="AangifteType"/>
        </xsl:variable>


        <xsl:for-each select="ProcesSchema/Bestand">

            <redirect:write select="concat('ProcesSchema',$count,'_',position(), '/dublin_core.xml')">
                <dublin_core schema="dc">
                    <xsl:call-template name="dossier-source"/>
                    <xsl:call-template name="document-title">
                        <xsl:with-param name="type">
                            <!--<xsl:choose>-->
                                <!--<xsl:when test="count(../../ProcesSchema/Bestand) &gt; 1">-->
                                    <!--<xsl:text>Processchema </xsl:text>-->
                                    <!--<xsl:value-of select="position()"/>-->
                                <!--</xsl:when>-->
                                <!--<xsl:otherwise>-->
                                    <xsl:text>Processchema</xsl:text>
                                <!--</xsl:otherwise>-->
                            <!--</xsl:choose>-->
                        </xsl:with-param>
                        <xsl:with-param name="level">
                            <xsl:text>2</xsl:text>
                        </xsl:with-param>
                    </xsl:call-template>
                    <xsl:call-template name="dc-type">
                        <xsl:with-param name="type">
                            <xsl:text>Processchema</xsl:text>
                        </xsl:with-param>
                    </xsl:call-template>
                    <xsl:call-template name="title-alternative">
                        <xsl:with-param name="title" select="'IMJV'"/>
                    </xsl:call-template>

                    <xsl:call-template name="document-date-issued">
                        <xsl:with-param name="level">
                            <xsl:text>2</xsl:text>
                        </xsl:with-param>
                    </xsl:call-template>
                    <xsl:call-template name="document-publisher"/>
                    <xsl:call-template name="document-author"/>
                    <xsl:call-template name="document-identifier">
                        <xsl:with-param name="level">
                            <xsl:text>2</xsl:text>
                        </xsl:with-param>
                    </xsl:call-template>
                    <xsl:apply-templates mode="dc"/>
                    <xsl:apply-templates select="//IdentificatieMetaData" mode="dc"/>
                </dublin_core>
            </redirect:write>
            <redirect:write select="concat('ProcesSchema',$count,'_',position(), '/metadata_imjv.xml')">
                <dublin_core schema="imjv">
                    <xsl:apply-templates mode="imjv"/>
                    <xsl:call-template name="document-dmsexportnotes"/>
                    <xsl:apply-templates select="//IdentificatieMetaData" mode="imjv"/>
                    <xsl:apply-templates select="//MilieuVerslagMetaData" mode="imjv"/>
                    <xsl:call-template name="dossier-dossiernummer"/>
                    <!--<xsl:call-template name="document-file-title"/>-->

                    <dcvalue element="aangiftetype">
                        <xsl:value-of select="$aangifteType"/>
                    </dcvalue>
                    <xsl:call-template name="aangifte-document-title">
                        <xsl:with-param name="aangiftetype" select="$aangifteType"/>
                        <xsl:with-param name="identifier">
                            <xsl:call-template name="document-identifier-internal"/>
                        </xsl:with-param>
                    </xsl:call-template>
                </dublin_core>
            </redirect:write>
            <redirect:write select="concat('ProcesSchema',$count,'_',position(),'/metadata_kbo.xml')">
                <dublin_core schema="kbo">
                    <xsl:copy-of select="utils:getExtraMetaData($directory,'kbo')"/>  
                </dublin_core>
            </redirect:write>
            
            
            
            <redirect:write select="concat('ProcesSchema',$count,'_',position(), '/contents')">
                <xsl:value-of select="$directory"/>
                <xsl:text>/</xsl:text>
                <xsl:value-of select="." disable-output-escaping="yes"/>
                <xsl:text>&#10;</xsl:text>
            </redirect:write>
            <redirect:write select="concat('ProcesSchema',$count,'_',position(),'/relations.xml')">
                <dublin_core schema="relation">
                    <dcvalue element="hasParent">
                        <xsl:value-of select="$root-directory"/>
                    </dcvalue>
                </dublin_core>
            </redirect:write>

        </xsl:for-each>
    </xsl:template>

    <xsl:template name="Bijlage">
        <!--2013/00112120000187-->
        <xsl:param name="root-directory"/>

        <xsl:variable name="count">
            <xsl:value-of select="position()"/>
        </xsl:variable>

        <xsl:variable name="aangifteType">
            <xsl:value-of select="AangifteType"/>
        </xsl:variable>

        <xsl:for-each select="AangifteGeneriek/Bijlagen/Bestand">

            <redirect:write select="concat('Bijlage',$count,'_',position(), '/dublin_core.xml')">
                <dublin_core schema="dc">
                    <xsl:call-template name="dossier-source"/>
                    <xsl:call-template name="document-title">
                        <xsl:with-param name="type">
                            <!--<xsl:choose>-->
                                <!--<xsl:when test="count(../../../AangifteGeneriek/Bijlagen/Bestand) &gt; 1">-->
                                    <!--<xsl:text>Bijlage </xsl:text>-->
                                    <!--<xsl:value-of select="position()"/>-->
                                <!--</xsl:when>-->
                                <!--<xsl:otherwise>-->
                                    <xsl:text>Bijlage</xsl:text>
                                <!--</xsl:otherwise>-->
                            <!--</xsl:choose>-->
                        </xsl:with-param>
                        <xsl:with-param name="level">
                            <xsl:text>3</xsl:text>
                        </xsl:with-param>
                    </xsl:call-template>
                    <xsl:call-template name="dc-type">
                        <xsl:with-param name="type">
                            <xsl:text>Bijlage</xsl:text>
                        </xsl:with-param>
                    </xsl:call-template>
                    <xsl:call-template name="title-alternative">
                        <xsl:with-param name="title" select="'IMJV'"/>
                    </xsl:call-template>

                    <xsl:call-template name="document-date-issued">
                        <xsl:with-param name="level">
                            <xsl:text>3</xsl:text>
                        </xsl:with-param>
                    </xsl:call-template>
                    <xsl:call-template name="document-publisher"/>
                    <xsl:call-template name="document-author"/>
                    <xsl:call-template name="document-identifier">
                        <xsl:with-param name="level">
                            <xsl:text>3</xsl:text>
                        </xsl:with-param>
                    </xsl:call-template>
                    <xsl:apply-templates mode="dc"/>
                    <xsl:apply-templates select="//IdentificatieMetaData" mode="dc"/>
                </dublin_core>
            </redirect:write>
            <redirect:write select="concat('Bijlage',$count,'_',position(), '/metadata_imjv.xml')">
                <dublin_core schema="imjv">
                    <xsl:apply-templates mode="imjv"/>
                    <xsl:call-template name="document-dmsexportnotes"/>
                    <xsl:apply-templates select="//IdentificatieMetaData" mode="imjv"/>
                    <xsl:apply-templates select="//MilieuVerslagMetaData" mode="imjv"/>
                    <xsl:call-template name="dossier-dossiernummer"/>
                    <dcvalue element="aangiftetype">
                        <xsl:value-of select="$aangifteType"/>
                    </dcvalue>
                    <xsl:call-template name="aangifte-document-title">
                        <xsl:with-param name="aangiftetype" select="$aangifteType"/>
                        <xsl:with-param name="identifier">
                            <xsl:call-template name="document-identifier-internal"/>
                        </xsl:with-param>
                    </xsl:call-template>
                </dublin_core>
            </redirect:write>
            
            <redirect:write select="concat('Bijlage',$count,'_',position(),'/metadata_kbo.xml')">
                <dublin_core schema="kbo">
                    <xsl:copy-of select="utils:getExtraMetaData($directory,'kbo')"/>  
                </dublin_core>
            </redirect:write>
            
            <redirect:write select="concat('Bijlage',$count,'_',position(), '/contents')">
                <xsl:value-of select="$directory"/>
                <xsl:text>/</xsl:text>
                <xsl:value-of select="." disable-output-escaping="yes"/>
                <xsl:text>&#10;</xsl:text>
            </redirect:write>
            <redirect:write select="concat('Bijlage',$count,'_',position(),'/relations.xml')">
                <dublin_core schema="relation">
                    <dcvalue element="hasParent">
                        <xsl:value-of select="$root-directory"/>
                    </dcvalue>
                </dublin_core>
            </redirect:write>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="Aanvulling">
        <!--2013/00112120000187-->
        <xsl:param name="root-directory"/>

        <xsl:variable name="count">
            <xsl:value-of select="position()"/>
        </xsl:variable>

        <xsl:variable name="aangifteType">
            <xsl:value-of select="AangifteType"/>
        </xsl:variable>

        <xsl:variable name="jaar">
            <xsl:value-of select="//RapporteringsJaar"/>
        </xsl:variable>
        <xsl:variable name="nummer">
            <xsl:value-of select="$cbbnr"/>
        </xsl:variable>

        <!-- TODO Change directory to non-fixed one-->
        <xsl:variable name="fileCount">
            <xsl:value-of select="utils:countAllAanvullingFiles($directory,$jaar,$nummer,$aangifteType)"/>
        </xsl:variable>

        <xsl:if test="$fileCount >0">

                <xsl:call-template name="aanvullingLoop">
                    <xsl:with-param name="root-directory">
                        <xsl:value-of select="concat('aangifte',position())"/>
                    </xsl:with-param>
                    <xsl:with-param name="fileCount">
                        <xsl:value-of select="$fileCount"/>
                    </xsl:with-param>
                    <xsl:with-param name="jaar">
                        <xsl:value-of select="$jaar"/>
                    </xsl:with-param>
                    <xsl:with-param name="nummer">
                        <xsl:value-of select="$nummer"/>
                    </xsl:with-param>
                    <xsl:with-param name="aangifteType">
                        <xsl:value-of select="$aangifteType"/>
                    </xsl:with-param>
                    <xsl:with-param name="i">1</xsl:with-param>

                </xsl:call-template>

        </xsl:if>
    </xsl:template>

    <xsl:template name="aanvullingLoop">
        <xsl:param name="root-directory"/>
        <xsl:param name="fileCount"/>
        <xsl:param name="i"/>
        <xsl:param name="jaar"/>
        <xsl:param name="nummer"/>
        <xsl:param name="aangifteType"/>
        <xsl:variable name="count">
            <xsl:value-of select="position()"/>
        </xsl:variable>

        <redirect:write select="concat('Aanvulling',$count,'_',$i, '/dublin_core.xml')">
            <dublin_core schema="dc">
                <xsl:call-template name="dossier-source"/>
                <xsl:call-template name="document-title">
                    <xsl:with-param name="type">
                        <!--<xsl:choose>-->
                            <!--<xsl:when test="$fileCount &gt; 1">-->
                                <!--<xsl:text>Aanvulling </xsl:text>-->
                                <!--<xsl:value-of select="$i"/>-->
                            <!--</xsl:when>-->
                            <!--<xsl:otherwise>-->
                                <xsl:text>Aanvulling</xsl:text>
                            <!--</xsl:otherwise>-->
                        <!--</xsl:choose>-->
                    </xsl:with-param>
                    <xsl:with-param name="level">
                        <xsl:text>0</xsl:text>
                    </xsl:with-param>
                </xsl:call-template>
                    <xsl:call-template name="dc-type">
                        <xsl:with-param name="type">
                            <xsl:text>Aanvulling</xsl:text>
                        </xsl:with-param>
                    </xsl:call-template>
                    <xsl:call-template name="title-alternative">
                        <xsl:with-param name="title" select="'IMJV'"/>
                    </xsl:call-template>
                <xsl:call-template name="document-date-issued">
                    <xsl:with-param name="level">
                        <xsl:text>0</xsl:text>
                    </xsl:with-param>
                </xsl:call-template>
                <xsl:call-template name="document-publisher"/>
                <xsl:call-template name="document-author"/>
                <!--<xsl:call-template name="aangifte-identifier"/>-->
                <dcvalue element="identifier">
                    <xsl:call-template name="aanvulling-identifier-internal">
                        <xsl:with-param name="jaar" select="$jaar"/>
                        <xsl:with-param name="nummer" select="$nummer"/>
                        <xsl:with-param name="aangifteType" select="$aangifteType"/>
                        <xsl:with-param name="i" select="$i"/>
                    </xsl:call-template>
                </dcvalue>
                <xsl:apply-templates mode="dc"/>
                <xsl:apply-templates select="//IdentificatieMetaData" mode="dc"/>
            </dublin_core>
        </redirect:write>

        <redirect:write select="concat('Aanvulling',$count,'_',$i, '/metadata_imjv.xml')">
            <dublin_core schema="imjv">
                <xsl:apply-templates mode="imjv"/>
                <xsl:call-template name="document-dmsexportnotes"/>
                <xsl:apply-templates select="//IdentificatieMetaData" mode="imjv"/>
                <xsl:apply-templates select="//MilieuVerslagMetaData" mode="imjv"/>
                <xsl:call-template name="dossier-dossiernummer"/>
                <dcvalue element="aangiftetype">
                    <xsl:value-of select="$aangifteType"/>
                </dcvalue>
                <xsl:call-template name="aangifte-document-title">
                    <xsl:with-param name="aangiftetype" select="$aangifteType"/>
                    <xsl:with-param name="identifier">
                        <xsl:call-template name="aanvulling-identifier-internal">
                            <xsl:with-param name="jaar" select="$jaar"/>
                            <xsl:with-param name="nummer" select="$nummer"/>
                            <xsl:with-param name="aangifteType" select="$aangifteType"/>
                            <xsl:with-param name="i" select="$i"/>
                        </xsl:call-template>
                    </xsl:with-param>
                </xsl:call-template>
            </dublin_core>
        </redirect:write>

        <redirect:write select="concat('Aanvulling',$count,'_',$i,'/metadata_kbo.xml')">
            <dublin_core schema="kbo">
                <xsl:copy-of select="utils:getExtraMetaData($directory,'kbo')"/>  
            </dublin_core>
        </redirect:write>

        <redirect:write select="concat('Aanvulling',$count,'_',$i, '/contents')">
            <xsl:value-of select="$directory"/>
            <xsl:text>/</xsl:text>
            <xsl:value-of select="utils:getFileNameBasedOnIndex($directory,$jaar,$nummer,$aangifteType,$i)" disable-output-escaping="yes"/>
            <xsl:text>&#10;</xsl:text>
        </redirect:write>

        <redirect:write select="concat('Aanvulling',$count,'_',$i, '/relations.xml')">
            <dublin_core schema="relation">
                <dcvalue element="hasParent">
                    <xsl:value-of select="$root-directory"/>
                </dcvalue>
            </dublin_core>
        </redirect:write>

        <xsl:if test="$i &lt; $fileCount">
            <xsl:call-template name="aanvullingLoop">
                <xsl:with-param name="i">
                    <xsl:value-of select="$i + 1"/>
                </xsl:with-param>
                <xsl:with-param name="fileCount">
                    <xsl:value-of select="$fileCount"/>
                </xsl:with-param>
                <xsl:with-param name="root-directory" select="$root-directory"/>
                <xsl:with-param name="jaar">
                    <xsl:value-of select="$jaar"/>
                </xsl:with-param>
                <xsl:with-param name="nummer">
                    <xsl:value-of select="$nummer"/>
                </xsl:with-param>
                <xsl:with-param name="aangifteType">
                    <xsl:value-of select="$aangifteType"/>
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    <!-- Dossier metadata -->

    <xsl:template name="dossier-title">
        <xsl:param name="type"/>
        <xsl:choose>
            <xsl:when test="Exploitatie">
                <dcvalue element="title">
                    <xsl:text>Integraal Milieu Jaarverslag - Dossier - </xsl:text>
                    <xsl:value-of select="RapporteringsJaar/text()"/>
                    <xsl:text> - </xsl:text>
                    <xsl:value-of select="Exploitatie/Naam/text()"/>
                    <xsl:text> - </xsl:text>
                    <xsl:value-of select="$cbbnr"/>
                    <xsl:if test="$type">
                        <xsl:text> - </xsl:text>
                        <xsl:value-of select="$type"/>
                    </xsl:if>
                </dcvalue>
            </xsl:when>
            <xsl:otherwise>
                <dcvalue element="title">
                    <xsl:text>Integraal Milieu Jaarverslag - Dossier - </xsl:text>
                    <xsl:value-of select="RapporteringsJaar/text()"/>
                    <xsl:text> - </xsl:text>
                    <xsl:value-of select="Exploitant/Naam/text()"/>
                    <xsl:text> - </xsl:text>
                    <xsl:value-of select="$cbbnr"/>
                    <xsl:if test="$type">
                        <xsl:text> - </xsl:text>
                        <xsl:value-of select="$type"/>
                    </xsl:if>
                </dcvalue>
            </xsl:otherwise>
        </xsl:choose>

    </xsl:template>

    <xsl:template name="source-title">
        <xsl:choose>
            <xsl:when test="Exploitatie">
                <dcvalue element="title">
                    <xsl:text>Integraal Milieu Jaarverslag - METADATA - </xsl:text>
                    <xsl:value-of select="RapporteringsJaar/text()"/>
                    <xsl:text> - </xsl:text>
                    <xsl:value-of select="Exploitatie/Naam/text()"/>
                    <xsl:text> - </xsl:text>
                    <xsl:value-of select="$cbbnr"/>
                </dcvalue>
            </xsl:when>
            <xsl:otherwise>
                <dcvalue element="title">
                    <xsl:text>Integraal Milieu Jaarverslag - Dossier - </xsl:text>
                    <xsl:value-of select="RapporteringsJaar/text()"/>
                    <xsl:text> - </xsl:text>
                    <xsl:value-of select="Exploitant/Naam/text()"/>
                    <xsl:text> - </xsl:text>
                    <xsl:value-of select="$cbbnr"/>
                </dcvalue>
            </xsl:otherwise>
        </xsl:choose>

    </xsl:template>

    <!--<xsl:template match="IdentificatieMetaData/Exploitant/Naam" mode="dc">-->
        <!--<dcvalue element="contributor" qualifier="author">-->
            <!--<xsl:value-of select="text()"/>-->
        <!--</dcvalue>-->
    <!--</xsl:template>-->

    <xsl:template match="IdentificatieMetaData/RapporteringsJaar" mode="imjv">
        <dcvalue element="rapporteringsjaar">
            <xsl:value-of select="text()"/>
        </dcvalue>
    </xsl:template>

    <xsl:template match="IdentificatieMetaData/Exploitatie/CBBExploitatieNummer" mode="imjv">
        <dcvalue element="exploitatie" qualifier="nummer">
            <xsl:value-of select="$cbbnr"/>
        </dcvalue>
    </xsl:template>

    <!--<xsl:template match="IdentificatieMetaData/Exploitant/CBBExploitantNummer" mode="imjv">-->
        <!--<dcvalue element="ExploitantCBBNummer">-->
            <!--<xsl:value-of select="text()"/>-->
        <!--</dcvalue>-->
    <!--</xsl:template>-->

    <xsl:template match="IdentificatieMetaData/Exploitatie/Naam" mode="imjv">
        <dcvalue element="exploitatie" qualifier="naam">
            <xsl:value-of select="text()"/>
        </dcvalue>
    </xsl:template>

    <!--<xsl:template match="IdentificatieMetaData/Exploitant/OndernemingsNummer" mode="imjv">-->
        <!--<dcvalue element="ExploitantOndernemingsNummer">-->
            <!--<xsl:value-of select="text()"/>-->
        <!--</dcvalue>-->
    <!--</xsl:template>-->

    <!--<xsl:template match="IdentificatieMetaData/Exploitatie/Locatie" mode="imjv">-->
        <!--<dcvalue element="ExploitatieLocatie">-->
            <!--<xsl:call-template name="vervlakte-voorstelling"/>-->
        <!--</dcvalue>-->
        <!--<xsl:apply-templates mode="imjv" />-->
    <!--</xsl:template>-->

    <xsl:template match="IdentificatieMetaData/Exploitatie/Locatie/Gemeente" mode="imjv">
        <dcvalue element="exploitatie" qualifier="gemeente">
            <xsl:value-of select="text()"/>
            <xsl:value-of select="Naam"/>
        </dcvalue>
    </xsl:template>

    <xsl:template match="IdentificatieMetaData/Exploitatie/Locatie/Postcode" mode="imjv">
        <dcvalue element="exploitatie" qualifier="postcode">
            <xsl:value-of select="text()"/>
        </dcvalue>
    </xsl:template>

    <xsl:template match="IdentificatieMetaData/Exploitatie/Locatie/Straat" mode="imjv">
        <dcvalue element="exploitatie" qualifier="straat">
            <xsl:value-of select="text()"/>
            <xsl:value-of select="Naam"/>
        </dcvalue>
    </xsl:template>

    <xsl:template match="IdentificatieMetaData/Exploitatie/Locatie/Nummer" mode="imjv">
        <dcvalue element="exploitatie" qualifier="huisnummer">
            <xsl:value-of select="text()"/>
        </dcvalue>
    </xsl:template>


    <!-- Document metadata -->

    <xsl:template name="document-title">
        <xsl:param name="type"/>
        <xsl:param name="level"/>
        <xsl:choose>
            <xsl:when test="//IdentificatieMetaData/Exploitatie">
                <dcvalue element="title">
                    <xsl:text>Integraal Milieu Jaarverslag - </xsl:text>
                    <xsl:choose>
                        <xsl:when test="$type">
                            <xsl:value-of select="$type"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text>Aangifte</xsl:text>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:text> </xsl:text>
                    <xsl:choose>
                        <xsl:when test="$level='0'">
                            <xsl:value-of select="AangifteType/text()"/>
                        </xsl:when>
                        <xsl:when test="$level='1'">
                            <xsl:value-of select="../AangifteType/text()"/>
                        </xsl:when>
                        <xsl:when test="$level='2'">
                            <xsl:value-of select="../../AangifteType/text()"/>
                        </xsl:when>
                        <xsl:when test="$level='3'">
                            <xsl:value-of select="../../../AangifteType/text()"/>
                        </xsl:when>
                        <xsl:when test="$level='4'">
                            <xsl:value-of select="../../../../AangifteType/text()"/>
                        </xsl:when>
                    </xsl:choose>
                    <xsl:text> - </xsl:text>
                    <xsl:value-of select="//IdentificatieMetaData/RapporteringsJaar/text()"/>
                    <xsl:text> - </xsl:text>
                    <xsl:value-of select="//IdentificatieMetaData/Exploitatie/Naam/text()"/>
                    <xsl:text> - </xsl:text>
                    <xsl:value-of select="$cbbnr"/>
                </dcvalue>
            </xsl:when>
            <xsl:otherwise>
                <dcvalue element="title">
                    <xsl:text>Integraal Milieu Jaarverslag - </xsl:text>
                    <xsl:choose>
                        <xsl:when test="$type">
                            <xsl:value-of select="$type"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text>Aangifte</xsl:text>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:text> </xsl:text>
                    <xsl:choose>
                        <xsl:when test="$level='0'">
                            <xsl:value-of select="AangifteType/text()"/>
                        </xsl:when>
                        <xsl:when test="$level='1'">
                            <xsl:value-of select="../AangifteType/text()"/>
                        </xsl:when>
                        <xsl:when test="$level='2'">
                            <xsl:value-of select="../../AangifteType/text()"/>
                        </xsl:when>
                        <xsl:when test="$level='3'">
                            <xsl:value-of select="../../../AangifteType/text()"/>
                        </xsl:when>
                        <xsl:when test="$level='4'">
                            <xsl:value-of select="../../../../AangifteType/text()"/>
                        </xsl:when>
                    </xsl:choose>
                    <xsl:text> - </xsl:text>
                    <xsl:value-of select="//IdentificatieMetaData/RapporteringsJaar/text()"/>
                    <xsl:text> - </xsl:text>
                    <xsl:value-of select="//IdentificatieMetaData/Exploitant/Naam/text()"/>
                    <xsl:text> - </xsl:text>
                    <xsl:value-of select="$cbbnr"/>
                </dcvalue>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="document-author">
        <xsl:if test="//IdentificatieMetaData/Exploitant/Naam">
            <dcvalue element="contributor" qualifier="author">
                <xsl:value-of select="//IdentificatieMetaData/Exploitant/Naam" />
            </dcvalue>
        </xsl:if>
    </xsl:template>

    <xsl:template name="document-publisher">
        <dcvalue element="publisher">
            <xsl:text>Ministerie van de vlaamse gemeenschap</xsl:text>
        </dcvalue>
    </xsl:template>

    <xsl:template name="document-date-issued">
        <xsl:param name="level"/>

        <xsl:choose>
            <xsl:when test="$level='0'">
                <xsl:if test="Feiten/Feit[Actie='Statuswijziging (ONTV)']/Tijdstip/text()">
                    <dcvalue element="date" qualifier="issued">
                        <xsl:call-template name="format-date">
                            <xsl:with-param name="date" select="Feiten/Feit[Actie='Statuswijziging (ONTV)']/Tijdstip/text()"/>
                        </xsl:call-template>
                    </dcvalue>
                </xsl:if>
            </xsl:when>
            <xsl:when test="$level='1'">
                <xsl:if test="../Feiten/Feit[Actie='Statuswijziging (ONTV)']/Tijdstip/text()">
                    <dcvalue element="date" qualifier="issued">
                        <xsl:call-template name="format-date">
                            <xsl:with-param name="date" select="../Feiten/Feit[Actie='Statuswijziging (ONTV)']/Tijdstip/text()"/>
                        </xsl:call-template>
                    </dcvalue>
                </xsl:if>
            </xsl:when>
            <xsl:when test="$level='2'">
                <xsl:if test="../../Feiten/Feit[Actie='Statuswijziging (ONTV)']/Tijdstip/text()">
                    <dcvalue element="date" qualifier="issued">
                        <xsl:call-template name="format-date">
                            <xsl:with-param name="date" select="../../Feiten/Feit[Actie='Statuswijziging (ONTV)']/Tijdstip/text()"/>
                        </xsl:call-template>
                    </dcvalue>
                </xsl:if>
            </xsl:when>
            <xsl:when test="$level='3'">
                <xsl:if test="../../../Feiten/Feit[Actie='Statuswijziging (ONTV)']/Tijdstip/text()">
                    <dcvalue element="date" qualifier="issued">
                        <xsl:call-template name="format-date">
                            <xsl:with-param name="date" select="../../../Feiten/Feit[Actie='Statuswijziging (ONTV)']/Tijdstip/text()"/>
                        </xsl:call-template>
                    </dcvalue>
                </xsl:if>
            </xsl:when>
            <xsl:when test="$level='4'">
                <xsl:if test="../../../../Feiten/Feit[Actie='Statuswijziging (ONTV)']/Tijdstip/text()">
                    <dcvalue element="date" qualifier="issued">
                        <xsl:call-template name="format-date">
                            <xsl:with-param name="date" select="../../../Feiten/Feit[Actie='Statuswijziging (ONTV)']/Tijdstip/text()"/>
                        </xsl:call-template>
                    </dcvalue>
                </xsl:if>
            </xsl:when>
        </xsl:choose>
    </xsl:template>

    <!--<xsl:template match="Aangifte/AangifteType" mode="imjv">-->
        <!--<dcvalue element="AangifteType">-->
            <!--<xsl:value-of select="text()"/>-->
        <!--</dcvalue>-->
    <!--</xsl:template>-->

    <!--<xsl:template match="Aangifte/AangifteType/Feiten/Feit[Actie/text()='Creatie']/Gebruiker" mode="dc">-->
        <!--<dcvalue element="contributor" qualifier="author">-->
            <!--<xsl:value-of select="text()"/>-->
        <!--</dcvalue>-->
    <!--</xsl:template>-->

    <xsl:template name="dossier-dmsexportnotes">
        <xsl:if test="not(Exploitatie)">
            <dcvalue element="dmsexportnotes">
                <xsl:text>Exploitatie ontbrak in DMS op moment van export</xsl:text>
            </dcvalue>
        </xsl:if>
    </xsl:template>

    <xsl:template name="document-dmsexportnotes">
        <xsl:if test="not(//IdentificatieMetaData/Exploitatie)">
            <dcvalue element="dmsexportnotes">
                <xsl:text>Exploitatie ontbrak in DMS op moment van export</xsl:text>
            </dcvalue>
        </xsl:if>
    </xsl:template>

    <xsl:template name="dc-type">
        <xsl:param name="type"/>
        <dcvalue element="type">
            <xsl:value-of select="$type"/>
        </dcvalue>
    </xsl:template>

    <xsl:template name="dossier-dossiernummer">
        <dcvalue element="dossiernummer">
            <xsl:call-template name="dossier-dossiernummer-internal"/>
        </dcvalue>
    </xsl:template>

    <xsl:template name="dossier-dossiernummer-internal">
        <xsl:value-of select="//IdentificatieMetaData/RapporteringsJaar/text()"/>
        <xsl:text>_</xsl:text>
        <xsl:value-of select="$cbbnr"/>
    </xsl:template>

    <xsl:template name="title-alternative">
        <xsl:param name="title"/>
        <dcvalue element="title" qualifier="alternative">
            <xsl:value-of select="$title"/>
        </dcvalue>
    </xsl:template>

    <xsl:template name="dossier-source">
        <dcvalue element="source" >
            <xsl:value-of select="'Integraal Milieu Jaarverslag'"/>
        </dcvalue>
    </xsl:template>

    <xsl:template name="dossier-identifier">
        <xsl:param name="type"/>

        <dcvalue element="identifier">
            <xsl:value-of select="RapporteringsJaar/text()"/>
            <xsl:text>_</xsl:text>
            <xsl:value-of select="$cbbnr"/>
            <xsl:if test="$type">
                <xsl:text>_</xsl:text>
                <xsl:value-of select="$type"/>
            </xsl:if>
        </dcvalue>
    </xsl:template>

    <xsl:template name="dossier-date-issued">
        <xsl:if test="//MilieuVerslagMetaData/Feiten/Feit[Actie/text()='StatusWijziging (ONTV)']">
            <dcvalue element="date" qualifier="issued">
                <xsl:call-template name="format-date">
                    <xsl:with-param name="date" select="//MilieuVerslagMetaData/Feiten/Feit[Actie/text()='StatusWijziging (ONTV)']/Tijdstip"/>
                </xsl:call-template>
                <!--<xsl:value-of select="//MilieuVerslagMetaData/Feiten/Feit[Actie/text()='StatusWijziging (ONTV)']/Tijdstip" />-->
            </dcvalue>
        </xsl:if>
    </xsl:template>

    <xsl:template name="format-date">
        <xsl:param name="date"/>
        <xsl:choose>
            <xsl:when test="string-length($date) = 19">
                <xsl:value-of select="substring($date, 0, 11)"/>
                <xsl:text>T</xsl:text>
                <xsl:value-of select="substring($date, 12)"/>
                <xsl:text>Z</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <!--<xsl:value-of select="string-length($date)"/>-->
                <xsl:value-of select="$date"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="aangifte-identifier">
        <dcvalue element="identifier">
            <xsl:value-of select="//IdentificatieMetaData/RapporteringsJaar/text()"/>
            <xsl:text>_</xsl:text>
            <xsl:value-of select="$cbbnr"/>
            <xsl:text>_</xsl:text>
            <xsl:value-of select="AangifteType/text()"/>
            <xsl:if test="Volgnummer">
                <xsl:text>_</xsl:text>
                <xsl:value-of select="Volgnummer/text()"/>
            </xsl:if>
        </dcvalue>
    </xsl:template>

    <xsl:template name="document-identifier">
        <xsl:param name="level"/>

        <dcvalue element="identifier">
            <xsl:call-template name="document-identifier-internal"/>
        </dcvalue>
    </xsl:template>

    <xsl:template name="document-identifier-internal">
        <xsl:value-of select="utils:getFileNameWithoutExtension(text())"/>
    </xsl:template>

    <xsl:template name="aangifte-document-title">
        <xsl:param name="aangiftetype"/>
        <xsl:param name="identifier"/>
        <xsl:variable name="dossiernummer">
            <xsl:call-template name="dossier-dossiernummer-internal"/>
        </xsl:variable>
        <xsl:if test="string-length($identifier) &gt; string-length($dossiernummer) + string-length($aangiftetype) + 2">
            <dcvalue element="document" qualifier="title">
                <xsl:value-of select="substring($identifier, string-length($dossiernummer) + string-length($aangiftetype) + 3)"/>
            </dcvalue>
        </xsl:if>
    </xsl:template>

    <xsl:template name="aanvulling-identifier-internal">
        <xsl:param name="jaar"/>
        <xsl:param name="nummer"/>
        <xsl:param name="aangifteType"/>
        <xsl:param name="i"/>
        <xsl:value-of select="utils:getFileNameWithoutExtension(utils:getFileNameBasedOnIndex($directory,$jaar,$nummer,$aangifteType,$i))"/>
    </xsl:template>

    <xsl:template match="//Rijksregisternummer" mode="dc"/>
    <xsl:template match="//Rijksregisternummer" mode="imjv"/>


    <!-- utility templates -->

    <xsl:template name="vervlakte-voorstelling">
        <xsl:choose>
            <xsl:when test="child::*">
                <xsl:for-each select="child::*">
                    <xsl:if test="name(.)!='Rijksregisternummer'">
                        <xsl:call-template name="vervlakte-voorstelling"/>
                        <xsl:if test="not(position()=last())">
                            <xsl:text> </xsl:text>
                        </xsl:if>
                    </xsl:if>
                </xsl:for-each>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="."/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="substring-before-last">
        <!--passed template parameter -->
        <xsl:param name="list"/>
        <xsl:param name="delimiter"/>
        <xsl:choose>
            <xsl:when test="contains($list, $delimiter)">
                <!-- get everything in front of the first delimiter -->
                <xsl:value-of select="substring-before($list,$delimiter)"/>
                <xsl:choose>
                    <xsl:when test="contains(substring-after($list,$delimiter),$delimiter)">
                        <xsl:value-of select="$delimiter"/>
                    </xsl:when>
                </xsl:choose>
                <xsl:call-template name="substring-before-last">
                    <!-- store anything left in another variable -->
                    <xsl:with-param name="list" select="substring-after($list,$delimiter)"/>
                    <xsl:with-param name="delimiter" select="$delimiter"/>
                </xsl:call-template>
            </xsl:when>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>