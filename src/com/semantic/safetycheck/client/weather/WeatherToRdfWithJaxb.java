package com.semantic.safetycheck.client.weather;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;


import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class WeatherToRdfWithJaxb
{
   /**
    * Program that converts the Weather XML feed to RDF
    * using JAXB and the Velocity template engine
    * @param args
    *    input xml - The URL of the input Weather XML feed
    *       (http://www.weather.gov/xml/current_obs/KBWI.xml)
    *    velocity template - The file name of the velocity template file
    *    output file - The file name of the output RDF file
    */
   public static void main(String[] args)
   {
      if(args.length != 3) 
      {   
         System.err.println("Usage: java WeatherToRdfWithJaxb "
            + "[input xml] [velocity template file] [output file]");
         return;
      }

      String xmlFile = args[0];
      String vmFile = args[1];
      String outputFile = args[2];
      
      try
      {
         //get the xml file input
         URL xmlFileUrl = new URL(xmlFile);
         InputStream xmlFileInputStream = xmlFileUrl.openStream();
         
         //get the vm file input
         FileInputStream vmFileInput = new FileInputStream(vmFile);
         
         //create the output file
         FileOutputStream outputStream = new FileOutputStream(outputFile);

         //unmarshal the information from xml
         JAXBContext jaxbContext = 
            JAXBContext.newInstance("net.semwebprogramming.chapter9.weather");
         Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
         CurrentObservation currentObservation = 
            (CurrentObservation)unmarshaller.unmarshal(xmlFileInputStream);
         
         //execute our velocity template
         VelocityEngine engine = new VelocityEngine();
         engine.init();
         VelocityContext velocityContext = new VelocityContext();
         velocityContext.put("observation", currentObservation);
         
         //set up an output stream that we can redirect to the jena model
         ByteArrayOutputStream vmOutputStream = new ByteArrayOutputStream();
         
         Writer resultsWriter = new OutputStreamWriter(vmOutputStream);
         engine.evaluate(
            velocityContext, 
            resultsWriter,
            "weatherRdf",
            new InputStreamReader(vmFileInput));
         resultsWriter.close();
            
         //build a jena model so we can serialize to Turtle
         ByteArrayInputStream modelInputStream = 
            new ByteArrayInputStream(vmOutputStream.toByteArray());
         
         Model rdfModel = ModelFactory.createDefaultModel();
         rdfModel.read(modelInputStream, null, "RDF/XML");
         rdfModel.write(outputStream, "TURTLE");
         outputStream.flush();
         
         System.out.println("Success.");
      }
      catch(MalformedURLException e)
      {
         e.printStackTrace();
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (JAXBException e)
      {
         e.printStackTrace();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
}
