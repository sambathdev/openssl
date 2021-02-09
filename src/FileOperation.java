import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class FileOperation {
	public static String view(String filePath) {
		String content = "";
		try {
			InputStream inputStream = new FileInputStream(filePath);
			Scanner scanner = new Scanner(inputStream);
			while(scanner.hasNext()) {
				String line = scanner.nextLine();
				content += line;
			}
			scanner.close();
			inputStream.close();
		} catch (IOException e) {
			return "notfound";
		}
		System.out.println("The data has been read!");
		return content;
	}
	
	public static int write(String filePath, String data) {
		try {
			OutputStream outputStream = new FileOutputStream(filePath);
			PrintWriter printWriter = new PrintWriter(outputStream);
			printWriter.write(data);
			printWriter.flush();
			printWriter.close();
			outputStream.close();
			System.out.println("The data has been writen!");
			return 1;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}
}
