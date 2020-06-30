package pl.kielce.tu.neo4j.ogm;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.ogm.session.Session;

class BookService extends GenericService<Book> {

    public BookService(Session session) {
		super(session);
	}
    
	@Override
	Class<Book> getEntityType() {
		return Book.class;
	}
	
    Iterable<Map<String, Object>> getBookRelationships() {
        String query = 
        		"MATCH (b:Book)-[r]-() " + 
        		"WITH type(r) AS t, COUNT(r) AS c " +
        		"WHERE c >= 1 " +
        		"RETURN t, c";
        System.out.println("Executing " + query);
        HashMap<String, Object> params = new HashMap<String, Object>();
        return session.query(query, params).queryResults();
    }
    
}