package entity;

public class Host {

	String address;
	String alias;
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
