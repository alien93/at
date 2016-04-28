package model;
import java.io.Serializable;
/**
 * @author nina
 */
import java.util.ArrayList;
import java.util.List;

import javax.websocket.Session;

public class Host implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String address;
	private String alias;
	
	public transient static List<Host> hosts = new ArrayList<Host>();
	public transient static List<User> loggedUsers = new ArrayList<User>();
	
	
	public Host() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Host(String address, String alias) {
		super();
		this.address = address;
		this.alias = alias;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getAlias() {
		return alias;
	}
	
	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	@Override
	public String toString() {
		return "Host [address=" + address + ", alias=" + alias + "]";
	}
	
	
}