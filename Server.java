/*
SHAKTHI PRAKASH THIRUVATHILINGAM
1001518112

References: 
https://www.geeksforgeeks.org/introducing-threads-socket-programming-java/
https://www.geeksforgeeks.org/multi-threaded-chat-application-set-1/
*/


import java.io.*; 
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class Server {
	
	public static ServerSocket ss;
	public static Socket s;
	public static DataInputStream dis;
	public static DataOutputStream dos;
	
	public static List<String> client_list  = new ArrayList<String>();
	public static JFrame frame = new JFrame("Server");
	public static JPanel outerpanel;
	public static JPanel panel1 = new JPanel();
	public static JPanel panel2 = new JPanel();
	public static JTextArea serv_out = new JTextArea();
	public static JScrollPane scrollPane = new JScrollPane(serv_out);
	public static JButton start_server = new JButton("Start Server");
	public static JButton stop_server = new JButton("Stop Server");
	public static JButton Client_list_but = new JButton("Display Clients");
	public static File server_folder = new File("D:/5306/server");
	public static Vector<ClientHandler> ar = new Vector<>();
	public static int  client_count = 0;
	public static int client_check = 0;
	public static Thread t;
	public static int serverstopkey=0;
	
	
	//constructor
	public Server()
	{
		intializecomponents();
	}
	
/* code Reference : https://www.tutorialspoint.com/swing/swing_gridbaglayout.htm
		    https://www.guru99.com/java-swing-gui.html */
// function to initialize the GUI components
	public void intializecomponents()
	{
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 1000);
		GridLayout grid = new GridLayout(2,1);
		outerpanel = new JPanel(grid);
		outerpanel.setBounds(0, 0, 1000, 1000);
		GridBagLayout panel1layout = new GridBagLayout();
		panel1.setLayout(panel1layout);
		GridBagConstraints gbc = new GridBagConstraints();
		panel1.setBounds(0, 0, 900,300 );
		gbc.insets = new Insets(2,2,2,2);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel1.add(start_server,gbc);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = 0;
		panel1.add(stop_server,gbc);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 2;
		gbc.gridy = 0;
		panel1.add(Client_list_but,gbc);

		panel2.setBounds(0, 400, 900, 600);
		outerpanel.add(panel1);
		outerpanel.add(panel2);
		serv_out.setRows(18);
		serv_out.setColumns(60);
		serv_out.setWrapStyleWord(true);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setPreferredSize(new Dimension(500,200));
		panel2.add(scrollPane);
		frame.add(outerpanel);
		frame.setVisible(true);	
        
        /* code reference : https://stackoverflow.com/questions/21879243/how-to-create-on-click-event-for-buttons-in-swing */
        //function to find the list of clients connected to the server
        Client_list_but.addActionListener(new ActionListener(){
			  public void actionPerformed(ActionEvent e)
			   {
				  String conn_clients = "Connect Clients : ";
				  for (ClientHandler mc : Server.ar)
				  {
					  if(mc.isactive == true)
						  conn_clients = conn_clients +" "+ mc.username;
				  }
				  
				  if(serverstopkey==0)
					  serv_out.append("\n" + conn_clients );
			   	  }
			  });
        
        //function to stop the server and disconnect the server successfully
        stop_server.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			   {
				  serverstopkey = 1;
				  serv_out.append("\n Stopping the server");
				  String stop_message = "serverstop";
				  
				  for (ClientHandler mc : Server.ar){ 	  	
	                    		try {
						if(mc.isactive){
						mc.dos.writeUTF(stop_message);
						mc.setBreakKey(); //key used to break the thread running for each client
									}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}  
				try {
					//closing the input and output stream of the server
				    dis.close();
				    dos.close();
				} 
				catch (IOException e1) {
				    e1.printStackTrace();
				}
				serv_out.append("\n Disconnected all the client.\n Server Stopped Successfully");
			   }
		});
}
/* code reference : https://dzone.com/articles/multi-threading-java-swing 
Running the Server GUI in a separate thread */
	public static void main(String[] args) throws IOException  
    { 
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	new Server();

            }
        });
		//starting the server
		start_server.addActionListener(new ActionListener(){
			  public void actionPerformed(ActionEvent e)
			   {
			    serv_out.append("Server Started"); 
			    if(!server_folder.exists()){
			    	server_folder.mkdir();
			    	File newtxt = new File("D:/5306/server/test.txt");
			    	try {
						newtxt.createNewFile();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
			    }
			    serv_out.append("\n A new folder server to keep the files uploaded by the clients is created \n in the path D:/5306/ ");  
			   
			   }
			  });
      
		//creating the server socket with port number 12345
		 ss = new ServerSocket(12345); 
		 s = null;
        // running infinite loop for getting 
        // client request 
		
        while (true)  
        {   
            try 
            { 
                // socket object to receive incoming client requests 
                s = ss.accept();  
                // obtaining input and out streams 
                dis = new DataInputStream(s.getInputStream()); 
                dos = new DataOutputStream(s.getOutputStream()); 
                
                String in_coming_client = dis.readUTF().toString();
                //handling duplicate entries
                for (ClientHandler mc : Server.ar) 
                {
					// checking if the new client name is matching the old client name and the client is active
                	if(mc.username.equals(in_coming_client) && mc.isactive == true)
                		client_check = 1;
                	// checking if the new client name is matching the old client name and the client is inactive
					else if (mc.username.equals(in_coming_client) && mc.isactive == false)
                		client_check = 2;
                	else
                		client_check = 0;
                }
                
				//handling the client connections based on the above flags
                if(client_check == 1)
                {
                	serv_out.append("\n A client with the same name found!! Cannot Connect");
                	dos.writeUTF("found");;
                }
                else if(client_check == 2)
                {
                	serv_out.append("\n A new client " +in_coming_client+ " is connected");  
                    serv_out.append("\n Assigning new thread for this client");
                    dos.writeUTF("Connected to the Server !! Welcome message from Server");
                    for (ClientHandler mc : Server.ar)
                    {
                    	if(mc.username.equals(in_coming_client) && mc.isactive == false)
                    		mc.isactive = true;
                    }
                }
                else
                {
           		client_list.add(in_coming_client);
                	serv_out.append("\n A new client " +in_coming_client+ " is connected");  
                    	serv_out.append("\n Assigning new thread for this client");
                    	dos.writeUTF("Connected to the Server !! Welcome message from Server");
                    	ClientHandler mtch = new ClientHandler(s, dis, dos,in_coming_client,serv_out);
                    	ar.add(mtch);
                    	// create a new thread object 
                     	t = new Thread(mtch);  
                    	// Invoking the start() method 
                    	t.start();
                    	client_count++;
		}  
            } 
            catch (Exception e){ 
                s.close(); 
                e.printStackTrace(); 
            } 
        }
    }
} 
  
