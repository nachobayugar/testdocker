package utils;

import java.io.*;
import java.util.*;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class FileUtils{
		
	public static String getQuery(){
		
		String query = "";
		searchInFile("Config.txt", "Query =");
		return query;
	}
	public static String readFirstLine(String fileName){
		FileInputStream fin = null;
		String returnStr = "";
		try{
			//int ch;
			//StringBuffer strContent = new StringBuffer("");
			fin = new FileInputStream(fileName);
			DataInputStream in = new DataInputStream(fin);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			Integer lineNumber = 0;

			while ((strLine = br.readLine()) != null&& lineNumber<1)   {				
				//System.out.println (strLine);
				returnStr = strLine; 
				lineNumber++;
			}
			in.close();
			fin.close();
			br.close();
			
		}
		catch(FileNotFoundException e){
			System.out.println("[" + fileName + "] - File " + fileName + " could not be found on filesystem");
		}
		catch(IOException ioe){
		   	System.out.println("[" + fileName + "] - Exception while reading the file" + ioe);
		}
		return returnStr;
		
	}
	
	
	public static Map searchInFile(String fileName, String searchString){
		Map searchResult = new LinkedHashMap();
		searchResult.put("lineContent", "");
		searchResult.put("lineNumber", 0);
		
		FileInputStream fin = null;
		//String returnStr = "";
		try{
			//int ch;
			//StringBuffer strContent = new StringBuffer("");
			fin = new FileInputStream(fileName);
			DataInputStream in = new DataInputStream(fin);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			Integer lineNumber = 1;

			while ((strLine = br.readLine()) != null)   {			
				//System.out.println (strLine);
				if(strLine.indexOf(searchString)!=-1){
					searchResult.put("lineContent", strLine);
					searchResult.put("lineNumber", lineNumber);
					return searchResult;
				}
				lineNumber++;
			}
			in.close();
			fin.close();
			br.close();
			
		}
		catch(FileNotFoundException e){
			System.out.println("[" + fileName + "] - File " + fileName + " could not be found on filesystem");
		}
		catch(IOException ioe){
		   	System.out.println("[" + fileName + "] - Exception while reading the file" + ioe);
		}
		return searchResult;

	}//Fin readLinebyLine
	
	public static Map strictSearchInFile(String fileName, String searchString){
		Map searchResult = new LinkedHashMap();
		searchResult.put("lineContent", "");
		searchResult.put("lineNumber", 0);
		
		FileInputStream fin = null;
		//String returnStr = "";
		try{
			//int ch;
			//StringBuffer strContent = new StringBuffer("");
			fin = new FileInputStream(fileName);
			DataInputStream in = new DataInputStream(fin);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			Integer lineNumber = 1;
			boolean found = false;

			while ((strLine = br.readLine()) != null && found==false)   {			
				//System.out.println (strLine);
				if(strLine.indexOf(searchString)==0){ //The line should start with de searchString
					searchResult.put("lineContent", strLine);
					searchResult.put("lineNumber", lineNumber);
					found = true;
				}
				lineNumber++;
			}
			in.close();
			fin.close();
			br.close();
			
		}
		catch(FileNotFoundException e){
			System.out.println("[" + fileName + "] - File " + fileName + " could not be found on filesystem");
		}
		catch(IOException ioe){
		   	System.out.println("[" + fileName + "] - Exception while reading the file" + ioe);
		}
		return searchResult;

	}//Fin stricSearchInFile
	
	public static boolean isEmpty(String fileName){
		FileInputStream fin = null;
		boolean isEmpty = true;
		try{
			//int ch;
			//StringBuffer strContent = new StringBuffer("");
			fin = new FileInputStream(fileName);
			DataInputStream in = new DataInputStream(fin);
			//BufferedReader br = new BufferedReader(new InputStreamReader(in));
			//String strLine;
			try{
				int line = fin.read();
				//System.out.println(line);
				if(line!=-1){
					isEmpty = false;					
				}
			}
			catch (IOException e){
				System.out.println("[" + fileName + "] - File " + fileName + " empty" + e.getStackTrace());
			}
			
			in.close();
			fin.close();
			
		}
		catch(FileNotFoundException e){
			System.out.println("[" + fileName + "] - File " + fileName + " could not be found on filesystem");
		}
		catch(IOException ioe){
		   	System.out.println("[" + fileName + "] - Exception while reading the file" + ioe);
		}
		return isEmpty;
		
	}
	
		
	public static void writeNewLine(String fileName, String dataToWrite){
		BufferedWriter bufferedWriter = null;
		FileWriter fileWriter = null;
	      
	      try {
	    	  fileWriter = new FileWriter(fileName, true);
	          bufferedWriter = new BufferedWriter(fileWriter);
	          bufferedWriter.newLine();
	          bufferedWriter.write(dataToWrite);
	          
	      } catch (FileNotFoundException ex) {
	          ex.printStackTrace();
	      } catch (IOException ex) {
	          ex.printStackTrace();
	      } finally {
	    	  
	          //Cierro BufferedWriter
	          try {
	              if (bufferedWriter != null) {
	                  bufferedWriter.flush();
	                  bufferedWriter.close();
	              }
	              if(fileWriter!=null){
	            	  fileWriter.close();  
	              }
	              
	              
	          } catch (IOException ex) {
	              ex.printStackTrace();
	          }
	      }
	}

	public static void appendInFile(String fileName, String dataToWrite){
		BufferedWriter bufferedWriter = null;
		FileWriter fileWriter = null;
	      
	      try {
	    	  fileWriter = new FileWriter(fileName, true);
	          bufferedWriter = new BufferedWriter(fileWriter);
	          bufferedWriter.write(dataToWrite);
	          bufferedWriter.newLine();
	          
	      } catch (FileNotFoundException ex) {
	          ex.printStackTrace();
	      } catch (IOException ex) {
	          ex.printStackTrace();
	      } finally {
	    	  
	          //Cierro BufferedWriter
	          try {
	              if (bufferedWriter != null) {
	                  bufferedWriter.flush();
	                  bufferedWriter.close();
	              }
	              if(fileWriter!=null){
	            	  fileWriter.close();  
	              }
	              
	              
	          } catch (IOException ex) {
	              ex.printStackTrace();
	          }
	      }
	}//Fin appendInFile
	
	public static void removeFirstLine(String fileName) {
		String extension = getFileExtension(fileName);
		
		File origFile = new File(fileName);
		File tempFile = new File(fileName.substring(0, fileName.indexOf(extension)-1) + "_temp" + extension);
		FileInputStream fin = null;
		
		try{
			//int ch;
			//StringBuffer strContent = new StringBuffer("");
			fin = new FileInputStream(fileName);
			DataInputStream in = new DataInputStream(fin);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			Integer lineNumber = 0;
			
			while ((strLine = br.readLine()) != null)   {				
				if(lineNumber!=0){
					appendInFile(tempFile.getName(), strLine);
				}
				lineNumber++;
			}
			in.close();
			fin.close();
			br.close();
			
		}
		catch(FileNotFoundException e){
			System.out.println("[" + fileName + "] - File " + fileName + " could not be found on filesystem");
		}
		catch(IOException ioe){
		   	System.out.println("[" + fileName + "] - Exception while reading the file" + ioe);
		}

		//Borro el archivo original
		origFile.delete();
		//Renonmbro el nuevo
		tempFile.renameTo(origFile);

			    
	}//Fin removeLineFromFile
	
	public static boolean exists(String fileName){
		FileInputStream fin = null;
		boolean exists = true;
		try{
			fin = new FileInputStream(fileName);	
		}
		catch(FileNotFoundException e){
			exists = false;
		}
		
		return exists;
		
	}//Fin exists
	
	public static String getFileExtension(String fileName){
		return fileName.substring(fileName.lastIndexOf("."), fileName.length());
	}
	
public static String fileToString(String fileName){
		
		String fileContent = new String();
		
		FileInputStream fin = null;

		try{
			fin = new FileInputStream(fileName);
			DataInputStream in = new DataInputStream(fin);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			Integer lineNumber = 1;

			while ((strLine = br.readLine()) != null)   {			
				fileContent += strLine;
				lineNumber++;
			}
			in.close();
			fin.close();
			br.close();
			
		}
		catch(FileNotFoundException e){
			System.out.println("[" + fileName + "] - File " + fileName + " could not be found on filesystem");
		}
		catch(IOException ioe){
		   	System.out.println("[" + fileName + "] - Exception while reading the file" + ioe);
		}
		
		return fileContent;
	}
	
	public static List<String> fileToArray(String fileName, List<String> filters){
		
		List<String> fileArrayList = new ArrayList<String>();
		
		FileInputStream fin = null;
	
		try{
			fin = new FileInputStream(fileName);
			DataInputStream in = new DataInputStream(fin);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			Integer lineNumber = 1;
	
			while ((strLine = br.readLine()) != null)   {		
				for(int i=0; i<filters.size(); i++){
					String filter = String.valueOf(filters.get(i));
					if(strLine.indexOf(filter)==-1){
						fileArrayList.add(strLine);
					}
				}
				
				lineNumber++;
			}
			in.close();
			fin.close();
			br.close();
			
		}
		catch(FileNotFoundException e){
			System.out.println("[" + fileName + "] - File " + fileName + " could not be found on filesystem");
		}
		catch(IOException ioe){
		   	System.out.println("[" + fileName + "] - Exception while reading the file" + ioe);
		}
		
		return fileArrayList;
	}

	public static List<String> fileToArray(String fileName, String filter){
		
		List<String> fileArrayList = new ArrayList<String>();
		
		FileInputStream fin = null;
	
		try{
			fin = new FileInputStream(fileName);
			DataInputStream in = new DataInputStream(fin);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			Integer lineNumber = 1;
	
			while ((strLine = br.readLine()) != null)   {		
				if(strLine.indexOf(filter)==-1){
					fileArrayList.add(strLine);
				}
				lineNumber++;
			}
			in.close();
			fin.close();
			br.close();
			
		}
		catch(FileNotFoundException e){
			System.out.println("[" + fileName + "] - File " + fileName + " could not be found on filesystem");
		}
		catch(IOException ioe){
		   	System.out.println("[" + fileName + "] - Exception while reading the file" + ioe);
		}
		
		return fileArrayList;
	}

	public static List<String> fileToArray(String fileName){
		
		List<String> fileArrayList = new ArrayList<String>();
		
		FileInputStream fin = null;

		try{
			fin = new FileInputStream(fileName);
			DataInputStream in = new DataInputStream(fin);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			Integer lineNumber = 1;

			while ((strLine = br.readLine()) != null)   {			
				fileArrayList.add(strLine);
				lineNumber++;
			}
			in.close();
			fin.close();
			br.close();
			
		}
		catch(FileNotFoundException e){
			System.out.println("[" + fileName + "] - File " + fileName + " could not be found on filesystem");
		}
		catch(IOException ioe){
		   	System.out.println("[" + fileName + "] - Exception while reading the file" + ioe);
		}
		
		return fileArrayList;
	}
	
	public static void arrayToFile(List <String> fileArrayList, String fileName){
		if(FileUtils.exists(fileName)){	
			try{
				RandomAccessFile file = new RandomAccessFile(fileName,  "rwd");
				file.setLength(0);
			}
			catch (FileNotFoundException fnfe){
				System.out.println("[" + fileName + "] - " + fnfe);
			}
			catch (IOException ioe){
				System.out.println("[" + fileName + "] - " + ioe);
			}			
			System.out.println("[" + fileName + "] - converting array to file - the file already exists");			
		}
			
		File fileFromArray = new File(fileName);
		for(int i=0; i<fileArrayList.size(); i++){			
			appendInFile(fileName, fileArrayList.get(i).toString());
		}			
			
	}
		
	public static void stringToFile(String stringToWrite, String fileName){
		if(FileUtils.exists(fileName)){	
			try{
				RandomAccessFile file = new RandomAccessFile(fileName,  "rwd");
				file.setLength(0);
			}
			catch (FileNotFoundException fnfe){
				System.out.println("[" + fileName + "] - " + fnfe);
			}
			catch (IOException ioe){
				System.out.println("[" + fileName + "] - " + ioe);
			}			
			System.out.println("[" + fileName + "] - converting array to file - the file already exists");			
		}
			
		File fileFromArray = new File(fileName);			
		appendInFile(fileName, stringToWrite);
	}
	
	public static void deployArrayInFile(List <String> fileArrayList, String fileName){
		if(FileUtils.exists(fileName)){	
			System.out.println("deployArrayInFile- File already exists");
			try{
				RandomAccessFile file = new RandomAccessFile(fileName,  "rwd");
				file.setLength(0);
			}
			catch (FileNotFoundException fnfe){
				System.out.println("[" + fileName + "] - " + fnfe);
			}
			catch (IOException ioe){
				System.out.println("[" + fileName + "] - " + ioe);
			}			
			System.out.println("[" + fileName + "] - deployArrayToFile - file already exists");			
		}
		else{
			System.out.println("deployArrayInFile- File does not exists");
			String dirName = fileName.substring(0, fileName.lastIndexOf("/"));
			if(!exists(dirName)){
				createDirectory(dirName);
				System.out.println("dirName created: " + dirName);
			}
		}
		File fileFromArray = new File(fileName);
		for(int i=0; i<fileArrayList.size(); i++){
			appendInFile(fileName, fileArrayList.get(i));
		}		
	}
	
	public static void deployStringInFile(String content, String fileName){
		if(FileUtils.exists(fileName)){	
			try{
				RandomAccessFile file = new RandomAccessFile(fileName,  "rwd");
				file.setLength(0);
			}
			catch (FileNotFoundException fnfe){
				System.out.println("[" + fileName + "] - " + fnfe);
			}
			catch (IOException ioe){
				System.out.println("[" + fileName + "] - " + ioe);
			}			
			System.out.println("[" + fileName + "] - deployArrayToFile - file already exists");			
		}
		else{
			String dirName = fileName.substring(0, fileName.lastIndexOf("/"));
			if(!exists(dirName)){
				createDirectory(dirName);
			}
		}
		File fileFromArray = new File(fileName);
		appendInFile(fileName, content);
	}
	
	public static void exchangeFiles(String fileToBeReplaced, String newFile){
		File origFile = new File(fileToBeReplaced);
		File tempFile = new File(newFile);
		
		if(!origFile.delete())
			System.out.println("[" + fileToBeReplaced + "] - Failed to delete the file because: " + getReasonForFileDeletionFailureInPlainEnglish(origFile));
		
		//Renombro el nuevo
		if (!tempFile.renameTo(origFile))
	        System.out.println("[" + fileToBeReplaced + "] - Could not rename file");
	}
	
	public static boolean exchangeFilesWithoutDeletion(String fileToBeReplaced, String newFile){
		File origFile = new File(fileToBeReplaced);
		File tempFile = new File(newFile);
		
		if (!tempFile.renameTo(origFile)){
	        System.out.println("[" + fileToBeReplaced + "] - Could not rename file");
	        return false;
		}
		return true;
	}
	
	public static boolean deleteFile(String fileName){
		File file = new File(fileName);
		return file.delete();		
	}
		 
	public static void changeLine(String fileName, Object searchTerm, String newValue, List<String> originalFileList) {
			Integer searchLine;
			try{
				searchLine = Integer.parseInt(searchTerm.toString());
			}
			catch(Exception e){
				searchLine = 0;
			}
		    //List<String> originalFileList = fileToArray(fileName);
		    List <String> newFileList = new ArrayList<String>();
		    for(int i=0; i<originalFileList.size(); i++){
		    	if(originalFileList.get(i).toString().indexOf(searchTerm.toString())==0){		    		
		    		newFileList.add(newValue);
		    	}
		    	else{
		    		newFileList.add(originalFileList.get(i));
		    	}
		    }
		    
		    //String tempFileName = fileName.substring(0, fileName.lastIndexOf(".")) + ".tmp";
		    
		    //arrayToFile(newFileList, tempFileName);
		    //deleteFile(fileName);
		    arrayToFile(newFileList, fileName); 
	 }
	
	public static void changeLine(String fileName, Object searchTerm, String newValue) {
			Integer searchLine;
			try{
				searchLine = Integer.parseInt(searchTerm.toString());
			}
			catch(Exception e){
				searchLine = 0;
			}
		    List<String> originalFileList = fileToArray(fileName);
		    List <String> newFileList = new ArrayList<String>();
		    for(int i=0; i<originalFileList.size(); i++){
		    	if(originalFileList.get(i).toString().indexOf(searchTerm.toString())==0){		    		
		    		newFileList.add(newValue);
		    	}
		    	else{
		    		newFileList.add(originalFileList.get(i));
		    	}
		    }
		    
		    String tempFileName = fileName.substring(0, fileName.lastIndexOf(".")) + ".tmp";
		    
		    arrayToFile(newFileList, tempFileName);
		    exchangeFiles(fileName, tempFileName);
		    /*
			//String extension = getFileExtension(fileName);

			BufferedWriter bufferedWriter = null;
			File origFile = new File(fileName);
			//File tempFile = new File(fileName.substring(0, fileName.indexOf(extension)-1) + "_temp" + extension);
			String tempFileName = fileName.substring(0, fileName.lastIndexOf(".")) + ".tmp";
			
			File tempFile = new File(tempFileName);
			FileInputStream fin = null;
			//String returnStr = "";
			
			try{
				//int ch;
				//StringBuffer strContent = new StringBuffer("");
				fin = new FileInputStream(fileName);
				DataInputStream in = new DataInputStream(fin);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String strLine;
				
				// BufferedWriter para escribir el archivo temporal
				bufferedWriter = new BufferedWriter(new FileWriter(tempFile.getName(), true));
		        
				
				Integer lineNumber = 1;
				while ((strLine = br.readLine()) != null)   {				
					if(strLine.indexOf(searchTerm.toString())==0 || lineNumber==searchLine){
						bufferedWriter.write(newValue);
						bufferedWriter.newLine();
					}
					else{
						bufferedWriter.write(strLine);
						bufferedWriter.newLine();
					}
					lineNumber++;
					bufferedWriter.flush();
				}
				br.close();
				in.close();
				fin.close();
				
			}
			catch(FileNotFoundException e){
				System.out.println("File " + fileName + " could not be found on filesystem");
			}
			catch(IOException ioe){
			   	System.out.println("Exception while reading the file" + ioe);
			}
			finally {
		    //Close BufferedWriter
		    try {
			    if (bufferedWriter != null) {
				    bufferedWriter.flush();
				    bufferedWriter.close();
			    }
			    } catch (IOException ex) {
			    	ex.printStackTrace();
			    }
			}
			
			//Borro el archivo original
			
			if(!origFile.delete())
				System.out.println("Failed to delete the file because: " + getReasonForFileDeletionFailureInPlainEnglish(origFile));
			
			//Renombro el nuevo
			if (!tempFile.renameTo(origFile))
		        System.out.println("Could not rename file");
		*/
		}//Fin removeLineFromFile
	
		public static String getReasonForFileDeletionFailureInPlainEnglish(File file) {
		    try {
		        if (!file.exists())
		            return "It doesn't exist in the first place.";
		        else if (file.isDirectory() && file.list().length > 0)
		            return "It's a directory and it's not empty.";
		        else
		            return "Somebody else has it open, we don't have write permissions, or somebody stole my disk.";
		    } catch (SecurityException e) {
		        return "We're sandboxed and don't have filesystem access.";
		    }
		  
		}
				
		public static List <String> listFilesInDirectory(String directoryName){
			
			List <String> returnList = new ArrayList();
			
			File dir = new File(directoryName);

			String[] children = dir.list();
			System.out.println(children);
			if (children == null) {
			    // Either dir does not exist or is not a directory
			} else {
			    for (int i=0; i<children.length; i++) {
			    	System.out.println(children[i]);
			        // Get filename of file or directory
			        String filename = children[i];
			        
			        if(filename.indexOf(".")!=-1){
			        	filename = filename.substring(0, filename.indexOf("."));
				        returnList.add(filename);			        	
			        }
			        
			    }
			}
			
			return returnList;
		}
		
		public static List <String> listFilesWithExtensionInDirectory(String directoryName){
			
			List <String> returnList = new ArrayList();
			
			File dir = new File(directoryName);
						
			String[] children = dir.list();
			if (children == null) {
			    // Either dir does not exist or is not a directory
			} else {
			    for (int i=0; i<children.length; i++) {
			        // Get filename of file or directory        
			        returnList.add(children[i]);			        
			    }
			}
			
			return returnList;
		}
		
		/*public static boolean createFile(String fileName){
			String dirName = fileName.substring(0, fileName.lastIndexOf("/"));
			if(!FileUtils.exists(dirName)){
				FileUtils.createDirectory(dirName);
			}
			new File(fileName);
			return true;
		}*/
		
		public static File createFile(String fileName){
			String dirName = fileName.substring(0, fileName.lastIndexOf("/"));
			if(!FileUtils.exists(dirName)){
				FileUtils.createDirectory(dirName);
			}
			return new File(fileName);
		}

		 public static boolean createDirectory (String dirName) {
			  try{
				  String strManyDirectories=dirName;
		
				  boolean success = (new File(strManyDirectories)).mkdirs();
				  if (success) {
					  System.out.println("Directories: " + strManyDirectories + " created");
				  }
				  return success;
		
			  }catch (Exception e){
				  System.err.println("Error: " + e.getMessage());
				  return false;
			  }
		}
		 
		public static void copyFile(File sourceFile, File destFile)  throws IOException {
				if (!sourceFile.exists()) {
				    return;
				}
				if (!destFile.exists()) {
				    destFile.createNewFile();
				}
				
				FileChannel source = null;
					FileChannel destination = null;
					source = new FileInputStream(sourceFile).getChannel();
					destination = new FileOutputStream(destFile).getChannel();
					if (destination != null && source != null) {
					    destination.transferFrom(source, 0, source.size());
					}
					if (source != null) {
					    source.close();
					}
					if (destination != null) {
					    destination.close();
					}
			
		}

}
