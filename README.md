# SafetyCheck

A Linked Data Project to exploit the powers of semantic web technologies - RDF, OWL, SPARQL, TDB etc. 

Currently being hosted on:
http://52.11.8.42:8080/SafetyCheckWeb/


To enable gzip compression in Tomcat Server:
```xml
<Connector compressableMimeType="text/html,text/xml,application/xml,text/javascript,text/css,text/plain,application/x-javascript,application/json" compression="on" compressionMinSize="2048" connectionTimeout="20000" noCompressionUserAgents="gozilla, traviata" port="8080" protocol="HTTP/1.1" redirectPort="8443"/>
```

To Start fuseki Server on Mac/Linux:
```
./fuseki-server --update --loc="/Users/yogeshpandey/Documents/safetyCheck/SafetyCheck/data/tdb" /sc
```

To Start fuseki Server on Windows:
```
C:\Git\SafetyCheck\jena-fuseki1-1.3.1>fuseki-server --update --loc="C:\Git\SafetyCheck\data\tdb" /sc
```
