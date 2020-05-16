/* SHAKTHI PRAKASH THIRUVATHILINGAM
1001518112

https://www.geeksforgeeks.org/introducing-threads-socket-programming-java/
https://www.geeksforgeeks.org/multi-threaded-chat-application-set-1/
*/

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*; 
import java.net.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

public class Client {
	
	static DataInputStream dis;
    static DataOutputStream dos;
    static InetAddress ip;
    static Socket s;
	
	
	public static JFrame frame = new JFrame("Client");
	public static JPanel outerpanel;
	public static JPanel panel1;
	public static JPanel panel2 = new JPanel();
	public static JPanel panel3;
	public static JTextArea client_out = new JTextArea();
	public static JScrollPane scrollPane = new JScrollPane(client_out);
	public static JButton connect= new JButton("Connect");
	public static JTextField client_name = new JTextField();
	public static JLabel client_label = new JLabel("Client Name");
	public static String read_client_name;
	public static int folder_count = -1;
	public static JButton upload= new JButton("Upload File");
	public static JButton display_files= new JButton("Display Files");
	public static JButton disconnect= new JButton("Disconnect");
	public static JButton yes = new JButton("Yes");
	public static JButton No = new JButton("No");
	public static File dis_file;
	public static File serv_dir;
	public static String latest_file;
	public static DirectoryWatchService dws;
	public static String latestfilename;
	public static int dup_key=0;
	
	//constructor
	public Client()
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
        GridLayout gridpanel1 = new GridLayout(1,2);
        outerpanel = new JPanel(grid);
        outerpanel.setBounds(0, 0, 1000, 1000);
        panel1 = new JPanel(gridpanel1);
        panel1.setBounds(0, 0, 900,300 );
        GridBagLayout panel3layout = new GridBagLayout();
        panel3 = new JPanel();
        panel3.setLayout(panel3layout);
        panel3.setBounds(0, 0, 900, 300);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel3.add(client_label,gbc);
        
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 0;
        client_name.setPreferredSize(new Dimension(200,40));
        panel3.add(client_name,gbc);
        
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 2;
        gbc.gridy = 0;
        panel3.add(connect,gbc);
        
        
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel3.add(display_files,gbc);
      
        
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel3.add(disconnect,gbc);
        
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel3.add(yes,gbc);
        
        
        panel1.add(panel3);
        client_out.setRows(18);
        client_out.setColumns(60);
        client_out.setWrapStyleWord(true);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(500,200));
        panel2.add(scrollPane);
        outerpanel.add(panel1);
        outerpanel.add(panel2);
        frame.add(outerpanel);
        frame.setVisible(true);

        
