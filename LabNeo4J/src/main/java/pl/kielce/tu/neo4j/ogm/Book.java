package pl.kielce.tu.neo4j.ogm;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity(label = "Book")
public class Book {

	@Id
	@GeneratedValue
	private Long id;

	@Property(name = "name")
	private String name;
	
	public Book() {
	}

	public Book(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Relationship(type = "BOOK_AUTHORS")
	private Set<Author> authors = new HashSet<>();
	
	public void addAuthor(Author author) {
		authors.add(author);
	}

	@Override
	public String toString() {
		return "Book [id=" + id + ", name=" + name + "]";
	}
}