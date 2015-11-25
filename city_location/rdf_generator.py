__author__ = 'HM'
import csv


rdf = open("city.rdf", "wb")
try:

    rdf.write("<?xml version=\"1.0\"?>\n")
    rdf.write("<!DOCTYPE rdf:RDF [\n")
    rdf.write("	<!ENTITY xsd \"http://www.w3.org/2001/XMLSchema#\" >\n")
    rdf.write("	<!ENTITY rdfs \"http://www.w3.org/2000/01/rdf-schema#\" >\n")
    rdf.write("	<!ENTITY rdf \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" >\n")
    rdf.write("	<!ENTITY sc \"http://www.semanticweb.org/ontologies/2015/10/SafetyCheck#\" >\n")
    rdf.write("]>\n")
    rdf.write("<rdf:RDF\n")
    rdf.write("	xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" \n")
    rdf.write("	xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" \n")
    rdf.write("	xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" \n")
    rdf.write("	xmlns:sc=\"http://www.semanticweb.org/ontologies/2015/10/SafetyCheck#\">\n\n")


    with open('items_new.csv', 'rb') as f:

        reader = csv.reader(f)
        x = 1
        for row in reader:
            if row[0] != 'city_name':
                rdf.write("	<rdf:Description rdf:about=\"&sc;Region" + str(x) + "\">\n")
                #<rdf:type rdf:resource="&sc;Person"/>
                rdf.write("	    <rdf:type rdf:resource=\"&sc;Region\">\n")
                rdf.write("		<sc:hasLocationName rdf:datatype=\"&xsd;string\">" + row[0] + "</sc:hasLocationName>\n")
                rdf.write("		<sc:hasLocationName rdf:datatype=\"&xsd;string\">" + row[0] + "</sc:hasLocationName>\n")
                rdf.write("		<sc:hasLatitude rdf:datatype=\"&xsd;float\">" + row[1] + "</sc:hasLatitude>\n")
                rdf.write("		<sc:hasLongitude rdf:datatype=\"&xsd;float\">" + row[2] + "</sc:hasLongitude>\n")
                rdf.write("	</rdf:Description>\n\n")
                x = x+1

    rdf.write("</rdf:RDF>")

finally:
    rdf.close()


