package DBIndex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 *Stub to provide implementation for interface, should be replaced with real DB implementation in real program
 *
 */
public class DBIndexStub implements DBIndex{
	
	private HashMap<String,List<Long>> industryIndex = new HashMap<String,List<Long>>();
	private HashMap<String,List<Long>> job_titleIndex = new HashMap<String,List<Long>>();
	private HashMap<String,Iterator<Long>> industryIterators = new HashMap<String,Iterator<Long>>();
	private HashMap<String,Iterator<Long>> job_titleIterators = new HashMap<String,Iterator<Long>>();

	@Override
	public void addIndustry(String industry, long user_id) {
		List<Long> index = industryIndex.get(industry);
		if(index == null)
		{
			index = new ArrayList<Long>();
			industryIndex.put(industry, index);
		}
		
		index.add(user_id);
	}

	@Override
	public void addJobTitle(String job_title, long user_id) {
		List<Long> index = job_titleIndex.get(job_title);
		if(index == null)
		{
			index = new ArrayList<Long>();
			job_titleIndex.put(job_title, index);
		}
		
		index.add(user_id);
	}

	@Override
	public void updateIndustry(long user_id, String old_industry, String new_industry) {
		List<Long> old_list = industryIndex.get(old_industry);
		List<Long> new_list = industryIndex.get(new_industry);
		
		if(new_list == null)
		{
			new_list = new ArrayList<Long>();
			industryIndex.put(new_industry, new_list);
		}
		
		old_list.remove(user_id);
		new_list.add(user_id);
	}

	@Override
	public void updateJobTitle(long user_id, String old_job_title, String new_job_title) {
		List<Long> old_list = industryIndex.get(old_job_title);
		List<Long> new_list = industryIndex.get(new_job_title);
		
		if(new_list == null)
		{
			new_list = new ArrayList<Long>();
			industryIndex.put(new_job_title, new_list);
		}
		
		old_list.remove(user_id);
		new_list.add(user_id);
	}

	@Override
	public long getNextIndustry(String industry) {
		Iterator<Long> itr = industryIterators.get(industry);	
		
		if(itr == null)
		{	
			List<Long> list = industryIndex.get(industry);
			if(list == null)
			{
				list = new ArrayList<Long>();
			}
			itr = list.iterator();
			industryIterators.put(industry, itr);
		}
		if(itr.hasNext())
			return itr.next();
		else
			return -1;
	}

	@Override
	public long getNextJobTitle(String job_title) {
		Iterator<Long> itr = job_titleIterators.get(job_title);	
		
		if(itr == null)
		{	
			List<Long> list = job_titleIndex.get(job_title);
			if(list == null)
			{
				list = new ArrayList<Long>();
			}
			itr = list.iterator();
			job_titleIterators.put(job_title, itr);
		}
		if(itr.hasNext())
			return itr.next();
		else
			return -1;
	}
}
