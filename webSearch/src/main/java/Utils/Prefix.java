package Utils;

public enum Prefix {
	INFO(Color.CYAN + "[INFO]: " + Color.WHITE),
	ERROR(Color.RED + "[ERROR]: " + Color.WHITE),
	WARNING(Color.YELLOW + "[WARNING]: " + Color.WHITE),
	MYSQL(Color.BLUE + "[MYSQL]: " + Color.WHITE);

	private final String code;

	Prefix(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return code;
	}
}