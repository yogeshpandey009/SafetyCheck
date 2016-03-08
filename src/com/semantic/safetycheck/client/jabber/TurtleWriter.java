package com.semantic.safetycheck.client.jabber;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple Streaming Turtle syntax RDF writer. The intended usage is to use
 * the static methods on this class to create a new <code>TurtleWriter</code>
 * instance. Add prefixes to the document before writing anything to it, then
 * use the prefixes to create new individuals and add properties to the 
 * individuals. Each individual must be closed before the next one can be opened. 
 *
 */
public class TurtleWriter
{ 
   private final PrintWriter _writer;
   private String _lastLine;
   private Map<String, String> _prefixMap = new HashMap<String, String>();

   private TurtleWriter(PrintWriter writer)
   {
      _writer = writer;
   }
   
   /**
    * @param outputFile the file to write to
    * @return A new Turtle Writer 
    * @throws FileNotFoundException
    */
   public static TurtleWriter createTurtleWriter(String outputFile) 
      throws FileNotFoundException
   {
      TurtleWriter writer = new TurtleWriter(new PrintWriter(outputFile));
      writer.openDocument();
      
      return writer;
   }
   
   /**
    * @param outputStream The output stream to write to
    * @return A new Turtle Writer
    */
   public static TurtleWriter createTurtleWriter(OutputStream outputStream)
   {
      TurtleWriter writer = new TurtleWriter(new PrintWriter(outputStream));
      writer.openDocument();
      
      return writer;
   }
   
   /**
    * Appends the default namespaces to the Turtle document
    */
   private void openDocument()
   {
      addPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
      addPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
      addPrefix("owl", "http://www.w3.org/2002/07/owl#");
      addPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
   }
   
   /**
    * Append a prefix to the document. This cannot be called after anything else
    * is written because this is a streaming writer. rdf, rdfs, owl, and xsd are
    * added by default.
    * @param prefix The prefix of the namespace
    * @param uri The full uri of the namespace
    */
   public void addPrefix(String prefix, String uri)
   {
      _prefixMap.put(prefix, uri);
      _writer.println(String.format("@prefix %1$s: <%2$s> .", prefix, uri));
   }
   
   /**
    * Open a new individual and declare that it's of this type
    * @param iPrefix The uri of the new individual
    * @param iFragment The fragment of the new individual uri
    * @param typePrefix The type uri prefix
    * @param typeFragment The fragment of the type uri
    */
   public void openIndividual(String iPrefix, String iFragment, 
      String typePrefix, String typeFragment)
   {
      _writer.println();
      _lastLine = String.format("  <%1$s%2$s> a <%3$s%4$s> ", 
         _prefixMap.get(iPrefix), iFragment, 
         _prefixMap.get(typePrefix), typeFragment);
   }
   
   /**
    * Add a literal to the already open individual
    * @param pPrefix The prefix of the property uri
    * @param pFragment The fragment of the property uri
    * @param value The literal value
    * @param datatype The datatype of the literal value
    */
   public void addLiteral(String pPrefix, String pFragment, 
      String value, String datatype)
   {
      _writer.println(_lastLine + ";");
      _lastLine = String.format("      %1$s:%2$s \"%3$s\"^^%4$s ",
         pPrefix, pFragment, value, datatype);
   }
   
   /**
    * Add a reference property value to the already open individual
    * @param pPrefix The prefix of the property uri
    * @param pFragment The fragment of the property uri
    * @param refPrefix The prefix of the value uri
    * @param refFragment The fragment of the value uri
    */
   public void addReference(String pPrefix, String pFragment, 
      String refPrefix, String refFragment)
   {
      _writer.println(_lastLine + ";");
      _lastLine = String.format("      %1$s:%2$s <%3$s%4$s> ", 
         pPrefix, pFragment, 
         _prefixMap.get(refPrefix), refFragment);
   }
   
   /**
    * Close the individual
    */
   public void closeIndividual()
   {
      _writer.println(_lastLine + ".");
      _lastLine = null;
   }
   
   /**
    * Flush the writer
    */
   public void flush()
   {
      _writer.flush();
   }
   
   /**
    * Close the writer
    */
   public void close()
   {
      _writer.close();
   }
}
