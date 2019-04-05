package tcpserver;
import java.net.*;
import java.io.*;
import java.sql.*;
import java.util.*;

public class TCPServer implements Runnable
{ 
   //connections
   private Connection con;
   private ResultSet rs;
   private Statement st;
   
   
   private ChatServerThread clients[] = new ChatServerThread[50];
   private String users[]=new String[50];
   //chatrooms
   private int chat_fun[]=new int[50];
   
   private ServerSocket server = null;
   private Thread       thread = null;
   private int clientCount = 0;

   public TCPServer (int port)
   {  try
      {  
        //For connection for database
        try 
        {
            con=DriverManager.getConnection("jdbc:mysql://localhost:3306/chattest","root","");
            
            
        } catch (Exception e) {
            System.out.println("Error"+e);
        }
          
         Arrays.fill(users," ");       
         System.out.println("Binding to port " + port + ", please wait  ...");
         server = new ServerSocket(port);  
         System.out.println("Server started: " + server);
         start(); }
      catch(IOException ioe)
      {  System.out.println("Can not bind to port " + port + ": " + ioe.getMessage()); }
   }
@Override
   public void run()
   {  while (thread != null)
      {  try
         {  System.out.println("Waiting for a client ..."); 
            addThread(server.accept()); }
         catch(IOException ioe)
         {  System.out.println("Server accept error: " + ioe); stop(); }
      }
   }
   public void start()  { if (thread == null)
      {  thread = new Thread(this); 
         thread.start();
      }
   }
   public void stop()   { 
   if (thread != null)
      {  thread.stop(); 
         thread = null;
      }
   }
   
   
   private int findClient(int ID)
   {  for (int i = 0; i < clientCount; i++)
         if (clients[i].getID() == ID)
            return i;
      return -1;
   }
   
   //Check and register individuals
   private int Register(String name,String password) throws SQLException
   {
       int value;
       DBworks con= new DBworks();
       value=con.reg(name, password);
       if(value==1)
       {
           return 1;
       }
       else
           return 0;
   }
   
   
   //login
   private int Login(String username, String password) throws SQLException
   {
       int val=0,check;
       //check if already logged in
       for(int i=0;i<clientCount;i++)
       {
           if(users[i].equals(username))
           {
               val=1;
           }
       }
       
       if(val==1)
       {
           return 0;
       }
       else
       {
           DBworks con= new DBworks();
           check=con.login(username,password);
           if(check==0)
           {
               return 3;
           }
           else
           {
               return 1;   
           }
           
           
       }
       
   }
   
   
//   //Request to be friend
//   public int requestfriend(int id,String Sname) throws SQLException
//   {
//        int count=0;
//        String Rname=users[id];
//        String query1="select * from friends where "
//                + "((freceiver='"+Rname+"' and fsender='"+Sname+"')or(freceiver='"+Sname+"' and fsender='"+Rname+"'))and (status='accepted' or status='pending')";
//        st=con.createStatement();
//        rs=st.executeQuery(query1);
//        while(rs.next())
//        {
//            count+=1;
//        }
//        
//        if(count>0)
//        {
//            st.close();
//            return 0;
//        }
//        else
//        {
//            String query2="INSERT INTO friends (freceiver,fsender,status) VALUES ('"+Rname+"','"+Sname+"','pending')";
//            st.executeUpdate(query2);
//            st.close();
//            return 1;
//        } 
//             
//   }
   
   
   public int addfriend(int id,String Sname) throws SQLException
   {
        int count=0;
        String Rname=users[id];
        String query1="select * from friends where (freceiver='"+Rname+"' and fsender='"+Sname+"') or (freceiver='"+Sname+"' and fsender='"+Rname+"')";
        st=con.createStatement();
        rs=st.executeQuery(query1);
        while(rs.next())
        {
            count+=1;
        }
        
        if(count>0)
        {
            st.close();
            return 0;
        }
        else
        {
            String query2="INSERT INTO friends (freceiver,fsender) VALUES ('"+Rname+"','"+Sname+"')";
            st.executeUpdate(query2);
            st.close();
            return 1;
        }
  }
   
 
   public int checkfriend(int id,String Sname) throws SQLException
   {
       int count=0;
        String Rname=users[id];
        String query1="select * from friends where (freceiver='"+Rname+"' and fsender='"+Sname+"') or (freceiver='"+Sname+"' and fsender='"+Rname+"')";
        st=con.createStatement();
        rs=st.executeQuery(query1);
        while(rs.next())
        {
            count+=1;
        }
        
        if(count>0)
        {
            st.close();
            return 1;
        }
        else
        {
            return 0;
        }
   }
   
   
   
   
   
