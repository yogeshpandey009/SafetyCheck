# -*- coding: utf-8 -*-
import facebook
import requests


def get_all_friends():
    token = 'CAACEdEose0cBABiEVZBaQjZAWkiCjrZBpnWfAzgWrqNRZAED1IGdjB3U02X7qazzkw9F9SvttfVTtQGdbYMEZBObATpOaiGvOL40KAhz0gOnyzD1FmpnuNrTsdXZAmPuIlWZCpj1UPu74gBSVKiyRYkZAmgoRWHB9N2G7MY0mSmJssZCVRK4NLMHxjde6T4yIAMaNQSA4fIUXKryHczkrGbJu'
    graph = facebook.GraphAPI(token)
    #profile = graph.get_object("me")
    #friends = graph.get_connections("me","friends")
    friends = graph.get_object('me/friends',fields='id,name,location')
    
    allfriends = []
    
    # Wrap this block in a while loop so we can keep paginating requests until
    # finished.
    while(True):
        for friend in friends['data']:
            try:
                name = friend["name"]            
                loc = friend["location"]["name"]
                allfriends.append({
                    "name": name,
                    "location": loc
                })
            except KeyError:
                pass
        try:
            # Attempt to make a request to the next page of data, if it exists.
            
            friends=requests.get(friends['paging']['next']).json()
        except KeyError:
            # When there are no more pages (['paging']['next']), break from the
            # loop and end the script.
            break
    
    #print allfriends
    return allfriends


def create_friends_instances(friends):
    instances = []
    instances.append("<?xml version=\"1.0\"?>\n")
    instances.append("<!DOCTYPE rdf:RDF [\n")
    instances.append("\t <!ENTITY owl \"http://www.w3.org/2002/07/owl#\" > \n")
    instances.append("\t <!ENTITY xsd \"http://www.w3.org/2001/XMLSchema#\" > \n")
    instances.append("\t <!ENTITY rdfs \"http://www.w3.org/2000/01/rdf-schema#\" > \n")
    instances.append("\t <!ENTITY rdf \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" > \n")
    instances.append("\t <!ENTITY sc \"http://www.semanticweb.org/ontologies/2015/10/SafetyCheck#\" > \n")
    instances.append("]> \n")
    instances.append("<rdf:RDF\n")
    instances.append("\t xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" \n")
    instances.append("\t xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" \n")
    instances.append("\t xmlns:owl=\"http://www.w3.org/2002/07/owl#\" \n")
    instances.append("\t xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" \n")
    instances.append("\t xmlns:sc=\"http://www.semanticweb.org/ontologies/2015/10/SafetyCheck#\">\n\n")
    i = 1
    for friend in friends:
        instances += create_friend_rdf(friend, i)
        i += 1
    instances.append("</rdf:RDF>")
    return "".join(instances)


def create_friend_rdf(friend, i):
    rdf = []
    rdf.append("\t <NamedIndividual rdf:about=\"&sc;friend" + str(i) + "\">\n")
    rdf.append("\t <rdf:type rdf:resource=\"&sc;Person\"/>\n")
    rdf.append("\t <sc:hasName rdf:datatype=\"&xsd;string\">"+ friend["name"] +"</sc:hasName>\n")    
    rdf.append("\t <sc:hasLocation rdf:datatype=\"&xsd;string\">"+ friend["location"] +"</sc:hasLocation>\n")
    rdf.append("\t </NamedIndividual>\n\n")
    return "".join(rdf)
    

if __name__ == "__main__":
    friends = get_all_friends()
    instances = create_friends_instances(friends)
    print instances
    text_file = open("friends.rdf", "w")
    text_file.write(instances)
    text_file.close()
