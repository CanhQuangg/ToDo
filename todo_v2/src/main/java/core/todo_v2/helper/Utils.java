package core.todo_v2.helper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.Document;
import org.springframework.stereotype.Component;

@Component
public class Utils {
	/**
	 * @author Quang
	 * @param String date format String: yyyy-mm-dd
	 * @return Date dateParsed from StringDate
	 */
	public Date parseStringToISODate(String date) {
		LocalDateTime fromDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE).atStartOfDay();
		Instant instant = fromDate.atZone(ZoneId.systemDefault()).toInstant();
		Date dateParsed = Date.from(instant);

		return dateParsed;
	}

	/**
	 * @author Quang
	 * @param List<Document> lst
	 * @return objectId in lst is converted objectId toString
	 */
	public List<Document> convertId(List<Document> lst) {
		for (Document element : lst) {
			element.put("_id", element.get("_id").toString());
		}
		return lst;
	}

	/**
	 * @author Quang To check params have blank value. If value is blank or empty or
	 *         null, remove this key
	 * @param Map<String, Object> body
	 * @return checkedParams
	 */
	public Map<String, Object> checkParams(Map<String, Object> body) {
		Map<String, Object> checkedBody = new HashMap<>();
		try {
			Set<String> keys = body.keySet();
			for (String key : keys) {
				String value = (String) body.get(key);
				if (!value.isBlank() && !value.isEmpty() && value != null) {
					checkedBody.put(key, value);
				}
			}
			return checkedBody;
		} catch (Exception e) {
			return checkedBody;
		}
	}
}