/* code reference : https://stackoverflow.com/questions/21879243/how-to-create-on-click-event-for-buttons-in-swing */
		connect.addActionListener(new ActionListener(){
			  public void actionPerformed(ActionEvent e)
			   {
				       
			   try {
				   
				   
				   //getting the user name from the client
			       read_client_name = client_name.getText().toString();
			       if(read_client_name.equals(""))
			    	   client_out.append("\n Please enter a client name to connect");
			       else
			    	   connect(read_client_name);// calling the connect function
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			   
			   }
		});
		
		// function to display the files in the client's directory
		display_files.addActionListener(new ActionListener(){
			  public void actionPerformed(ActionEvent e)
			   {
				  	   
				       dis_file = new File("D:/5306/"+read_client_name);
				       if(!dis_file.exists())
				       {
				    	   client_out.append("\n Please connect to the Server");
				       }
				       else if(dis_file.list().length == 0)
				    	   client_out.append("\n No files found in the shared directory");
				       else
				       {	
				    	   String flist = "Files : ";
				    	   String files_list[] = dis_file.list(); //gets all the files from the directory
				    	   for(int j=0; j< files_list.length;j++)
				    		   flist = flist + " " + files_list[j];
				    	   client_out.append("\n"+ flist);
				       }
				      
			   
			   }
			  });
		
		// function to disconnect the client from server
		disconnect.addActionListener(new ActionListener(){
			  public void actionPerformed(ActionEvent e)
			   {
				  	   
				 try {
					dos.writeUTF("Exit"); // sending exit message to the server
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				 
				client_out.append("\n Disconnected Succesfully from the server");
				      
			   }
			  });
		
		yes.addActionListener(new ActionListener(){
			  public void actionPerformed(ActionEvent e)
			   {
				  	   
				 try {
					dos.writeUTF("Yes"); // sending exit message to the server
				} catch (IOException e1) {
					e1.printStackTrace();
				}     
			   }
			  });
		
		
		
	}
	/* code reference: https://www.mkyong.com/java/java-generate-random-integers-in-a-range/ */
	public static int getRandomNumberInRange(int min, int max) {

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		return (int)(Math.random() * ((max - min) + 1)) + min;
	}
	
/* code reference : https://www3.ntu.edu.sg/home/ehchua/programming/java/J5e_multithreading.html */
	public static void connect(String username) throws Exception
	{
		System.out.println(username);
		 ip = InetAddress.getByName("localhost"); 
	      
        // establish the connection with server port 12345 
       s = new Socket(ip,12345); 
  
        // obtaining input and out streams 
         dis = new DataInputStream(s.getInputStream()); 
         dos = new DataOutputStream(s.getOutputStream()); 
         
        dos.writeUTF(username);
        
	        Thread runClient = new Thread(new Runnable()
			{ 
			public void run()
			{
		         while (true)  
		            { 
		        	 try
		        	 {
						 //read the messages from the server
		        		 String msg = dis.readUTF().toString();
						 //client is notified for duplicate names in the server
		        		 if(msg.equals("found"))
		        		 {
		        			 client_out.append("\n There is a already a client connected with the same name please use different client name to connect \n");
		        		 }
						 //message to notify that the server stopped.
		        		 else if(msg.equals("serverstop"))
		        		 {
		        			 client_out.append("\n Server Stopped....Disconnected");
		        			 break;
		        			
		        		 }
		        		 else if(msg.equals("Vote"))// Check the messages for Election from server
		        		 {
		        			 
		        			 client_out.append("\n A client has requested for deletion of a file.");
		        			 /*https://www.mkyong.com/java/java-generate-random-integers-in-a-range/ */
		        			 int randomKey = getRandomNumberInRange(1, 10); // generates a random number from 1 to 10
		        			 System.out.println("\n"+randomKey);
		        			 if(randomKey %2 == 0){
		        				 dos.writeUTF("Yes");
		        				 client_out.append("\n The client voted Yes");
		        			 }
		        		 }
		        		 else if(msg.equals("Delete"))
		        		 {
		        			 client_out.append("\n The file is deleted from the repository");
		        			 File delfile = new File("D:\\5306\\"+read_client_name+"\\test.txt");
		        			 System.out.println("\n D:\\5306\\"+read_client_name+"\\test.txt");
		        			 if(delfile.delete())
		        				 System.out.println("\n the file is deleted");
		        			 dup_key=1;
		        		 }
		        		 else if(msg.equals("NODelete"))// checks for message to abort the operation
		        		 {
		        			 dup_key=0;
		        			 client_out.append("\n All clients has not accepted to delete hence abort the operation");
		        		 }
		        		 else if(msg.equals("restore"))// restore the file from server if the operation is aborted
		        		 {
		        			 dup_key=0;
		        			 client_out.append("\n The deleted file test.txt has been restored");
		        		 }
		        		 else if(msg.equals("updatekey"))
		        			 dup_key=0;
		        		 else{
		        			 folder_count = folder_count +1;
		        			 client_out.append("\n"+msg); 
		        			 
		        			 // creating a shared folder for the first time
		        			 if(folder_count == 0)
		        			 {
		        				 File client_fold = new File("D:/5306/"+read_client_name);
		        				 if(!client_fold.exists()){
		        					 client_fold.mkdir();
		        					File testfile = new File("D:/5306/"+read_client_name+"/test.txt");
		        					testfile.createNewFile();
		        				 }
		        				 client_out.append("\n A shared directory designated to Client "+read_client_name + " is created");
		        				 
		        				//running directory watch service in a separate thread
		        		          dws = new DirectoryWatchService(username,client_out);
		        		          Thread d = new Thread(dws);
		        		          d.start();
		        			 
		        			 }
		        			 
		        			
		        		 }
		        		 
		        		 
		            } 
		              
		            catch(Exception e){ 
		            e.printStackTrace(); 
		        }
		       }
			}
			});
	        runClient.start();
        
	}
	/* code reference : https://dzone.com/articles/multi-threading-java-swing 
	Running the Client GUI in a separate thread */
	public static void main(String[] args) throws IOException  
    { 
		
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	new Client();

            }
        });
		
		
         
    } 
}

/* code reference : https://www.baeldung.com/java-nio2-watchservice */
class DirectoryWatchService implements Runnable
{
	
	public static WatchService watchService;
	public static Path path;
	public static WatchKey key;
	final JTextArea client_out;
	final String client_name;
	static String latest_file;
	
	public DirectoryWatchService(String client_name,JTextArea client_out) throws Exception
	{
		this.client_name = client_name;
		this.client_out = client_out;
		watchService = FileSystems.getDefault().newWatchService();
        path = Paths.get("D:\\5306\\"+this.client_name);
        
        path.register(
       	        watchService, 
       	          StandardWatchEventKinds.ENTRY_CREATE, 
       	            StandardWatchEventKinds.ENTRY_DELETE, 
       	              StandardWatchEventKinds.ENTRY_MODIFY);
		
	}
	
	public String readFileName()
	{
		return latest_file;
		
	}
	
	public void run()
	{
		try {
			//Actively look for events in the directory
			while ((key = watchService.take()) != null) {
			    for (WatchEvent<?> event : key.pollEvents()) {
			    	
			    	if(event.kind().toString().equals("ENTRY_DELETE")&& Client.dup_key==0)// checking for delete event in the client directory
			    	{
			    		client_out.append("\n The Client has deleted the test.txt file and it has requested for a election to the server");
			    		//latest_file = event.context().toString();//get the new file name
						Client.dos.writeUTF("Deletion:"+this.client_name); // send the server invalid notice
						System.out.println(" Yes");
			    	}
			    	if(event.kind().toString().equals("ENTRY_CREATE"))
			    		Client.dup_key = 0;
			    }
			    key.reset();
			}
		} catch (InterruptedException | IOException e) {

			e.printStackTrace();
		}
    }
		
}


