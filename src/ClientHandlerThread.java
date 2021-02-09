import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ClientHandlerThread extends Thread {
	
	private Socket connection;
	
	public ClientHandlerThread(Socket connection) {
		super();
		this.connection = connection;
	}
	
	public void setConnection(Socket connection) {
		this.connection = connection;
	}

	@Override
	public void run() {
		super.run();
		
		while(true) {
			try {
				// 	Read request from the client
				InputStream inputStream = connection.getInputStream();
				Scanner scanner = new Scanner(inputStream);
				
				while(scanner.hasNext()){
					String rawRequest = scanner.nextLine();
					StfmpRequest request = StfmpRequest.fromRawString(rawRequest);
					// Process the request
					boolean isValidRequest = validateRequest(request);
					if(!isValidRequest){
						StfmpResponse response = new StfmpResponse(Constants.PROTOCOL_VERSION, StfmpStatus.INVALID, "Invalid request.");
						sendResponse(connection, response);
					}else{

						if (request.getOperation().equals(StfmpOperation.VIEW)) {
							String readFromFile = FileOperation.view("./" + request.getFileName());
							if(readFromFile.equals("notfound")){
								//response notfound
								StfmpResponse response = new StfmpResponse(Constants.PROTOCOL_VERSION, StfmpStatus.NOT_FOUND, "File not found.");
								sendResponse(connection, response);
							}else {
								StfmpResponse response = new StfmpResponse(Constants.PROTOCOL_VERSION, StfmpStatus.OK, readFromFile);
								sendResponse(connection, response);
							}
						} else if (request.getOperation().equals(StfmpOperation.WRITE)) {
							String content = request.getContent();
							int writeStatus = FileOperation.write("./" + request.getFileName(), content);
							if(writeStatus == -1){
								StfmpResponse response = new StfmpResponse(Constants.PROTOCOL_VERSION, StfmpStatus.NOT_FOUND, "File not found.");
								sendResponse(connection, response);
							}else{
								StfmpResponse response = new StfmpResponse(Constants.PROTOCOL_VERSION, StfmpStatus.OK, "file have been written.");
								sendResponse(connection, response);
							}
						} else if (request.getOperation().equals(StfmpOperation.CLOSE)) {
							System.out.println("Done with the client.!!!!");
							scanner.close();
							connection.close();
							break;
						}
					}
				}

				
			}catch(IOException ex) {
				
			}
		}
	}
	
	private static void sendResponse(Socket connection, StfmpResponse response) throws IOException {
		OutputStream outputStream = connection.getOutputStream();
		PrintWriter printWriter = new PrintWriter(outputStream);
		printWriter.write(response.toRawResponse());
		printWriter.flush();
	}
	
	private static boolean validateRequest(StfmpRequest request) {
		String protocolVersion = request.getProtocolVersion();
		String operation = request.getOperation();
		boolean isValidProtocol = (protocolVersion.equals(Constants.PROTOCOL_VERSION));
		boolean isValidOperation = (operation.equals(StfmpOperation.VIEW)) || (operation.equals(StfmpOperation.WRITE)) || (operation.equals(StfmpOperation.CLOSE));
		if(isValidProtocol && isValidOperation){
			return true;
		}else{
			return false;
		}
	}

}
