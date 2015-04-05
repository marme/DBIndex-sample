package DBIndex;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeoutException;

import org.json.simple.JSONObject;


/**
 * Stub to represent DB library
 *
 */
public class DBStub implements Database{
	
	private Map<Long,JSONObject> db;
	
	public DBStub(String host)
	{
		db = new HashMap<Long, JSONObject>();
	}
	

	@Override
	public JSONObject read(long user_id) {
		return db.get(user_id);
	}

	@Override
	public long update(long user_id, JSONObject user_data) {
		db.put(user_id,user_data);
		return 0;
	}

	@Override
	public boolean delete(long user_id) {
		return true;
	}

	@Override
	public DBIterator scan(long user_id){
		DBIterator current = new DBIterator(db);
		DBIterator previous = new DBIterator(db);
		
		try{
			while(current.hasNext())
			{
				if(current.next().getKey() >= user_id)
					return previous;
				
				previous.next();
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return current;
	}

	class DBIterator {
		Iterator<Entry<Long,JSONObject>> itr;
		
		DBIterator(Map<Long,JSONObject> db){
			itr = db.entrySet().iterator();
		}
		
		 public boolean hasNext() {
             return itr.hasNext();
         }
		
	    public Entry<Long, JSONObject> next() throws TimeoutException{
            return itr.next();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
	    

		
	}
	
}
