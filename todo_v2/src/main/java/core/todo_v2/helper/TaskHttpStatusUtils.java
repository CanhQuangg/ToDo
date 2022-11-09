package core.todo_v2.helper;

public enum TaskHttpStatusUtils {
	DATA_NOT_EXISTS(2002, "Data not exists"), NO_CONTENT(2004, "No Content"),
	DATA_HAS_ALREADY_EXITST(2005, " Data has already existed"), DATA_ALREADY_ACTION(2007, "Data already action"),

	PERMISSION(4001, "Permission"),
//	UNAUTHORIZED(4012, "Unauthorized"),
	SERVER_ERROR(5001, "Server error"),

	NO_PARAM(6001, "No Param"), INVALID_VALUE(6002, "Invalid value"),;

	private final int value;

	private final String reasonPhrase;

	TaskHttpStatusUtils(int value, String reasonPhrase) {
		this.value = value;
		this.reasonPhrase = reasonPhrase;
	}

	public int value() {
		return this.value;
	}

	public String getReasonPhrase() {
		return this.reasonPhrase;
	}
}
