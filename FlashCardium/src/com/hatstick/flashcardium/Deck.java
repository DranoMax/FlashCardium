package com.hatstick.flashcardium;

import java.sql.Timestamp;

public class Deck {

	private String name;
	private String description;
	private String author;

	public Deck() {
		
	}
	
	public Deck(String name, String description, String author) {
		this.setName(name);
		this.setDescription(description);
		this.setAuthor(author);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
	
}
