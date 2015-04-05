package DBIndex;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.json.simple.JSONObject;
import org.junit.Test;

import DBIndex.DBIndex;
import DBIndex.DBIndexStub;
import DBIndex.DBStub;
import DBIndex.DBUpdate;
import DBIndex.Indexer;

public class IndexerTest {

	@Test
	public void test() {
		
		Map<String,String> job_title_map = new HashMap<String,String>();
		Map<String,String> industry_map = new HashMap<String,String>();
		DBStub db = new DBStub("");
		
		JSONObject user_data = new JSONObject();
		user_data.put(DBUpdate.INDUSTRY, "test");
		user_data.put(DBUpdate.JOB_TITLE, "test");
		user_data.put("name", "test");
		user_data.put("version", "test");
		db.update(1, user_data);
		
		long endid = 10;
		DBIndex index = new DBIndexStub();
		
		Indexer indexer = new Indexer(db.scan(0), endid, index);
		
		Thread t = new Thread(indexer);
		t.start();
		
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail();
		}
		
		Assert.assertEquals(1, index.getNextIndustry("test"));
		Assert.assertEquals(-1, index.getNextIndustry("other"));
	}

}
