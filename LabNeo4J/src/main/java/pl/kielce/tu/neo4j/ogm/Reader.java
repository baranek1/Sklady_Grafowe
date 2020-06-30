package pl.kielce.tu.neo4j.ogm;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity(label = "Reader")
public class Reader {

	@Id
	@GeneratedValue
	private Long id;

	@Property(name = "name")
	private String name;
	
	public Reader() {
	}

	public Reader(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	@Relationship(type = "READER_BOOKS")
	private Set<Book> books = new HashSet<>();

	public void addBook(Book book) {
		books.add(book);
	}
	
	@Override
	public String toString() {
		return "Reader [id=" + id + ", name=" + name + "]";
	}
}