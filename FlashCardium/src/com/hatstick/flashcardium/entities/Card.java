package com.hatstick.flashcardium.entities;

public class Card {
	
	private long id;
	private String deck;
	private String subject;
	private String question;
	private String answer;
	
	public Card() {
		
	}
	
	public Card(String subject, String question, String answer) {
		this.setSubject(subject);
		this.setQuestion(question);
		this.setAnswer(answer);
	}
	
	public Card(String deck, String subject, String question, String answer) {
		this.setDeck(deck);
		this.setSubject(subject);
		this.setQuestion(question);
		this.setAnswer(answer);
	}
	
	public Card(int id, String subject, String question, String answer) {
		this.setId(id);
		this.setSubject(subject);
		this.setQuestion(question);
		this.setAnswer(answer);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getDeck() {
		return deck;
	}

	public void setDeck(String deck) {
		this.deck = deck;
	}
	
	

}
