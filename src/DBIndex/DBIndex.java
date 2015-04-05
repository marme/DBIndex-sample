package DBIndex;


/**
 * Interface that provides methods to add and remove items to the pre batch run index
 *
 */
public interface DBIndex {

	/**
	 * adds an element to industry index
	 * 
	 * @param user_id user_id of this row
	 * @param industry industry of user_id
	 */
	public void addIndustry(String industry, long user_id);
	
	/**
	 * adds an element to job_title index
	 * 
	 * @param user_id user_id of this row
	 * @param job_title job_title of user_id
	 */
	public void addJobTitle(String job_title, long user_id);
	
	/**
	 * moves element to new industry in index
	 * 
	 * @param user_id user_id of this row
	 * @param old_industry industry the user_id is removed from
	 * @param new_industry industry the user_id is added to
	 */
	public void updateIndustry(long user_id, String old_industry, String new_industry);
	
	/**
	 * moves element to new industry in index
	 * 
	 * @param user_id user_id of this row
	 * @param old_job_title job_title the user_id is removed from
	 * @param new_job_title job_title the user_id is added to
	 */
	public void updateJobTitle(long user_id, String old_job_title, String new_job_title);
	
	/**
	 * return next user_id that has industry provided
	 * 
	 * @param industry industry that index should be returned for 
	 * @return
	 */
	public long getNextIndustry(String industry);
	
	/**
	 * return next user_id that has job_title provided
	 * 
	 * @param job_title job_title that index should be returned for
	 * @return
	 */
	public long getNextJobTitle(String job_title);
	
	
	
	
	
	
	
}
