/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpserver;

import java.net.*;
import java.io.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatServerThread extends Thread
{  
   private TCPServer       server    = null;
   private Socket           socket    = null;
   private int              ID        = -1;
   private DataInputStream  streamIn  =  null;
   private DataOutputStream streamOut = null;
   public int loggedin=0;
   private String username;
   
   public ChatServerThread(TCPServer _server, Socket _socket)
   {  super();
      server = _server;
      socket = _socket;
      ID     = socket.getPort();
      
   }
   public void send(String msg)
   {   try
       {  streamOut.writeUTF(msg);
          streamOut.flush();
       }
       catch(IOException ioe)
       {  System.out.println(ID + " ERROR sending: " + ioe.getMessage());
          server.remove(ID);
          stop();
       }
   }
   
   //check if logged in
   public void setstatus(int i,String name)
   {
       loggedin=i;
       username=name;
   }
   //get the login status
   public int getstatus()
   {
       if(loggedin==1)
       {
           return 1;
       }
       else
       {
           return 0;
       }
   }
   
   //show username
   public String showusername()
   {
       return username;
   }
   
   
   public int getID()
   {  return ID;
   }
   public void run()
   {  System.out.println("Server Thread " + ID + " running.");
      while (true)
      {  try
         {  try {
             server.handle(ID, streamIn.readUTF());
          } catch (SQLException ex) {
              Logger.getLogger(ChatServerThread.class.getName()).log(Level.SEVERE, null, ex);
          }
         }catch(IOException ioe)
         {  System.out.println(ID + " ERROR reading: " + ioe.getMessage());
            server.remove(ID);
            stop();
         }
      }
   }
   public void open() throws IOException
   {  
       streamIn = new DataInputStream(new 
                        BufferedInputStream(socket.getInputStream()));
      streamOut = new DataOutputStream(new
                        BufferedOutputStream(socket.getOutputStream()));
   }
   public void close() throws IOException
   {  if (socket != null)    socket.close();
      if (streamIn != null)  streamIn.close();
      if (streamOut != null) streamOut.close();
   }
}