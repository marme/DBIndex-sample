package DBIndex;

import org.json.simple.JSONObject;

public interface MessageQueueInterface {
	public void callback(long user_id, JSONObject user_data);
}
