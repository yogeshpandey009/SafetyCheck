import csv


rdf = open("regions.rdf", "wb")
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


    with open('world_cities.csv', 'rb') as f:

        reader = csv.reader(f)
        next(reader, None)
        x = 1
        for row in reader:
            lat = "%.2f" % float(row[2])
            lng = "%.2f" % float(row[3])
            point_id = lat + "_" + lng
            rdf.write("	<rdf:Description rdf:about=\"&sc;" + point_id + "\">\n")
            rdf.write("	    <rdf:type rdf:resource=\"&sc;Point\"/>\n")
            rdf.write("		<sc:hasLatitude rdf:datatype=\"&xsd;float\">" + lat + "</sc:hasLatitude>\n")
            rdf.write("		<sc:hasLongitude rdf:datatype=\"&xsd;float\">" + lng + "</sc:hasLongitude>\n")
            rdf.write("	</rdf:Description>\n\n")

            rdf.write("	<rdf:Description rdf:about=\"&sc;region" + str(x) + "\">\n")
            rdf.write("	    <rdf:type rdf:resource=\"&sc;Region\"/>\n")
            rdf.write("		<sc:hasRegionName rdf:datatype=\"&xsd;string\">" + row[1].encode("string_escape").encode('UTF-8') + ", " + row[8].encode("string_escape").encode('UTF-8') + ", " + row[5].encode("string_escape").encode('UTF-8') + "</sc:hasRegionName>\n")
            rdf.write("		<sc:hasPopulation rdf:datatype=\"&xsd;unsignedLong\">" + str(int(float(row[4]))) + "</sc:hasPopulation>\n")
            rdf.write("		<sc:hasPoint rdf:resource=\"&sc;"+ point_id + "\"/>\n")
            rdf.write("	</rdf:Description>\n\n")
            x += 1

    rdf.write("</rdf:RDF>")

finally:
    f.close()
    rdf.close()