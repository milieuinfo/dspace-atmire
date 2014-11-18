<xsl:stylesheet
        version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xalan="org.apache.xalan.xslt.extensions.Redirect"
        extension-element-prefixes="xalan" exclude-result-prefixes="xalan">

    <xsl:output encoding="UTF-8" indent="yes"/>

    <xsl:template match="@* | node()">
        <xsl:apply-templates select="*"/>
           </xsl:template>

    <xsl:template match="IdentificatieMetaData">
        <xalan:write select="concat('IdentificatieMetaData',position(),'/dublin_core.xml')">
            <dublin_core schema="dc">
                <xsl:apply-templates/>
                <dcvalue element="identifier" qualifier="other">
                    <xsl:value-of select="Exploitatie/CBBExploitatieNummer"/>
                </dcvalue>
            </dublin_core>
        </xalan:write>
    </xsl:template>

    <xsl:template match="AangiftenMetaData/Aangifte">
        <xalan:write select="concat('aangifte',position(), '/dublin_core.xml')">
            <dublin_core schema="dc">
                <xsl:apply-templates/>
                <dcvalue element="identifier" qualifier="other">
                    <xsl:value-of select="../../IdentificatieMetaData/Exploitatie/CBBExploitatieNummer"/>
                </dcvalue>
            </dublin_core>
        </xalan:write>
    </xsl:template>

<xsl:template match="IdentificatieMetaData/Exploitatie/Naam">
    <dcvalue element="contributor" qualifier="author">
        <xsl:value-of select="text()"/>
    </dcvalue>
</xsl:template>

    <xsl:template match="Aangifte/AangifteType">
        <dcvalue element="type" >
            <xsl:value-of select="text()"/>
        </dcvalue>
    </xsl:template>

</xsl:stylesheet>