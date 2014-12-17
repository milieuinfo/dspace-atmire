<xsl:stylesheet
        version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:redirect="http://xml.apache.org/xalan/redirect"
        extension-element-prefixes="redirect" exclude-result-prefixes="redirect">

    <xsl:output encoding="UTF-8" indent="yes" omit-xml-declaration="yes"/>

    <xsl:template match="@* | node()">
        <xsl:apply-templates select="*"/>
    </xsl:template>

    <xsl:template match="text()"/>

    <!-- Dossier -->
    <xsl:template match="IdentificatieMetaData">
        <redirect:write select="concat('IdentificatieMetaData',position(),'/dublin_core.xml')">
            <dublin_core schema="dc">
                <xsl:call-template name="dossier-title"/>
                <xsl:apply-templates select="." mode="dc"/>
            </dublin_core>
        </redirect:write>
        <redirect:write select="concat('IdentificatieMetaData',position(),'/metadata_imjv.xml')">
            <dublin_core schema="imjv">
                <xsl:apply-templates select="." mode="imjv"/>
                <xsl:apply-templates select="//MilieuVerslagMetaData" mode="imjv"/>
            </dublin_core>
        </redirect:write>
        <redirect:write select="concat('IdentificatieMetaData',position(),'/source.xml')">
            <xsl:copy>
                <xsl:apply-templates select="/" mode="copy"/>
            </xsl:copy>
        </redirect:write>
        <redirect:write select="concat('IdentificatieMetaData',position(), '/contents')">
            <xsl:text></xsl:text>
        </redirect:write>
    </xsl:template>

    <!-- Documenten -->
    <xsl:template match="AangiftenMetaData/Aangifte">
        <redirect:write select="concat('aangifte',position(), '/dublin_core.xml')">
            <dublin_core schema="dc">
                <xsl:call-template name="document-title"/>
                <xsl:call-template name="document-date-issued"/>
                <xsl:call-template name="document-publisher"/>
                <xsl:call-template name="document-author"/>
                <xsl:apply-templates mode="dc"/>
            </dublin_core>
        </redirect:write>
        <redirect:write select="concat('aangifte',position(), '/metadata_imjv.xml')">
            <dublin_core schema="imjv">
                <xsl:apply-templates mode="imjv"/>
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
        <redirect:write select="concat('aangifte',position(),'/source.xml')">
            <xsl:copy>
                <xsl:apply-templates select="@*|node()" mode="copy"/>
            </xsl:copy>
        </redirect:write>

    </xsl:template>

    <!-- Dossier metadata -->

    <xsl:template name="dossier-title">
        <dcvalue element="title">
            <xsl:value-of select="Exploitatie/Naam/text()"/>
            <xsl:text> - </xsl:text>
            <xsl:value-of select="RapporteringsJaar/text()"/>
        </dcvalue>
    </xsl:template>

    <xsl:template match="IdentificatieMetaData/Exploitatie/Naam" mode="dc">
        <dcvalue element="contributor" qualifier="author">
            <xsl:value-of select="text()"/>
        </dcvalue>
    </xsl:template>

    <xsl:template match="IdentificatieMetaData/RapporteringsJaar" mode="imjv">
        <dcvalue element="Rapporteringsjaar">
            <xsl:value-of select="text()"/>
        </dcvalue>
    </xsl:template>

    <xsl:template match="IdentificatieMetaData/Exploitatie/CBBExploitatieNummer" mode="imjv">
        <dcvalue element="ExploitatieCBBNummer">
            <xsl:value-of select="text()"/>
        </dcvalue>
    </xsl:template>

    <xsl:template match="IdentificatieMetaData/Exploitant/CBBExploitantNummer" mode="imjv">
        <dcvalue element="ExploitantCBBNummer">
            <xsl:value-of select="text()"/>
        </dcvalue>
    </xsl:template>

    <xsl:template match="IdentificatieMetaData/Exploitatie/Naam" mode="imjv">
        <dcvalue element="ExploitatieNaam">
            <xsl:value-of select="text()"/>
        </dcvalue>
    </xsl:template>

    <xsl:template match="IdentificatieMetaData/Exploitant/OndernemingsNummer" mode="imjv">
        <dcvalue element="ExploitantOndernemingsNummer">
            <xsl:value-of select="text()"/>
        </dcvalue>
    </xsl:template>

    <xsl:template match="IdentificatieMetaData/Exploitatie/Locatie" mode="imjv">
        <dcvalue element="ExploitatieLocatie">
            <xsl:call-template name="vervlakte-voorstelling"/>
        </dcvalue>
        <xsl:apply-templates mode="imjv" />
    </xsl:template>

    <xsl:template match="IdentificatieMetaData/Exploitatie/Locatie/Gemeente" mode="imjv">
        <dcvalue element="ExploitatieLocatieStad">
            <xsl:value-of select="text()"/>
        </dcvalue>
    </xsl:template>

    <xsl:template match="IdentificatieMetaData/Exploitatie/Locatie/Postcode" mode="imjv">
        <dcvalue element="ExploitatieLocatiePostcode">
            <xsl:value-of select="text()"/>
        </dcvalue>
    </xsl:template>

    <xsl:template match="IdentificatieMetaData/Exploitant/Adres" mode="imjv">
        <dcvalue element="ExploitantAdres">
            <xsl:call-template name="vervlakte-voorstelling"/>
        </dcvalue>
        <xsl:apply-templates mode="imjv" />
    </xsl:template>

    <xsl:template match="IdentificatieMetaData/Exploitant/Adres/Gemeente" mode="imjv">
        <dcvalue element="ExploitantAdresStad">
            <xsl:value-of select="text()"/>
        </dcvalue>
    </xsl:template>

    <xsl:template match="IdentificatieMetaData/Exploitant/Adres/Postcode" mode="imjv">
        <dcvalue element="ExploitantAdresPostcode">
            <xsl:value-of select="text()"/>
        </dcvalue>
    </xsl:template>

    <xsl:template match="MilieuVerslagMetaData/VerzendAdres" mode="imjv">
        <dcvalue element="MilieuVerslagVerzendAdres">
            <xsl:call-template name="vervlakte-voorstelling"/>
        </dcvalue>
    </xsl:template>

    <xsl:template match="MilieuVerslagMetaData/Feiten/Feit" mode="imjv">
        <dcvalue element="MilieuVerslagFeit">
            <xsl:call-template name="vervlakte-voorstelling"/>
        </dcvalue>
        <xsl:apply-templates mode="imjv" />
    </xsl:template>

    <xsl:template match="MilieuVerslagMetaData/Feiten/Feit/Tijdstip" mode="imjv">
        <dcvalue element="MilieuVerslagFeitTijdstip">
            <xsl:value-of select="text()"/>
        </dcvalue>
    </xsl:template>

    <xsl:template match="MilieuVerslagMetaData/Feiten/Feit/Gebruiker/Voornaam" mode="imjv">
        <dcvalue element="MilieuVerslagFeitGebruikerVoornaam">
            <xsl:value-of select="text()"/>
        </dcvalue>
    </xsl:template>

    <xsl:template match="MilieuVerslagMetaData/Feiten/Feit/Gebruiker/Naam" mode="imjv">
        <dcvalue element="MilieuVerslagFeitGebruikerNaam">
            <xsl:value-of select="text()"/>
        </dcvalue>
    </xsl:template>

    <xsl:template match="MilieuVerslagMetaData/Feiten/Feit/Actie" mode="imjv">
        <dcvalue element="MilieuVerslagFeitActie">
            <xsl:value-of select="text()"/>
        </dcvalue>
    </xsl:template>


    <!-- Document metadata -->

    <xsl:template name="document-title">
        <dcvalue element="title">
            <xsl:value-of select="//IdentificatieMetaData/Exploitatie/Naam/text()"/>
            <xsl:text> - </xsl:text>
            <xsl:value-of select="AangifteType/text()"/>
            <xsl:text> - </xsl:text>
            <xsl:value-of select="//IdentificatieMetaData/RapporteringsJaar/text()"/>
        </dcvalue>
    </xsl:template>

    <xsl:template name="document-author">
        <xsl:if test="//IdentificatieMetaData/Exploitatie/Naam">
            <dcvalue element="contributor" qualifier="author">
                <xsl:value-of select="//IdentificatieMetaData/Exploitatie/Naam" />
            </dcvalue>
        </xsl:if>
    </xsl:template>

    <xsl:template name="document-publisher">
        <dcvalue element="publisher">
            <xsl:text>IMJV</xsl:text>
        </dcvalue>
    </xsl:template>

    <xsl:template name="document-date-issued">
        <xsl:if test="//MilieuVerslagMetaData/Feiten/Feit[Actie/text()='StatusWijziging (ONTV)']">
            <dcvalue element="date" qualifier="issued">
                <xsl:value-of select="//MilieuVerslagMetaData/Feiten/Feit[Actie/text()='StatusWijziging (ONTV)']/Tijdstip" />
            </dcvalue>
        </xsl:if>
    </xsl:template>

    <xsl:template match="Aangifte/AangifteType" mode="imjv">
        <dcvalue element="AangifteType">
            <xsl:value-of select="text()"/>
        </dcvalue>
    </xsl:template>

    <xsl:template match="Aangifte/AangifteType/Feiten/Feit[Actie/text()='Creatie']/Gebruiker" mode="dc">
        <dcvalue element="contributor" qualifier="author">
            <xsl:value-of select="text()"/>
        </dcvalue>
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

    <xsl:template match="@*|node()" mode="copy">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()" mode="copy"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>