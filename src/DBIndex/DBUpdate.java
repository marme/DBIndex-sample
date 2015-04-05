package DBIndex;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeoutException;

import org.json.simple.JSONObject;

import DBIndex.DBStub.DBIterator;


public class DBUpdate implements MessageQueueInterface{
	
	public static final String JOB_TITLE = "job_title";
	public static final String INDUSTRY = "industry";
	public static final String OLD_INDUSTRY = "old_industry";
	public static final String NEW_INDUSTRY = "new_industry";
	public static final String OLD_JOB_TITLE = "old_job_title";
	public static final String NEW_JOB_TITLE = "new_job_title";
	
	public static final int BATCH_SIZE = 100;
	

	private Database db;
	private Map<String,String> job_title_map;
	private Map<String,String> industry_map;
	private DBIndex index;
	
	
	/**
	 * @param host string for DB connection
	 */
	public DBUpdate(String host)
	{
		db = new DBStub(host);
		job_title_map = new HashMap<String,String>();
		industry_map = new HashMap<String,String>();
		index = new DBIndexStub();
	}
	
	
	/**
	 * for testing
	 * 
	 * @return
	 */
	Database getDB()
	{return db;}
	
	/**
	 * for testing
	 * 
	 * @return
	 */
	DBIndex getIndex()
	{return index;}
	
	/**
	 * this method queues up job titles to be changed by batch process
	 * 
	 * @param old_title
	 * @param new_title
	 */
	public void change_job_title(String old_title, String new_title)
	{
		job_title_map.put(old_title,new_title);
	}
	
	/**
	 * this method queues up industries to be changed by batch process
	 * 
	 * @param old_industry
	 * @param new_industry
	 */
	public void change_industry(String old_industry, String new_industry)
	{
		industry_map.put(old_industry, new_industry);
	}
	
	
	/**
	 * This method begins creating pre-batch indexing of all the records to be updated based on the items queued
	 */
	public void createIndex(long currentid)
	{
		ArrayList<Thread> threads = new ArrayList<Thread>();
		boolean retry = true;
		while(retry)
		{
			try{
		
				DBIterator itr = db.scan(currentid);
				
				while(itr.hasNext())
				{
					Indexer indexer = new Indexer(itr, currentid+BATCH_SIZE, index);
					Thread t = new Thread(indexer);
					t.start();
					threads.add(t);
					
					currentid += BATCH_SIZE+1;
					itr = db.scan(currentid);
				}
				
				retry = false;
			}
			catch(TimeoutException e){
				retry = true;
			}
		}
		
		for(Thread t : threads)
		{
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	

	
	/* 
	 * callback method for MessageQueue that will update the index should write occur between building of index and running of batch process
	 */
	@Override
	public void callback(long user_id, JSONObject user_data) {
		
		if(user_data.containsKey(OLD_INDUSTRY))
		{
			String old_industry = (String) user_data.get(OLD_INDUSTRY);
			String new_industry = (String) user_data.get(NEW_INDUSTRY);
						
			index.updateIndustry(user_id, old_industry, new_industry);
		}
		
		if(user_data.containsKey(OLD_JOB_TITLE))
		{
			String old_job_title = (String) user_data.get(OLD_JOB_TITLE);
			String new_job_title = (String) user_data.get(NEW_JOB_TITLE);
			
			index.updateJobTitle(user_id, old_job_title, new_job_title);
		}	
	}
	
	public void beginBatch()
	{
		for(Entry<String,String> entry : industry_map.entrySet())
		{
			String old_industry = entry.getKey();
			String new_industry = entry.getValue();
			
			long next = index.getNextIndustry(old_industry);
			
			while(next >= 0)
			{
				JSONObject user_data = null;
				boolean read = false;
				while(!read)
				{
					try{
						user_data = db.read(next);
						read = true;
					}
					catch(TimeoutException e)
					{}
				}
				
				try{
					user_data.put(INDUSTRY, new_industry);
				}catch(Exception e)
				{
					System.out.println("JSON not formatter properly");
					e.printStackTrace();
				}
				
				boolean write = false;
				while(!write)
				{
					try{
						db.update(next, user_data);
						write = true;
					}
					catch(TimeoutException e)
					{}
				}
				
				next = index.getNextIndustry(old_industry);
			}
			
		}
		
		for(Entry<String,String> entry : job_title_map.entrySet())
		{
			String old_job_title = entry.getKey();
			String new_job_title = entry.getValue();
			
			long next = index.getNextJobTitle(old_job_title);
			
			while(next >= 0)
			{
				JSONObject user_data = null;
				boolean read = false;
				while(!read)
				{
					try{
						user_data = db.read(next);
						read = true;
					}
					catch(TimeoutException e)
					{}
				}
				
				try{
					user_data.put(JOB_TITLE, new_job_title);
				}catch(Exception e)
				{
					System.out.println("JSON not formatter properly");
					e.printStackTrace();
				}
				
				boolean write = false;
				while(!write)
				{
					try{
						db.update(next, user_data);
						write = true;
					}
					catch(TimeoutException e)
					{}
				}
				
				next = index.getNextJobTitle(old_job_title);
			}		
		}	
	}
	
	public static void main(String [] args)
	{
		DBUpdate dbupdate = new DBUpdate("");
		
		//create index
		dbupdate.createIndex(0);
		
		//queue up industry file
		try (BufferedReader br = new BufferedReader(new FileReader("industry.txt")))
		{
			String line;
 
			while ((line = br.readLine()) != null) {
				String [] strs = line.split(",");
				dbupdate.change_industry(strs[0], strs[1]);
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e){
			System.out.println("industry input file not formatter properly");
			e.printStackTrace();
		}
		
		//queue up job_title file
		try (BufferedReader br = new BufferedReader(new FileReader("job_title.txt")))
		{
			String line;
 
			while ((line = br.readLine()) != null) {
				String [] strs = line.split(",");
				dbupdate.change_job_title(strs[0], strs[1]);
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e){
			System.out.println("job title input file not formatter properly");
			e.printStackTrace();
		}
		
		//begin batch
		dbupdate.beginBatch();
	}


	
}
