package DBIndex;

import java.util.concurrent.TimeoutException;

import org.json.simple.JSONObject;

import DBIndex.DBStub.DBIterator;

public interface Database {
	
	public JSONObject read(long user_id) throws TimeoutException;
	public long update(long user_id, JSONObject user_data) throws TimeoutException;
	public boolean delete(long user_id) throws TimeoutException;
	public DBIterator scan(long user_id) throws TimeoutException;
}
