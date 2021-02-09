
public class StfmpRequest {
	private String protocolVersion;
	private String operation;
	private String fileName;
	private String content;
	
	public StfmpRequest(String protocolVersion, String operation, String fileName, String content) {
		super();
		this.protocolVersion = protocolVersion;
		this.operation = operation;
		this.fileName = fileName;
		this.content = content;
	}
	public String getProtocolVersion() {
		return protocolVersion;
	}
	public void setProtocolVersion(String protocolVersion) {
		this.protocolVersion = protocolVersion;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public String toRawString() {

		String rawString = "";
		if(content != null){
			rawString = protocolVersion + "##" + operation + "##" + fileName + "##" + content;
		}else if(fileName != null){
			rawString = protocolVersion + "##" + operation + "##" + fileName;
		}else{
			rawString = protocolVersion + "##" + operation + "##";
		}
		return rawString  + "\r\n";
	}
	
	public static StfmpRequest fromRawString(String rawString) {
		String[] parts = rawString.split("##");
		String protocolVersion = parts[0];
		String operation = parts[1];
		String fileName = null;
		String content = null;
		if(operation.equals(StfmpOperation.WRITE)){
			fileName = parts[2];
			content = parts[3];
		}
		if(operation.equals(StfmpOperation.VIEW)){
			fileName = parts[2];
		}
		return new StfmpRequest(protocolVersion, operation, fileName, content);
	}
}
