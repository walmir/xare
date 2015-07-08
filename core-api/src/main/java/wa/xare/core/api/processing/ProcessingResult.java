package wa.xare.core.api.processing;

import wa.xare.core.api.Packet;

public class ProcessingResult {

	private boolean successful;

	private Throwable cause;

	private Packet resultingPacket;

	private ProcessingResult() {

	}

	public static ProcessingResult successfulProcessingResult(
	    Packet resultingPacket) {

		ProcessingResult result = new ProcessingResult();
		result.setSuccessful(true);
		result.setResultingPacket(resultingPacket);

		return result;
	}

	public static ProcessingResult failedProcessingResult(Packet resultingPacket,
	    Throwable cause) {

		ProcessingResult result = new ProcessingResult();
		result.setSuccessful(false);
		result.setResultingPacket(resultingPacket);
		result.setCause(cause);

		return result;
	}

	public boolean isSuccessful() {
		return successful;
	}

	private void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	public Throwable getCause() {
		return cause;
	}

	private void setCause(Throwable cause) {
		this.cause = cause;
	}

	public Packet getResultingPacket() {
		return resultingPacket;
	}

	private void setResultingPacket(Packet resultingPacket) {
		this.resultingPacket = resultingPacket;
	}

}
