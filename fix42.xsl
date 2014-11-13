<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://maven.apache.org/POM/4.0.0"
                xmlns:mvn="http://maven.apache.org/POM/4.0.0" exclude-result-prefixes="mvn">

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template name="copy-attributes">
        <xsl:for-each select="@*">
            <xsl:attribute name="{name(.)}">
                <xsl:value-of select="."/>
            </xsl:attribute>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="string-replace-all">
        <xsl:param name="text"/>
        <xsl:param name="replace"/>
        <xsl:param name="by"/>
        <xsl:choose>
            <xsl:when test="contains($text, $replace)">
                <xsl:value-of select="substring-before($text,$replace)"/>
                <xsl:value-of select="$by"/>
                <xsl:call-template name="string-replace-all">
                    <xsl:with-param name="text"
                                    select="substring-after($text,$replace)"/>
                    <xsl:with-param name="replace" select="$replace"/>
                    <xsl:with-param name="by" select="$by"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$text"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="mvn:activation">
        <activation>
            <file>
                <exists>
                    <xsl:value-of select="concat(following-sibling::mvn:modules/mvn:module/text(),'/pom.xml')"/>
                </exists>
            </file>
        </activation>
    </xsl:template>
</xsl:stylesheet>