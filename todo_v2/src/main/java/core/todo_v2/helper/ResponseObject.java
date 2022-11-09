package core.todo_v2.helper;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseObject {
	private int statusCode;
	private String message;
	private Object data;
}
