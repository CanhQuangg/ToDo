package core.todo_v2.helper;

public class QueryHandler {

	/**
	 * @author Quang
	 * @param int limit
	 * @return check limit, set default if limit error
	 */
	public int checkLimit(int limit) {
		try {
			if (limit < 1) {
				limit = 10;
			}
			return limit;
		} catch (Exception e) {
			return 10;
		}
	}
}
