# -*- coding: utf-8 -*-
import facebook
import requests


def get_all_friends(token, allfriends):
    graph = facebook.GraphAPI(token)
    #profile = graph.get_object("me")
    #friends = graph.get_connections("me","friends")
    friends = graph.get_object('me/friends',fields='id,name,location')
    
    # Wrap this block in a while loop so we can keep paginating requests until
    # finished.
    while(True):
        for friend in friends['data']:
            try:
                name = friend["name"]            
                loc = friend["location"]["name"]
                allfriends[friend["id"]] = {
                    "name": name,
                    "location": loc
                }
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
    for key, friend in friends.iteritems():
        instances += create_friend_rdf(friend, i)
        i += 1
    instances.append("</rdf:RDF>")
    return "".join(instances)


def create_friend_rdf(friend, i):
    rdf = []
    rdf.append("\t <NamedIndividual rdf:about=\"&sc;person" + str(i) + "\">\n")
    rdf.append("\t <rdf:type rdf:resource=\"&sc;Person\"/>\n")
    rdf.append("\t <sc:hasName rdf:datatype=\"&xsd;string\">"+ friend["name"] +"</sc:hasName>\n")    
    rdf.append("\t <sc:hasLocation rdf:datatype=\"&xsd;string\">"+ friend["location"] +"</sc:hasLocation>\n")
    rdf.append("\t <sc:isFriendOf rdf:resource=\"&sc;person26\"/>\n")
    rdf.append("\t </NamedIndividual>\n\n")
    return "".join(rdf)
    

if __name__ == "__main__":
    tokens = ['CAACEdEose0cBAHH9oFSY88PKZB6TJlouzHHbb2Nh3H3Tp5wZACSk0olkITiSeEZAeUSjeuyZAHMnpaGxZAEidsnKgriKnwl8sX6IfQNIx023ZASZAlz4MbqAtfLDj0OZAW7r74Sm6ByDz9fC8YZCza2QDZBqFYzpOdUMUGlZBVB13ZAm2wni1rKwjKqf8KX7vjmZA47sCWw7LdBXegcHidwIqE0qR',
    'CAACEdEose0cBANnJZAmDvvdKQHhJMoT0866uGLYuadYAnZAIy7luZAacgZCU8fgg0qg3ALh3rP6foT913L8G21ZCq1aTYhLgPKmHpIDeBoq9rJMo5uwTy7QcLSzXokIlzDgNzpYJhM5ZB87zYoErC8k0FKTTcS88R80ZA1cTMGWLAZCx3ujPu2NLALWgHeVI7rNE51V4cH9hsZBS9vcbDYTqE',
    'CAACEdEose0cBABxIAG86QNfZBNfGrMhpZBFAYt7pxwbIftZASLusU61qCixFEvBmNQwxRIQuSv0q2D8DqAk13ZBTcK59y6gRHyzThHwUrZAmxccGyHaTiI8sZC7Rt27XVrlByp7Kw2cn0LZAGQTupnX7Or3EcNEIcBozaLC1GrPFGS7AyCp3CfeE1OlsNSRmtoZD', 
    'CAACEdEose0cBADVZCclh7Eg5PBAangEtlH7X1x1Jh7dTDWMKX2NaTnOGTuQq07wxjUSHN7cYP3rCmgOzVZADPbYZBldQJZC83FNHekaOZBQ1zNo8F9fOxZBZCmxUw8NXOYTcVAXoms9PC0VrT2H7Y8HqttABfUsn6sA88rf4ViMp0K1PWZBMRmEdub49lPj2i1wZD',
    'CAACEdEose0cBAMtmMlYHxbl2dxxuJaSK9Kxv1gbXKpPGSplhkqPROis2vYuZAZCIchGcFOdt6rGW5s8hl3vtMZAPQ9DZCJ7zvubJPgoPl1TpFalKeHZCz9JJ7sT0alUYAFqVBu6KQxPDZAT34ZALKDXOUo7MxsDZCSxgYU3RA1qR7WGo8gCvwANHSmj9s9eNJMvQ87eCzjao3DqEpX2o8O9F',
    'CAACEdEose0cBANbAiDztBM6r2gpM59zlCCDnOGLTkUCRR02HT8kT52mKKVR3xSvHMfmAZBYF9LkshVjMyHoC9OyQQEeK2zig8g8Gx5CcRftSrxH07ChHuZBSRi9lLCXAZB4e64Tl4DsFJj79LHNLhLxLLmgV8vBIVSrE85qhwxwVf3yvfK7UzmZAItTFsEaUtlizRiXfqmzXDZBh2Bvb4']
    allfriends = {}
    for token in tokens:
        get_all_friends(token, allfriends)
        
    instances = create_friends_instances(allfriends)
    print instances
    text_file = open("friends.rdf", "w")
    text_file.write(instances)
    text_file.close()
