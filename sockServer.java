package simpleSocketServer2;

import java.io.IOException;
import java.io.BufferedReader;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class sockServer implements Runnable
{
	   Socket csocket;
	   String ipString;
	   char threadType;

	   static Vector<String> vec = new Vector<String>(5);
	   
	   static final String newline = "\n";
	   static int first_time = 1;
	   
	   static int port_num = 3333;
	   
	   static int numOfConnections = 0;
	   static int numOfMessages = 0;
	   static int max_connections = 5;
	   static int numOfTransactions = 0; 

	   sockServer(Socket csocket, String ip)
	   {
	      this.csocket  = csocket;
	      this.ipString = ip;
	   } 

	   public static void runSockServer()   // throws Exception
	   {
	     boolean sessionDone = false;
	  
	     ServerSocket ssock = null;
	   
	     try
	     {
		   ssock = new ServerSocket(port_num);
	     }
	     catch (BindException e)
	     {
		    e.printStackTrace();
	     }
	     catch (IOException e)
	     {
		    e.printStackTrace();
	     }
	 
	     // update the status text area to show progress of program
	     try 
	     {
		     InetAddress ipAddress = InetAddress.getLocalHost();
		     sss2.textArea.append("IP Address : " + ipAddress.getHostAddress() + newline);
	     }
	     catch (UnknownHostException e1)
	     {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
	     }
	 
	     sss2.textArea.append("Listening on port " + port_num + newline);
	     sss2.textArea.setCaretPosition(sss2.textArea.getDocument().getLength());
	     sss2.textArea.repaint();
	 
	     sessionDone = false;
	     while (sessionDone == false)
	     {
	        Socket sock = null;
		    try
		    {
		    // blocking system call
			   sock = ssock.accept();
		    }
		    catch (IOException e)
		    {
			   e.printStackTrace();
		    }
		 
		    // update the status text area to show progress of program
	        sss2.textArea.append("Client Connected : " + sock.getInetAddress() + newline);
	        sss2.textArea.setCaretPosition(sss2.textArea.getDocument().getLength());
	        sss2.textArea.repaint();
	        
	        new Thread(new sockServer(sock, sock.getInetAddress().toString())).start();
	     }
	 
	     try 
	     {
		    ssock.close();
	     }
	     catch (IOException e) 
	     {
		    e.printStackTrace();
	     }
	}	  

	// This is the thread code that ALL clients will run()
	public void run()
	{
	   try
	   {
		  boolean session_done = false; 
	      long threadId;
	      String clientString;
	      String keyString = "";
	    
	      threadId = Thread.currentThread().getId();
	      
	      numOfConnections++;
	      
	      sss2.textArea.append("Num of Connections = " + numOfConnections + newline);
	      sss2.textArea.setCaretPosition(sss2.textArea.getDocument().getLength());
	      sss2.textArea.repaint();
	      
	      keyString = ipString + ":" + threadId;
	      
	      if (vec.contains(keyString) == false)
	        {
	    	    int counter = 0;
	        	vec.addElement(keyString);
	        	
	        	sss2.textArea_2.setText("");
	        	Enumeration<String> en = vec.elements();
	        	while (en.hasMoreElements())
	        	{
	        		sss2.textArea_2.append(en.nextElement() + " || ");
	        		
	        		if (++counter >= 6)
	        		{
	        			sss2.textArea_2.append("\r\n");
	        			counter = 0;
	        		}
	        	}

  	            sss2.textArea_2.repaint();
	        }
	       
	      PrintStream pstream = new PrintStream (csocket.getOutputStream());
	      BufferedReader rstream = new BufferedReader(new InputStreamReader(csocket.getInputStream()));
	       
	      while (session_done == false)
	      {
	       	if (rstream.ready())   // check for any data messages
	       	{
	              clientString = rstream.readLine();
	              
	              // update the status text area to show progress of program
	   	           sss2.textArea.append("RECV : " + clientString + newline);
	     	       sss2.textArea.setCaretPosition(sss2.textArea.getDocument().getLength());
	     	       sss2.textArea.repaint();
	     	       // update the status text area to show progress of program
	     	       sss2.textArea.append("RLEN : " + clientString.length() + newline);
	     	       sss2.textArea.setCaretPosition(sss2.textArea.getDocument().getLength());
	     	       sss2.textArea.repaint();
	              
	              if (clientString.length() > 128)
	              {
	           	   session_done = true;
	           	   continue;
	              }

	              if (clientString.contains("quit"))
	              {
	                 session_done = true;
	              }
	              else if (clientString.contains("QUIT"))
	              {
	                 session_done = true;
	              }
	              else if (clientString.contains("Quit"))
	              {
	                 session_done = true;
	              }
	              else if (clientString.contains("Date>"))
	              {
	            	numOfMessages++;
	            	  
	            	// Create an instance of SimpleDateFormat used for formatting 
	            	// the string representation of date (month/day/year)
	            	DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

	            	// Get the date today using Calendar object.
	            	Date today = Calendar.getInstance().getTime();
	            	
	            	// Using DateFormat format method we can create a string 
	            	// representation of a date with the defined format.
	            	String reportDate = df.format(today);

	            	// Print what date is today!
	            	pstream.println("Num Of Messages : " + numOfMessages + "   Simple Date: " + reportDate);
	              }
	       	   }
	         			    		        	
	           Thread.sleep(500);
	           
	        }    // end while loop
	
            keyString = ipString + ":" + threadId;
	      
	        if (vec.contains(keyString) == true)
	        {
	        	int counter = 0;
	        	vec.removeElement(keyString);
	        	
	        	sss2.textArea_2.setText("");
	        	Enumeration<String> en = vec.elements();
	        	while (en.hasMoreElements())
	        	{        		     		
                    sss2.textArea_2.append(en.nextElement() + " || ");
	        		
	        		if (++counter >= 6)
	        		{
	        			sss2.textArea_2.append("\r\n");
	        			counter = 0;
	        		}
	        	}

  	            sss2.textArea_2.repaint();
	        }
	      
	        numOfConnections--;

	        // close client socket
	        csocket.close();
	       
	        // update the status text area to show progress of program
		     sss2.textArea.append("Child Thread : " + threadId + " : is Exiting!!!" + newline);
		     sss2.textArea.append("Num of Connections = " + numOfConnections);
		     sss2.textArea.setCaretPosition(sss2.textArea.getDocument().getLength());
		     sss2.textArea.repaint();
		     
	     } // end try  
	 
	     catch (SocketException e)
	     {
		  // update the status text area to show progress of program
	      sss2.textArea.append("ERROR : Socket Exception!" + newline);
	      sss2.textArea.setCaretPosition(sss2.textArea.getDocument().getLength());
	      sss2.textArea.repaint();
	     }
	     catch (InterruptedException e)
	     {
		  // update the status text area to show progress of program
	      sss2.textArea.append("ERROR : Interrupted Exception!" + newline);
	      sss2.textArea.setCaretPosition(sss2.textArea.getDocument().getLength());
	      sss2.textArea.repaint();
	     }
	     catch (UnknownHostException e)
	     {
		  // update the status text area to show progress of program
	      sss2.textArea.append("ERROR : Unkonw Host Exception" + newline);
	      sss2.textArea.setCaretPosition(sss2.textArea.getDocument().getLength());
	      sss2.textArea.repaint();
	     }
	     catch (IOException e) 
	     {
	     // update the status text area to show progress of program
	      sss2.textArea.append("ERROR : IO Exception!" + newline);
	      sss2.textArea.setCaretPosition(sss2.textArea.getDocument().getLength());
	      sss2.textArea.repaint();       
	     }     
	     catch (Exception e)
	     { 
		  numOfConnections--;
		  
		  // update the status text area to show progress of program
	      sss2.textArea.append("ERROR : Generic Exception!" + newline);
	      sss2.textArea.setCaretPosition(sss2.textArea.getDocument().getLength());
	      sss2.textArea.repaint(); 
	     }
	   
	  }  // end run thread method
}
