package com.semantic.safetycheck.client.weather;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.semantic.javaobjectstordf.JavaObjectRdfSerializer;

public class WeatherJavaObjectsToRdf
{
   /**
    * Program that converts weather XML feed to RDF using
    * a combination of JAXB and the Java object serializer
    * @param args
    *    input xml - The URL of the input Weather XML file
    *       (http://www.weather.gov/xml/current_obs/KBWI.xml)
    *    output file - the file name of the output RDF file
    *       (ouputs in TURTLE format)
    */
   public static void main(String[] args)
   {
      if(args.length != 2) 
      {   
         System.err.println("Usage: java WeatherJavaObjectsToRdf "
            + "[input xml] [output file]");
         return;
      }

      String xmlFile = args[0];
      String outputFile = args[1];
      
      try
      {
         //get the xml file input
         URL xmlFileUrl = new URL(xmlFile);
         InputStream xmlFileInputStream = xmlFileUrl.openStream();
         
         //create the output file
         FileOutputStream outputStream = new FileOutputStream(outputFile);

         //unmarshal the information from xml
         JAXBContext jaxbContext = 
            JAXBContext.newInstance("com.semantic.safetycheck.client.weather");
         Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
         CurrentObservation currentObservation = 
            (CurrentObservation)unmarshaller.unmarshal(xmlFileInputStream);

         JavaObjectRdfSerializer serializer = new JavaObjectRdfSerializer(
               "http://www.weather.gov/weather#", 
               "http://www.weather.gov/ontology#", 
               //"TURTLE", 
               "RDF/XML",
               "com.semantic.safetycheck");
         
         Collection<Object> objects = new LinkedList<Object>();
         objects.add(currentObservation);
         serializer.serialize(objects, outputStream);
         outputStream.close();
         
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

