package test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.Executors;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class orderFile {
	private static final long MEGABYTE = 1024L * 1024L;
	private static ArrayList<String[]> employeeData1 = new ArrayList<String[]>();
	private static ArrayList<Scanner> fileChunksPointer = new ArrayList<Scanner>();
	private static ArrayList<String> linesDataArr = new ArrayList<String>();
	private static ArrayList<String> wholeLineData = new ArrayList<String>();
	static int totalChunks ;

	public static long bytesToMegabytes(long bytes) {
		return bytes / MEGABYTE;
	}

	public static void main(String[] args) throws Exception,OutOfMemoryError {
		// TODO Auto-generated method stub

		InputStream inputStream = null;

		String InputfileName = "largefile.txt";
		Path path = Paths.get(InputfileName);


		try {

			long allowAbleMem=Runtime.getRuntime().freeMemory(); 

			if(bytesToMegabytes(allowAbleMem) > 500)
				allowAbleMem -= allowAbleMem-(500*MEGABYTE);


			inputStream = new FileInputStream(InputfileName);
			System.out.println("Memory usage in MB : "+bytesToMegabytes(allowAbleMem));

			long bytes = Files.size(path);
		

			System.out.println("File Size : "+bytesToMegabytes(bytes));
			totalChunks =  split(InputfileName, allowAbleMem);


			// totalChunks =18;

			if(totalChunks!=0) {
				sortChunks(totalChunks); // ----------Sorting Each chunks individually ------------

			}




			System.out.println("After Chunks Sorting");

			populate_file_chunks_arrays();

			FileWriter writerOut = new FileWriter("output.txt", true);
			String empId = "";
			int totaloutputlines =0;

			while(fileChunksPointer.size()>0) { // if we have file to be added into final outuut file then take first lines from all of files 

				int minIndex =0;
				String idComp = linesDataArr.size()>0?linesDataArr.get(0).toString():"";
				// -----------------Find the  file whose first line has minimum value ------------------------------------
				for (int cc=0;cc< linesDataArr.size();cc++) {
					if(idComp=="") {
						idComp = linesDataArr.get(cc);
					}
					if(linesDataArr.get(cc).toString().compareTo(idComp)<0) {
						idComp = linesDataArr.get(cc).toString();
						minIndex =cc;;
					}

				}
				// -----------------Find the  file whose first line has minimum value------------------------------------

				if(wholeLineData.size()>0) {
					totaloutputlines++;
					writerOut.write(wholeLineData.get(minIndex).replace(",", "\t"));

				}


				// -----------------Update the pointer of minimum value's line to next line ------------------------------------
				update_file_pointer(minIndex);
				// -----------------Update the pointer of minimum value's line to next line ------------------------------------


			}


			long afterUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
			long actualMemUsed=afterUsedMem-allowAbleMem;
			System.out.println("Actual Memory Used IN MB :"+bytesToMegabytes(actualMemUsed));




		}  catch (IOException i ) {

			System.err.println(employeeData1.size());
			System.err.println("IOException: "+i );

		} catch (OutOfMemoryError oome) {

			System.err.println("Array size too large ");
			System.err.println("Max JVM memory: " +bytesToMegabytes(Runtime.getRuntime().maxMemory()) );


		}

		finally {
			if (inputStream != null) {
				inputStream.close();


			}

		}
	}


	public static void populate_file_chunks_arrays() {
		try {
			for(int j=1;j<=totalChunks;j++) {

				fileChunksPointer.add(new Scanner(new File(("chunkSorted" + j + "out.txt"))));

				Scanner chunkSc = fileChunksPointer.get(j-1);
				if(chunkSc.hasNext()) {
					String tmp = chunkSc.nextLine();
					wholeLineData.add(tmp+"\r\n");
					String  id = tmp.split("\\s+")[0];
					linesDataArr.add(id);

				}

			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void update_file_pointer(int minIndex) {
		if(fileChunksPointer.get(minIndex).hasNext()) {
			String tmp =fileChunksPointer.get(minIndex).nextLine();
			if(wholeLineData.size()>minIndex && linesDataArr.size()>minIndex) {

				wholeLineData.set(minIndex,tmp+"\r\n");
				String  id = tmp.split("\\s+")[0];

				linesDataArr.set(minIndex,id );
			}
		}else {
			if(fileChunksPointer.size()>minIndex)
				fileChunksPointer.remove(minIndex);
			if(wholeLineData.size()>minIndex)
				wholeLineData.remove(minIndex);
			if(linesDataArr.size()>minIndex)
				linesDataArr.remove(minIndex);
		}

	}

	public static int split(String FilePath, long splitlen) {
		int count = 1;
		long outfilesize =0;
		int totalinputlines =0;
		try {
			File filename = new File(FilePath);


			FileInputStream fis=new FileInputStream(FilePath);       
			Scanner sc=new Scanner(fis);    //file to be scanned  



			while(sc.hasNextLine())  
			{ 
				String lineString =sc.nextLine()+"\r\n";
				int tempsize = lineString.length();
				filename = new File("chunk" + count + ".txt");
				System.out.println("Chunks File Name"+filename.getName());
				FileWriter writer = new FileWriter(filename, true);

				outfilesize = 0;
				boolean outerwrite =false;

				while(sc.hasNextLine())  {
					if(outerwrite) {
						lineString =sc.nextLine()+"\r\n";
						tempsize = lineString.length();
					}
					if (outfilesize + tempsize > splitlen)
						break;
					outerwrite =true;
					writer.write(lineString);

					totalinputlines++;
					outfilesize +=tempsize;
				}


				count++;


				writer.close();

			}  
			sc.close();     //closes the scanner  

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Total Input LIne"+totalinputlines);
		return count-1;
	}

	public static void sortChunks(int totalChunks) throws IOException {
		try {
			for (int i = 1; i <= totalChunks; i++) 
			{


				File filename = new File("chunk" + i + ".txt");

				System.out.println("Working On : "+"chunk" + i + ".txt");
				Scanner myReader;
				// Sort Chunk file ...................................


				myReader = new Scanner(filename);


				System.out.println("Before Loop ");
				while (myReader.hasNextLine()) {
					String data = myReader.nextLine();

					String [] linedata = data.split("\\s+");
					employeeData1.add(linedata);


					// Sort Chunk file ...................................


				}
				myReader.close();
				filename.delete();
				Collections.sort(employeeData1,new Comparator<String[]>() {
					public int compare(String[] strings, String[] otherStrings) {
						return (strings[1]).compareTo((otherStrings[1]));
					}
				});

				Collections.sort(employeeData1,new Comparator<String[]>() {
					public int compare(String[] strings, String[] otherStrings) {
						return (strings[0]).compareTo((otherStrings[0]));
					}
				});

				System.out.println("After Loop ");

				// delete chunk file..................

				System.out.println("output file : "+"chunkSorted" + i + ".txt");
				File writename = new File("chunkSorted" + i + "out.txt");
				// Overwrite the sorted Data back to chunk file so we can use minimum space and reuse hard disk memory.
				FileWriter writer = new FileWriter(writename, false);

				System.out.println("Before Writing to out file "+ filename.getName()); 



				String linedata = "";
				System.out.println();
				for (String[] sa : employeeData1) {
					linedata = (Arrays.toString(sa)+"\r\n").replaceAll("[\\[\\]\\(\\)]", "");
					//System.out.println(linedata);
					writer.write(linedata.toString());
					// 
				}

				System.out.println("After Writing to output file");
				writer.close();
				fileChunksPointer.add(new Scanner(new File(writename.getName())));

				//filemaniplation.writeToChunkFile(employeeData1,fileName + i + ".txt");
				employeeData1.clear();




			} 


		}catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
}
