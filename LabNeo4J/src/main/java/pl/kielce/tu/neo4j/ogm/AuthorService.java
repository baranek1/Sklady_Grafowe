package pl.kielce.tu.neo4j.ogm;

import org.neo4j.ogm.session.Session;

class AuthorService extends GenericService<Author> {

    public AuthorService(Session session) {
		super(session);
	}
    
	@Override
	Class<Author> getEntityType() {
		return Author.class;
	}
    
}