// ClientHandler class 
class ClientHandler implements Runnable  
{ 
    final DataInputStream dis; 
    final DataOutputStream dos; 
    final Socket s; 
    boolean isactive;
    final String username;
    final JTextArea serv_out;
    int breakkey;
    static String received;
    static int count=0;
    Timer timer;
    static String currentuser;
      
  
    // Constructor 
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos,String username,JTextArea serv_out)  
    { 
        this.s = s; 
        this.dis = dis; 
        this.dos = dos;
        this.isactive = true;
        this.username = username;
        this.serv_out = serv_out ;
        this.breakkey = 0;
    }
    
    public void setBreakKey()
    {
    	this.breakkey = 1;
    }
    
    @Override
    public void run()  
    {   
        while (true)  
        { 
            try { 
            	//checking if the break key is set to 1 , if it is set to 1 the server stops
            	if(this.breakkey == 1){
            		dis.close();
            		dos.close();
            		//s.close();
            		break;
            	}
            	
      
                // receive the answer from client 
            	received = dis.readUTF().toString(); 
            	
            	if(received.equals("Yes")) 
                {  
                   count = count+1; //increases the vote count if yes from a client
                } 
            	//client disconnect request by sending "exit" keyword as message
                if(received.equals("Exit")) 
                {  
                    this.isactive = false; 
                    serv_out.append("\n The client "+this.username+" got disconnected");
                    //this.s.close(); 
                    //break;
                } 
                
				/* code references : https://www.geeksforgeeks.org/moving-file-one-directory-another-using-java/
									 https://www.baeldung.com/java-copy-file */
                
                if((received.length() >= 8) && (received.substring(0, 8).equals("Deletion"))) //client has informed the server about the deletion.
                {
                	currentuser = received.substring(9);
                	serv_out.append("\n "+currentuser+" has requested to delete the test.txt. The Server has\n asked other clients for vote");
                	for (ClientHandler cl : Server.ar)
                	{
						//copying the files to all the clients shared directory
                		if(cl.isactive == true && !(cl.username.equals(currentuser)))
                		{
                			
    						cl.dos.writeUTF("Vote");	// send out the election message to the other clients
                			
                		}
                	
                		
                	}
                	//https://stackoverflow.com/questions/4044726/how-to-set-a-timer-in-java
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                    	  @Override
                    	  public void run() {
                    		if(count >= 2)// checks for the vote count 
					{
					serv_out.append("\n All clients has accepted to delete the file");
					for (ClientHandler cl : Server.ar)
					{
						try {
							cl.dos.writeUTF("Delete");
							count = 0; // resetting the vote count
						} 
						catch (IOException e) {
							e.printStackTrace();
						}
					}
                              }
                    	      else{
                    	 		serv_out.append("\n All clients has not accepted to delete the file");
                    			 for (ClientHandler cl : Server.ar)
                                	{
                                		try {
							cl.dos.writeUTF("NODelete");
							count = 0; // resetting the vote count
							if(cl.username.equals(currentuser)){
								cl.dos.writeUTF("restore");// if the vote fails, sends the restore message to the client
								Files.copy 
								(Paths.get("D:\\5306\\server\\test.txt"),  
							     Paths.get("D:\\5306\\"+cl.username+"\\test.txt"),StandardCopyOption.REPLACE_EXISTING);
								}
  						} 
						catch (IOException e) 
						{
  							e.printStackTrace();
  						}
                                	}
                    	  	}
                    }
                 }, 3000);// timer for three seconds
                    
                }
             
            } catch (IOException e) { 

           } 
        } 
    } 
} 

