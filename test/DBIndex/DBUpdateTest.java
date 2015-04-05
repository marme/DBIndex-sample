package DBIndex;

import static org.junit.Assert.*;

import java.util.concurrent.TimeoutException;

import junit.framework.Assert;

import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

import DBIndex.DBIndex;
import DBIndex.DBUpdate;

public class DBUpdateTest {
	DBUpdate dbupdate;
	
	@Before
	public void Init() throws TimeoutException
	{
		dbupdate = new DBUpdate("");
		dbupdate.change_job_title("test","TEST");
		dbupdate.change_industry("test","TEST");
		
		JSONObject user_data = new JSONObject();
		user_data.put(DBUpdate.INDUSTRY, "test");
		user_data.put(DBUpdate.JOB_TITLE, "test");
		user_data.put("name", "test");
		user_data.put("version", "test");
		
		dbupdate.getDB().update(1, user_data);
	}
	
	
	@Test
	public void testCreateIndex()
	{
		dbupdate.createIndex(0);
		DBIndex index = dbupdate.getIndex();
		
		Assert.assertEquals(1, index.getNextIndustry("test"));
	}	
	
	@Test
	public void testCallback()
	{
		dbupdate.createIndex(0);
		
		JSONObject user_data = new JSONObject();
		user_data.put(DBUpdate.OLD_INDUSTRY, "test");
		user_data.put(DBUpdate.NEW_INDUSTRY, "test2");
		user_data.put(DBUpdate.OLD_JOB_TITLE, "test");
		user_data.put(DBUpdate.NEW_JOB_TITLE, "test2");
		
		dbupdate.callback(1, user_data);
		
		Assert.assertEquals(-1,dbupdate.getIndex().getNextIndustry("test"));
		
		
	}
	
	@Test
	public void testBeginBatch()
	{
		dbupdate.createIndex(0);
		
		dbupdate.beginBatch();
		
		try {
			JSONObject user_data = dbupdate.getDB().read(1);
			Assert.assertEquals("TEST", user_data.get(DBUpdate.INDUSTRY));
			Assert.assertEquals("TEST", user_data.get(DBUpdate.JOB_TITLE));
		} catch (TimeoutException e) {
			fail();
		}
		
		
	}

}
