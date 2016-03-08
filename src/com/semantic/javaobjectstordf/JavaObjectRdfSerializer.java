package com.semantic.javaobjectstordf;

import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;

public class JavaObjectRdfSerializer
{
   private final String _format;
   private final String _baseUri;
   private final String _ontUri;
   private final String _package;
   
   /**
    * The model that is generated from the objects to be serialized
    */
   private final OntModel _model;
   
   /**
    * Map that maintains a record of the objects that have already been processed
    */
   private final Map<Object, Individual> _processedObjects;
   
   /**
    * Create a new Instance of the Java Object to RDF Serializer
    * @param baseUri The base URI of the generated RDF file 
    * (and any created instances)
    * @param ontUri The URI of the ontology that describes the generated data
    * @param format The format for the generated RDF document
    * @param pkg The JAVA package to limit the serialization to 
    * (recursive include) 
    */
   public JavaObjectRdfSerializer(String baseUri, String ontUri, String format, String pkg)
   {
      _baseUri = baseUri;
      _ontUri = ontUri;
      _format = format;
      _package = pkg;
      _model = ModelFactory.createOntologyModel();
      _model.setNsPrefix("dOnt", ontUri);
      _processedObjects = new Hashtable<Object, Individual>();
   }
   
   /**
    * Serialize the collection of objects to the output stream as RDF
    * @param objects The objects to serialize
    * @param outputStream The output stream to write the generated RDF to
    */
   public void serialize(Collection<Object> objects, OutputStream outputStream)
   {
      for(Object o : objects)
      {
         processObject(o);
      }
      
      _model.write(outputStream, _format, _baseUri);
   }
   
   /**
    * Create an individual from the object
    * @param o The object to convert to an individual
    * @return
    */
   private Individual processObject(Object o)
   {
      Individual individual = null;
      
      /*
       * Determine if we should process this object
       * We don't process it if one of the following is true:
       *  1) We've already processed it
       *  2) It's not in our target package
       */
      boolean shouldProcess = true;
      shouldProcess = 
         null != o
         && !_processedObjects.containsKey(o) 
         && o.getClass().getPackage().getName().startsWith(_package);
      
      //now process it if we should
      if(shouldProcess)
      {
         //get the uri from the hashcode
         String resourceUri = String.format("%1$s%2$s", _baseUri, o.hashCode());
         
         /*
          * get the class from the name of the class (this implementation
          * doesn't support more than one Class by the same name, 
          * but in different packages)
          */
         String classUri = String.format("%1$s%2$s", _ontUri, o.getClass().getSimpleName());
         
         OntClass c = _model.createClass(classUri);
         individual = c.createIndividual(resourceUri);
         _processedObjects.put(o, individual);
         
         for(Method m : o.getClass().getMethods())
         {
            try
            {
               processMethod(o, individual, m);
            }
            catch (IllegalArgumentException e)
            {
               e.printStackTrace();
            }
            catch (IllegalAccessException e)
            {
               e.printStackTrace();
            }
            catch (InvocationTargetException e)
            {
               e.printStackTrace();
            }
         }
      }
      else if(_processedObjects.containsKey(o))
      {
         individual = _processedObjects.get(o);
      }
      
      return individual;
   }
   
   /**
    * Process the specific method on the object
    * @param o The object to process
    * @param individual The individual that was generated from the object
    * @param m The method to process
    * @throws IllegalAccessException
    * @throws IllegalArgumentException
    * @throws InvocationTargetException
    */
   private void processMethod(Object o, Individual individual, Method m) 
      throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
   {
      /*
       * Determine if we should process this method
       * We don't process it if one of the following is true:
       *  1) It's not a getter
       *  2) It has more than 0 parameters
       *  3) It isn't public
       */
      boolean shouldProcess = true;
      shouldProcess =
         null != m
         && m.getName().startsWith("get")
         && m.getParameterTypes().length == 0
         && Modifier.isPublic(m.getModifiers());

      if(shouldProcess)
      {
         String propertyName = m.getName().substring(3);
         Property p = _model.createProperty(
            String.format("%1$s%2$s", _ontUri, propertyName));
         Object value = m.invoke(o);
         
         if(null == value)
         {
            //we just want to skip these
         }
         else if(m.getReturnType().isPrimitive() || isBoxedPrimitive(value))
         {
            addBoxedPrimitiveValue(individual, p, value);
         }
         else
         {
            //add a resource
            Individual newIndividual = processObject(value);
            if(null != newIndividual)
            {
               individual.addProperty(p, newIndividual);
            }
         }
      }
   }

   /**
    * Processes a boxed primitive, generating a property value on the individual
    * @param i The individual for which the boxed primitive is a property value
    * @param p The property of the property value
    * @param value The boxed primitive
    */
   private void addBoxedPrimitiveValue(Individual i, Property p, Object value)
   {
      //check each type of boxed primitive and convert it
      if(value instanceof String)
      {
         i.addLiteral(p, value);
      }
      else if(value instanceof Integer)
      {
         i.addLiteral(p, ((Integer)value).longValue());
      }
      else if(value instanceof Float)
      {
         i.addLiteral(p, ((Float)value).floatValue());
      }
      else if(value instanceof Double)
      {
         i.addLiteral(p, ((Double)value).doubleValue());
      }
      else if(value instanceof Long)
      {
         i.addLiteral(p, ((Long)value).longValue());
      }
      else if(value instanceof Calendar)
      {
         Calendar c = (Calendar)value;
         XSDDateTime dateTime = new XSDDateTime(c);
         i.addLiteral(p, dateTime);
      }
      else if (value instanceof Boolean)
      {
         i.addLiteral(p, ((Boolean)value).booleanValue());
      }
      else if(value instanceof BigDecimal)
      {
         i.addLiteral(p, ((BigDecimal)value).doubleValue());
      }
      else if(value instanceof BigInteger)
      {
         i.addLiteral(p, ((BigInteger)value).longValue());
      }
      
   }

   /**
    * Checks to see if the object is a boxed primitive
    * @param value The value to check
    * @return
    */
   private boolean isBoxedPrimitive(Object value)
   {
      boolean isPrimitive = false;
      
      isPrimitive = 
         (value instanceof String)
         || (value instanceof Integer)
         || (value instanceof Float)
         || (value instanceof Double)
         || (value instanceof Long)
         || (value instanceof Calendar)
         || (value instanceof Boolean)
         || (value instanceof BigInteger)
         || (value instanceof BigDecimal);
      
      return isPrimitive;
   }
   
}
