package DBIndex;

import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeoutException;

import org.json.simple.JSONObject;

import DBIndex.DBStub.DBIterator;

public class Indexer implements Runnable{
	
	private DBIterator itr;
	private long endid;
	private DBIndex index;
	
	/**
	 * This class contains logic for thread that will be spun off to read segments of DB
	 * 
	 * @param itr iterator from DB
	 * @param endid user_id this thread should stop at
	 * @param index class to actually create index
	 */
	public Indexer( DBIterator itr, long endid, DBIndex index)
	{
		this.itr = itr;
		this.endid = endid;
		this.index = index;
	}
	
	@Override
	public void run() {
		boolean retry = true;
		
		while(retry)
		{
			try{
				Entry<Long,JSONObject> entry = itr.next();
				JSONObject json = entry.getValue();
				long user_id = entry.getKey();
				
				if(user_id > endid)
				{
					retry = false;
					break;
				}
				
				try{
					String job_title = (String)json.get(DBUpdate.JOB_TITLE);
					String industry = (String)json.get(DBUpdate.INDUSTRY);
					
					index.addIndustry(industry, user_id);
					index.addJobTitle(job_title, user_id);
				}catch(Exception e){
					System.out.println("Improperly formatted JSON");
					e.printStackTrace();
				}
				
			}catch(TimeoutException e){
				retry = true;
			}catch(NoSuchElementException e)
			{
				retry = false; //itr.next() no next element so stop retry
			}
		}
		
	}

}
