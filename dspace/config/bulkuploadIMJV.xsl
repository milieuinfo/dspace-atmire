<xsl:stylesheet
        version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:redirect="http://xml.apache.org/xalan/redirect"
        extension-element-prefixes="redirect" exclude-result-prefixes="redirect">

    <xsl:output encoding="UTF-8" indent="yes" omit-xml-declaration="yes" />

    <xsl:template match="@* | node()">
        <xsl:apply-templates select="*"/>
    </xsl:template>

    <xsl:template match="IdentificatieMetaData">
        <redirect:write select="concat('IdentificatieMetaData',position(),'/dublin_core.xml')">
            <dublin_core schema="dc">
                <xsl:apply-templates/>
                <dcvalue element="identifier" qualifier="other">
                    <xsl:value-of select="Exploitatie/CBBExploitatieNummer"/>
                </dcvalue>
            </dublin_core>
        </redirect:write>
    </xsl:template>

    <xsl:template match="AangiftenMetaData/Aangifte">
        <redirect:write select="concat('aangifte',position(), '/dublin_core.xml')">
            <dublin_core schema="dc">
                <xsl:apply-templates/>
                <dcvalue element="identifier" qualifier="other">
                    <xsl:value-of select="../../IdentificatieMetaData/Exploitatie/CBBExploitatieNummer"/>
                </dcvalue>
            </dublin_core>
        </redirect:write>
        <redirect:write select="concat('aangifte',position(), '/contents')">
            <xsl:if test="AangiftePdf">
                <xsl:text>../../</xsl:text>
                <xsl:value-of select="AangiftePdf"/>
                <xsl:text>&#10;</xsl:text>
            </xsl:if>
            <xsl:if test="ProcesSchema/Bestand">
                <xsl:text>../../</xsl:text>
                <xsl:value-of select="ProcesSchema/Bestand"/>
            </xsl:if>
                </redirect:write>
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