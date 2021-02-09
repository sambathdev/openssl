
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class StfmpClient {

	public static void main(String[] args) {

		// Specify trust store info
		System.setProperty("javax.net.ssl.trustStore", "D:\\program_installed\\eclipse\\dataproject\\STFMP\\lib\\piu-trust-store.cacerts");
		System.setProperty("javax.net.ssl.trustStorePassword", "123456");
		try {
			SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket connection = (SSLSocket) socketFactory.createSocket("localhost", 9999);			
			System.out.println("Connected to server");
			
			InputStream inputStream = connection.getInputStream();
			Scanner scanner = new Scanner(inputStream);
			OutputStream outputStream = connection.getOutputStream();
			Scanner inputScanner = new Scanner(System.in);  				
			String step = "operation";
			showInstruction();
			String operation = null;
			String fileName = null;
			String content = null;
			
			while (inputScanner.hasNext()) {
			String input = inputScanner.nextLine();
			if(step.equals("operation")){
				if(input.equals("1")) {
					operation = StfmpOperation.VIEW;
				}
				if(input.equals("2")) {
					operation = StfmpOperation.WRITE;
				}
				if(input.equals("3")) {
					operation = StfmpOperation.CLOSE;
					
					StfmpRequest request = new StfmpRequest(Constants.PROTOCOL_VERSION, StfmpOperation.CLOSE, null, null);					
					PrintWriter printWriter = new PrintWriter(outputStream);
					String rawRequest = request.toRawString();
					printWriter.write(rawRequest);
					printWriter.flush();
					scanner.close();
					connection.close();
					break;
				}
				step = "fileName";
				System.out.println("Please Provide file Name: ");
			} else if(step.equals("fileName")){
				fileName = input;
				if(operation.equals(StfmpOperation.VIEW)){
					step = "operation";
					StfmpRequest request = new StfmpRequest(Constants.PROTOCOL_VERSION, StfmpOperation.VIEW, fileName + ".txt", null);					
					PrintWriter printWriter = new PrintWriter(outputStream);
					String rawRequest = request.toRawString();
					printWriter.write(rawRequest);
					printWriter.flush();
					try {
						String rawResponse = scanner.nextLine();
						StfmpResponse response = StfmpResponse.fromRawResponse(rawResponse);
						if(response.getStatus().equals(StfmpStatus.NOT_FOUND)){
							System.out.println("File Not Found");
						}else{
							System.out.println("Content of " + fileName + ".txt: \n" + response.getResult());
						}
					}catch (NoSuchElementException e) {
						e.printStackTrace();
					}
					showInstruction();
				}else if(operation.equals(StfmpOperation.WRITE)){
					step = "content";
					System.out.println("Please Provide content of file");
				}
			} else if(step.equals("content")){
				content = input;
				step = "operation";
				StfmpRequest request = new StfmpRequest(Constants.PROTOCOL_VERSION, StfmpOperation.WRITE, fileName + ".txt", content);					
				PrintWriter printWriter = new PrintWriter(outputStream);
				printWriter.write(request.toRawString());
				printWriter.flush();
				try {
					String rawResponse = scanner.nextLine();
					StfmpResponse response = StfmpResponse.fromRawResponse(rawResponse);
					if(response.getStatus().equals(StfmpStatus.NOT_FOUND)){
						System.out.println("Not Found");
					}else if(response.getStatus().equals(StfmpStatus.OK)){
						System.out.println(response.getResult());
					}
				}catch (NoSuchElementException e) {
					e.printStackTrace();
				}
				showInstruction();
			}
		}
		inputScanner.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void showInstruction() {
		System.out.println("Put 1 for Read file \nPut 2 For Write file \nPut 3 for Quit");
	}
}





