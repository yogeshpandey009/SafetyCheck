<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
  xmlns:fxml="http://api.facebook.com/1.0/"
  xmlns:f="http://www.semwebprogramming.net/2009/04/fb-ont#"
  xml:base="http://www.semwebprogramming.net/friends" 
  version="1.0">

  <xsl:output method="xml" version="1.0" encoding="UTF-8"
    indent="yes" />

  <xsl:template match="/fxml:users_getInfo_response">
    <rdf:RDF
      xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
      xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
      xmlns:owl="http://www.w3.org/2002/07/owl#"
      xmlns:xsd="http://www.w3.org/2001/XMLSchema#"
      xmlns:f="http://www.semwebprogramming.net/2009/04/fb-ont#"
      xml:base="http://www.semwebprogramming.net/friends" 
      >
      <xsl:for-each select="fxml:user">
        <f:Friend rdf:about="#user{fxml:uid}">
          <f:uid><xsl:value-of select="fxml:uid"/></f:uid>
          <f:name><xsl:value-of select="fxml:name"/></f:name>


          <xsl:if test="fxml:pic != ''">
            <f:picture><xsl:value-of select="fxml:pic"/></f:picture>
          </xsl:if>

          <xsl:if test="fxml:birthday != ''">
            <f:birthday><xsl:value-of select="fxml:birthday"/></f:birthday>
          </xsl:if>
          <xsl:if test="fxml:about_me != ''">
            <f:about_me><xsl:value-of select="fxml:about_me"/></f:about_me>
          </xsl:if>
          <xsl:if test="fxml:activities != ''">
            <f:activities><xsl:value-of select="fxml:activities"/></f:activities>
          </xsl:if>
          <xsl:if test="fxml:books != ''">
            <f:books><xsl:value-of select="fxml:books"/></f:books>
          </xsl:if>
          <xsl:if test="fxml:interests != ''">
            <f:interests><xsl:value-of select="fxml:interests"/></f:interests>
          </xsl:if>
          <xsl:if test="fxml:movies != ''">
            <f:movies><xsl:value-of select="fxml:movies"/></f:movies>
          </xsl:if>
          <xsl:if test="fxml:music != ''">
            <f:music><xsl:value-of select="fxml:music"/></f:music>
          </xsl:if>
          <xsl:if test="fxml:tv != ''">
            <f:tv><xsl:value-of select="fxml:tv"/></f:tv>
          </xsl:if>
          <xsl:if test="fxml:political != ''">
            <f:political><xsl:value-of select="fxml:political"/></f:political>
          </xsl:if>

          <xsl:for-each select="fxml:hometown_location">
            <f:location>
              <f:Location>
                <xsl:if test="fxml:city != ''">      
                  <f:city><xsl:value-of select="fxml:city"/></f:city>
                </xsl:if>
                <xsl:if test="fxml:state != ''">      
                  <f:state><xsl:value-of select="fxml:state"/></f:state>
                </xsl:if>
                <xsl:if test="fxml:country != ''">      
                  <f:country><xsl:value-of select="fxml:country"/></f:country>
                </xsl:if>
              </f:Location>
            </f:location>
          </xsl:for-each>
          
          <xsl:if test="fxml:current_location">
            <f:location>
              <f:Location>
                <xsl:if test="fxml:city != ''">      
                  <f:city><xsl:value-of select="fxml:city"/></f:city>
                </xsl:if>
                <xsl:if test="fxml:state != ''">      
                  <f:state><xsl:value-of select="fxml:state"/></f:state>
                </xsl:if>
                <xsl:if test="fxml:country != ''">      
                  <f:country><xsl:value-of select="fxml:country"/></f:country>
                </xsl:if>
              </f:Location>
            </f:location>
          </xsl:if>
          
          <xsl:for-each select="fxml:affiliations/fxml:affiliation">
            <f:hasAffiliation>
              <f:Affiliation>
                <rdf:type rdf:resource="http://www.semwebprogramming.net/2009/04/fb-ont#{fxml:type}"/>
                <f:affiliationId rdf:resource="#affiliation{fxml:nid}" />
                <f:name><xsl:value-of select="fxml:name"/></f:name>
                <xsl:if test="fxml:status != ''">
                  <f:status><xsl:value-of select="fxml:status"/></f:status>
                </xsl:if>
                <xsl:if test="fxml:date != 0">
                  <f:date><xsl:value-of select="fxml:date"/></f:date>
                </xsl:if>
              </f:Affiliation>
            </f:hasAffiliation>
          </xsl:for-each>
			  
			</f:Friend>         
      </xsl:for-each>
    </rdf:RDF>
  </xsl:template>

</xsl:stylesheet>  
