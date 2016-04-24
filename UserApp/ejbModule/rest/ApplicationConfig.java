package rest;


import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import session.UserBean;


@ApplicationPath("rest")
public class ApplicationConfig extends Application{

	public Set<Class<?>> getClasses() {
		Set<Class<?>> s = new HashSet<Class<?>>();
		s.add(UserBean.class);
		return s;
	}
	
}
