package model;
import java.io.Serializable;
/**
 * @author nina
 */
import java.util.ArrayList;
import java.util.HashMap;


public class Message implements Serializable{

	private static final long serialVersionUID = 1L;
	private User from;
	private User to;
	private String date;
	private String subject;
	private String content;
	
	public transient static HashMap<String, ArrayList<Message>> messages = new HashMap<>();
	
	public Message() {
	}

	public Message(User from, User to, String date, String subject, String content) {
		super();
		this.from = from;
		this.to = to;
		this.date = date;
		this.subject = subject;
		this.content = content;
	}

	public User getFrom() {
		return from;
	}

	public void setFrom(User from) {
		this.from = from;
	}

	public User getTo() {
		return to;
	}

	public void setTo(User to) {
		this.to = to;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "Message [from=" + from + ", to=" + to + ", date=" + date + ", subject=" + subject + ", content="
				+ content + "]";
	}
}