   public synchronized void handle(int ID, String input) throws SQLException
   {   
       String[] words= input.split(" ");
      
      if(words[0].equals("reg"))
      {
          //System.out.println(words[0]);
          String user=words[1];
          String pass=words[2];
          int val;
          val=Register(user,pass);
          int id=findClient(ID);
          
          if(val==1)
          {
              clients[findClient(ID)].send("Successfully registered\n");
              
          }
          else
          {
              clients[findClient(ID)].send("User already exists!\nTry a new username");
          }
          
      }
      else if(words[0].equals("login"))
      {
          String uname=words[1];
          String pass=words[2];
          
          int check;
          check=Login(uname,pass);
          
          if(check==0)
          {
              clients[findClient(ID)].send("User already Logged in!!\n");
              
          }
          else if(check==3)
          {
              clients[findClient(ID)].send("Incorrect Username or password!\n");
              
          }
          else
          {
              clients[findClient(ID)].send("Welcome : "+uname+"  :)\n");
              int id=findClient(ID);
              users[id]=uname;
              clients[id].setstatus(1,uname);
              st=con.createStatement();
              String query="select count(*) from pending_messages where receiver='"+uname+"'";
              rs=st.executeQuery(query);
              int s=0;
              while(rs.next())
              {
                //String username=rs.getString("username");
                //String pass=rs.getString("password");
                s=rs.getInt(1);
                System.out.println(s);
                
              }
              
              if(s>0)
              {
                  String q="select * from pending_messages where receiver='"+uname+"'";
                  rs=st.executeQuery(q);
                  while(rs.next())
                  {
                    String sender=rs.getString("sender");
                    String message=rs.getString("message");
                    clients[findClient(ID)].send(sender+" (pm): "+message+"");
                    
                  }
                  String q1="delete from pending_messages where receiver='"+uname+"'";
                  st.executeUpdate(q1);
                  st.close();
              }
              else
              {
                  st.close();
                  clients[findClient(ID)].send("You dont have any pending messages to show\n");
                  
              }
              
          }
          
      }
      else if(words[0].equals("show_all_users"))
      {
          clients[findClient(ID)].send("\nOnline Users are:");
          for(int i=0;i<clientCount;i++)
          {
              String name=clients[i].showusername();
              clients[findClient(ID)].send(name);
          }
          
      }
     else if(words[0].equals("add"))
     {   int status;
         String fname=words[2];
         int id=findClient(ID);
         status=addfriend(id,fname);
         //requestfriend(id,fname);
         
         if(status==0)
         {
            clients[findClient(ID)].send("Already added as a friend!");
             
         }
         else
         {
             int friendsid = 0;
             for(int i=0;i<users.length;i++)
             {
                 if(users[i]==fname)
                 {
                     friendsid=i;
                 }
             }
             clients[friendsid].send(users[findClient(ID)]+" Added you as a friend :) \n you can chat with him by keywords"
                     + ": send username message");
             clients[findClient(ID)].send("Successfully added "+fname+" as your friend");
             
         }
         
     }
     else if(words[0].equals("send"))
     {
         String receipient=words[1];
         String message="";
         for(int i=2;i<words.length;i++)
         {
             message+=" "+words[i];
         }
         
         
         int id=findClient(ID);
         String sender=users[id];
         //System.out.println("pm sender:  "+sender);
         int status=checkfriend(id, receipient);
         
         if(status==0)
         {
             clients[findClient(ID)].send(receipient+" Not listed as friend!\n Please add as friend to chat in private");
             
         }
         else
         {
            int found=0,r_id=0;
            for(int i=0;i<users.length;i++)
            {
                if(users[i].equals(receipient))
                {
                    r_id=i;
                    found=1;
                    break;
                }
            }

            if(found==1)
            {
                clients[r_id].send(users[findClient(ID)]+" (pm): "+message);
            }
            else
            {
               clients[findClient(ID)].send(receipient+" Not online\nYour message will be delivered as soon as your friend is online");
               st=con.createStatement();
               String query2="INSERT INTO pending_messages (sender,receiver,message) VALUES ('"+sender+"','"+receipient+"','"+message+"')";
               st.executeUpdate(query2);
               st.close();
            }
 
         }
         
     }
     else if(words[0].equals("show_my_friends"))
     {
         String myname=users[findClient(ID)];
         int c=0;
         st=con.createStatement();
         String query="select * from friends where freceiver='"+myname+"' or fsender='"+myname+"'";
         rs=st.executeQuery(query);
         clients[findClient(ID)].send("****** Your Friend list ******");
         while(rs.next())
         {   c+=1;
             String name1=rs.getString("freceiver");
             String name2=rs.getString("fsender");
             if(name1.equals(myname))
             {
                clients[findClient(ID)].send(c+". "+name2);
          
             }
             else
             {
                 clients[findClient(ID)].send(c+". "+name1);
             }
         }
               
     }
     else if (input.equals(".bye"))
      {  clients[findClient(ID)].send(".bye");
         remove(ID); 
      }
     else
     {
         int loginstatus=clients[findClient(ID)].getstatus();
         if(loginstatus==1)
         {
             for (int i = 0; i < clientCount; i++)
             {   
                 clients[i].send(users[findClient(ID)] + ": " + input);
             }
         }
         else
         {
             for (int i = 0; i < clientCount; i++)
             {   
                 clients[i].send(ID + ": " + input);
             }          
         }
           
     }
            
   }
   
   
   public synchronized void remove(int ID)
   {  int pos = findClient(ID);
      users[pos]=" ";
      if (pos >= 0)
      {  ChatServerThread toTerminate = clients[pos];
         System.out.println("Removing client thread " + ID + " at " + pos);
         if (pos < clientCount-1)
            for (int i = pos+1; i < clientCount; i++)
            {  clients[i-1] = clients[i];
               users[i-1]=users[i];
            }               
         clientCount--;
         try
         {  toTerminate.close(); }
         catch(IOException ioe)
         {  System.out.println("Error closing thread: " + ioe); }
         toTerminate.stop(); }
   }
   private void addThread(Socket socket)
   {  if (clientCount < clients.length)
      {  System.out.println("Client accepted: " + socket);
         clients[clientCount] = new ChatServerThread(this, socket);
         try
         {  clients[clientCount].open(); 
            clients[clientCount].start();  
            clientCount++; }
         catch(IOException ioe)
         {  System.out.println("Error opening thread: " + ioe); } }
      else
         System.out.println("Client refused: maximum " + clients.length + " reached.");
   }
   public static void main(String args[]) { TCPServer server = null;
         server = new TCPServer(2000); }
}