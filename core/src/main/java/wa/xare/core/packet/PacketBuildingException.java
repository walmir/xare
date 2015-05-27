package wa.xare.core.packet;

@SuppressWarnings("serial")
public class PacketBuildingException extends RuntimeException {

	public PacketBuildingException(String message) {
		super(message);
	}

	public PacketBuildingException(String message, Throwable cause) {
		super(message, cause);
	}

}
