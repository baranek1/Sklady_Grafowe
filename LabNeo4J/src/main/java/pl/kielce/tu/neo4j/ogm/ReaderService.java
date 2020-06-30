package pl.kielce.tu.neo4j.ogm;

import org.neo4j.ogm.session.Session;

class ReaderService extends GenericService<Reader> {

    public ReaderService(Session session) {
		super(session);
	}
    
	@Override
	Class<Reader> getEntityType() {
		return Reader.class;
	}
    
}