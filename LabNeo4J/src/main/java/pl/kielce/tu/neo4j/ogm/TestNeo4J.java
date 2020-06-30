package pl.kielce.tu.neo4j.ogm;

import java.util.Map;

import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

public class TestNeo4J {

	public static void main(String[] args) {
		
		Configuration configuration = new Configuration.Builder().uri("bolt://localhost:7687").credentials("neo4j", "neo4jpassword").build();
	    SessionFactory sessionFactory = new SessionFactory(configuration, "pl.kielce.tu.neo4j.ogm");

		Session session = sessionFactory.openSession();
		
		session.purgeDatabase();
			
		BookService bookService = new BookService(session);
		
		Author author1 = new Author("Sienkiewicz");
		Book book1 = new Book("Potop");
		book1.addAuthor(author1);
		bookService.createOrUpdate(book1);
		
		Author author2 = new Author("Mickiewicz");
		Author author3 = new Author("Słowacki");
		Book book2 = new Book("Wiersze");
		book2.addAuthor(author2);
		book2.addAuthor(author3);
		bookService.createOrUpdate(book2);
		
		Reader reader1 = new Reader("Kowalski");
		reader1.addBook(book1);
		ReaderService readerService = new ReaderService(session);
		readerService.createOrUpdate(reader1);
		
		System.out.println("All books:");
		for(Book b : bookService.readAll())
			System.out.println(b);
	
		for(Map<String, Object> map : bookService.getBookRelationships())
			System.out.println(map);
		
		sessionFactory.close();
	}
}
