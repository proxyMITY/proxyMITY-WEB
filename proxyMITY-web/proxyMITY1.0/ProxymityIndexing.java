
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;


public class ProxymityIndexing {

	
	final static String videoListName = "./videoList.xml";
	final static String tagsFileName = "./tags.json";
	final static File files[] = (new File("./data/xml")).listFiles();
	static boolean created = false;
	static boolean created2 = false;
	static int lastVideoIndex = 0;
	static Map<String, Integer> tagMap = new HashMap<String, Integer>();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("\n\n------------------------ INDEXING ----------------------\n");
		
		int i;
		File videoList = new File(videoListName);
		File tagsFile = new File(tagsFileName);
		/*String fileName;
		Arrays.sort(files);
		for (int i = 0, n = files.length; i < n; i++) {
			fileName = files[i].toString();
			System.out.println("fileName = "+fileName);
			
		}*/
		
		
		try {
			created = videoList.createNewFile();
			created2 = tagsFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//FileWriter fstream;
		//BufferedWriter out;
		ArrayList<String> filesToBeUpdated = new ArrayList<String>();
		if(created && created2){
			System.out.println("\nvideoList and taglist do not exist");
			
			try {
				//fstream = new FileWriter(videoListName);
				//out = new BufferedWriter(fstream);
                 
				for (i = 0; i < files.length; i++) {
					//out.write(files[i].toString()+"\n");
					filesToBeUpdated.add(files[i].toString());
				}
				//out.close();
				//set the index to last video
				//lastVideoIndex = i-1;
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else{
			System.out.println("videoList exists");
			LineNumberReader lnr = null;
			try {
				lnr = new LineNumberReader(new FileReader(videoListName));
				lnr.skip(Long.MAX_VALUE);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("num lines in videoList = "+lnr.getLineNumber());
			if((lnr.getLineNumber()-3)/7 != files.length){
				ArrayList<String> videoListFiles = new ArrayList<String>();
				
				FileInputStream finstream;
				try {
					finstream = new FileInputStream(videoListName);
					// Get the object of DataInputStream
					DataInputStream in = new DataInputStream(finstream);
					BufferedReader br = new BufferedReader(new InputStreamReader(in));
					String strLine;
					//Read File Line By Line
					i=1;
					int j=1;
					while ((strLine = br.readLine()) != null){
						// Print the content on the console
						System.out.println("i = "+i+" 2+j*7-1 = "+(2+j*7-1)+" strLine = "+strLine);
						
						if(i == (2+j*7-1)){
							System.out.println("xmlname = "+strLine.substring(11, strLine.length()-10));
							videoListFiles.add(strLine.substring(11, strLine.length()-10));
							j++;
							lastVideoIndex++;
						}
						i++;
					}
					//Close the input stream
					in.close();
					
					//check for entries whether present in file or not
					for (i = 0; i < files.length; i++) {
						if(!videoListFiles.contains(files[i].toString()))
							filesToBeUpdated.add(files[i].toString());
					}
					
					
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}//end of if-else for file exists
		
		//if there is some addition, update the entries
		if(!filesToBeUpdated.isEmpty())
			processXML(filesToBeUpdated);
		
		System.out.println("\nUpdated entries = "+filesToBeUpdated.size());
		System.out.println("\n\n----------------------- SUCCESSFUL ----------------------\n");
		

	}

	private static void processXML(final ArrayList<String> filesToBeUpdated) {
		// TODO Auto-generated method stub
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser;
		
		try {
			int i;
			saxParser = factory.newSAXParser();
			FileWriter fstream = new FileWriter(videoListName, true);
			final BufferedWriter out = new BufferedWriter(fstream);
			
			FileWriter fstreamTags = new FileWriter(tagsFileName, true);
			final BufferedWriter outTags = new BufferedWriter(fstreamTags);
			
			
			
			/*
			 [
				{value: "21", name: "Mick Jagger"},
				{value: "43", name: "Johnny Storm"},
				{value: "46", name: "Richard Hatch"},
				{value: "54", name: "Kelly Slater"},
				{value: "55", name: "Rudy Hamilton"},
				{value: "79", name: "Michael Jordan"}
			  ]*/
			
			DefaultHandler handler = new DefaultHandler() {
				 
				boolean bVidName = false;
				boolean bCName = false;
				boolean bPresentn = false;
				boolean bSpeaker = false;
				boolean bSlName = false;
			 
                @Override
				public void startElement(String uri, String localName,String qName, 
			                Attributes attributes) throws SAXException {
			 
					System.out.println("Start Element : " + qName);
					try {
						if(qName.equalsIgnoreCase("theme")) {
							System.out.println("themename = "+attributes.getValue(0));
							parseTags(attributes.getValue(0));
							
						}else if(qName.equalsIgnoreCase("slidename")) {
							bSlName = true;
							
						}else if(qName.equalsIgnoreCase("videoname")) {
							bVidName = true;
							out.write("\t<Video>\n");
							out.write("\t\t<VideoName>");
							
						}else if(qName.equalsIgnoreCase("coursename")) {
							bCName = true;
							out.write("\t\t<CourseName>");
							
						}else if(qName.equalsIgnoreCase("LectureName")) {
							bPresentn = true;
							out.write("\t\t<LectureName>");
							
						}else if(qName.equalsIgnoreCase("speaker")) {
							bSpeaker = true;
							out.write("\t\t<Speaker>");
							
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			 
				

                @Override
				public void endElement(String uri, String localName,
					String qName) throws SAXException {
			 
					System.out.println("End Element : " + qName);
			 
				}
			 
                @Override
				public void characters(char ch[], int start, int length) throws SAXException {
			 
					try {
						if (bSlName) {
							//System.out.println("slide Name : " + new String(ch, start, length));
							bSlName = false;
							
						} else if (bVidName) {
							//System.out.println("video Name : " + new String(ch, start, length));
							out.write(new String(ch, start, length));
							out.write("</VideoName>\n");
							
							parseTags(new String(ch, start, length));
							bVidName = false;
							
						} else if (bCName) {
							//System.out.println("course Name : " + new String(ch, start, length));
							out.write(new String(ch, start, length));
							out.write("</CourseName>\n");
							
							parseTags(new String(ch, start, length));
							//outTags.write("\t{ value : \""+lastVideoIndex+"\", name : \""+new String(ch, start, length)+"\" },\n");
							bCName = false;
							
						} else if (bPresentn) {
							//System.out.println("presentation : " + new String(ch, start, length));
							out.write(new String(ch, start, length));
							out.write("</LectureName>\n");
							
							parseTags(new String(ch, start, length));
							bPresentn = false;
							
						} else if (bSpeaker) {
							//System.out.println("speaker : " + new String(ch, start, length));
							out.write(new String(ch, start, length));
							out.write("</Speaker>\n");
							
							parseTags(new String(ch, start, length));
							bSpeaker = false;
							
						}
					
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			
			
			boolean isFirstTag = false;
			
			//if new file is created
			if(created && created2){
				out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
				out.write("<ProxyMITYVideos>\n");
				outTags.write("[");
				isFirstTag = true;
				
			} else{
	             RandomAccessFile raf = new RandomAccessFile(videoListName, "rw");
	             RandomAccessFile raf2 = new RandomAccessFile(tagsFileName, "rw");
	            // System.out.println("File Length="+raf.length());
	             //last line is of 18 chars
	             raf.setLength(raf.length() - 18);
	             raf2.setLength(raf2.length() - 2);
	             //System.out.println("File Length="+raf.length());
	             raf.close();
	             raf2.close();
			}
			
			System.out.println("last video index ="+lastVideoIndex);
			
			for(i=0; i<filesToBeUpdated.size(); i++){
				//call the parser
				System.out.println("\nNow parsing "+filesToBeUpdated.get(i));
				
				saxParser.parse(filesToBeUpdated.get(i), handler);
				
				out.write("\t\t<xmlName>"+filesToBeUpdated.get(i)+"</xmlName>\n");
				out.write("\t</Video>\n");
				
				for(Map.Entry<String, Integer> entry : tagMap.entrySet()) {
				  //  System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
					if(isFirstTag){
						outTags.write("\n\t{ \"value\" : \""+entry.getValue()+"\", \"name\" : \""+entry.getKey()+"\" }");
						isFirstTag = false;
					} else
						outTags.write(",\n\t{ \"value\" : \""+entry.getValue()+"\", \"name\" : \""+entry.getKey()+"\" }");
				}
				
				tagMap.clear();
				lastVideoIndex++;
			}
			
			//end the file
			out.write("</ProxyMITYVideos>");
			outTags.write("\n]");
			
			//for(i = 0; i < filesToBeUpdated.size(); i++) {
				//out.write(filesToBeUpdated.get(i)+"\n");
			//}
			
			//close the file
			out.close();
			outTags.close();
			
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 
		
		
		
	}
	
	
	
	public static void parseTags(String valueString) {
		String tagArray[] = valueString.split("\\s+");
		for(int i=0; i < tagArray.length; i++ ){
			//System.out.println("tagArray["+i+"] = "+tagArray[i]);
			if(!(tagArray[i].equals("to") || tagArray[i].equals("is") || tagArray[i].equals("in") || tagArray[i].equals("of")
					 || tagArray[i].equals("Prof."))){
				
				if(!tagMap.containsKey(tagArray[i])){
					tagMap.put(tagArray[i], lastVideoIndex);
					System.out.println("tagmap put = "+tagArray[i]);
				}
			}
		}
		
	}

}
