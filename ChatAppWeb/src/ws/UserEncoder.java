package ws;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import org.json.JSONObject;

@SuppressWarnings("rawtypes")
public class UserEncoder implements Encoder.Text{

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(EndpointConfig arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String encode(Object arg0) throws EncodeException {
		JSONObject jo = new JSONObject(arg0);
		return jo.toString();

	}

}
