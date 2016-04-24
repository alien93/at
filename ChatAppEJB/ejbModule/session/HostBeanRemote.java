package session;


import javax.ejb.Remote;

import entity.Host;
import entity.User;
import exception.AliasExsistsException;

@Remote
public interface HostBeanRemote {

	HostsList register(String address, String alias) throws AliasExsistsException;
	void unregister(Host host);
	void addUser(User user);
	void removeUser(User user);
}
