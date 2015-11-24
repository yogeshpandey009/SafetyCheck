__author__ = 'HM'
import csv


rdf = open("city_rdf", "wb")
try:
    rdf.write("<rdf:RDF\n")
    rdf.write("	xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" \n")
    rdf.write("	xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" \n")
    rdf.write("	xmlns:owl=\"http://www.w3.org/2002/07/owl#\" \n")
    rdf.write("	xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" \n")
    rdf.write("	xmlns:sc=\"http://www.semanticweb.org/ontologies/2015/10/SafetyCheck#\">\n\n")


    with open('items_new.csv', 'rb') as f:

        reader = csv.reader(f)
        x = 1
        for row in reader:
            rdf.write("	<rdfd:Description rdf:about=\"&sc;city" + str(x) + "\">\n")
            rdf.write("		<sc:city rdf:datatype=\"&xsd;string\">" + row[0] + "</sc:city>\n")
            rdf.write("		<sc:atLatitude rdf:datatype=\"&xsd;float\">" + row[1] + "</sc:atLatitude>\n")
            rdf.write("		<sc:atLongitude rdf:datatype=\"&xsd;float\">" + row[2] + "</sc:atLongitude>\n")
            rdf.write("	</rdf:Description>\n\n")
            x = x+1

    rdf.write("</rdf:RDF>")

finally:
    rdf.close()


