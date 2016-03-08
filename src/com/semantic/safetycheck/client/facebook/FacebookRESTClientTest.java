/* =============================================================================
*
*                  COPYRIGHT 2008 BBN Technologies Corp.
*                  1300 North 17th Street, Suite 600
*                       Arlington, VA  22209
*                          (703) 284-1200
*
*       This program is the subject of intellectual property rights
*       licensed from BBN Technologies
*
*       This legend must continue to appear in the source code
*       despite modifications or enhancements by any party.
*
*
* =============================================================================
*
* Created : Oct 5, 2008
* Workfile: FacebookRdfTest.java
* $Revision: $
* $Date:  $
* $Author:  $
*
* =============================================================================
*/
 
package com.semantic.safetycheck.client.facebook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class FacebookRESTClientTest
{
   public static void main(String[] args) throws IOException, TransformerException
   {
      if(args.length != 3) 
      {   
         System.err.println("Usage: java FacebookRESTClientTest "
            + "[xml file] [output file] [output format]");
         return;
      }
      
      //create the output turtle file
      FileOutputStream outputStream = new FileOutputStream(args[1]);

      //get the xml file input
      URL xmlFileUrl = new URL(args[0]);
      InputStream xmlFileInputStream = xmlFileUrl.openStream();

      //get the xsl file input
      FileInputStream xslInputStream = new FileInputStream("./conf/facebook.xsl");

      //set up an output stream that we can redirect to the jena model
      ByteArrayOutputStream transformOutputStream = new ByteArrayOutputStream();

      //transform the xml document into rdf/xml
      TransformerFactory factory = TransformerFactory.newInstance();
      StreamSource xslSource = new StreamSource(xslInputStream);
      StreamSource xmlSource = new StreamSource(xmlFileInputStream);
      StreamResult outResult = new StreamResult(transformOutputStream);

      Transformer transformer = factory.newTransformer(xslSource);
      transformer.transform(xmlSource, outResult);
      transformOutputStream.close();

      //build a jena model so we can serialize to Turtle
      ByteArrayInputStream modelInputStream = new ByteArrayInputStream(
         transformOutputStream.toByteArray());

      Model rdfModel = ModelFactory.createDefaultModel();
      rdfModel.read(modelInputStream, null, "RDF/XML");
      rdfModel.write(outputStream, args[2]);
      outputStream.flush();

      System.out.println("Success.");      
      
   }
}
