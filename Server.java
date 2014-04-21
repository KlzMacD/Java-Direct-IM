 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.io.EOFException;
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.net.InetAddress;
 import java.net.ServerSocket;
 import java.net.Socket;
 import javax.swing.JFrame;
 import javax.swing.JScrollPane;
 import javax.swing.JTextArea;
 import javax.swing.JTextField;
 import javax.swing.SwingUtilities;
 
 public class Server
   extends JFrame
 {
   private JTextField userText;
   private JTextArea chatWindow;
   private ObjectOutputStream output;
   private ObjectInputStream input;
   private ServerSocket server;
   private Socket connection;
   
   public Server()
   {
     super("Crypto Instant Messenger");
     
     this.userText = new JTextField();
     this.userText.setEditable(false);
     
     this.userText.addActionListener(new ActionListener()
     {
       public void actionPerformed(ActionEvent paramAnonymousActionEvent)
       {
         Server.this.sendMessage(paramAnonymousActionEvent.getActionCommand());
         Server.this.userText.setText("");
       }
     });
     add(this.userText, "North");
     
     this.chatWindow = new JTextArea();
     add(new JScrollPane(this.chatWindow));
     setSize(300, 150);
     setVisible(true);
   }
   
   public void startRunning()
   {
     try
     {
       this.server = new ServerSocket(6789, 100);
       for (;;)
       {
         try
         {
           waitForConnection();
           setupStreams();
           whileChatting();
         }
         catch (EOFException localEOFException)
         {
           showMessage("\n Server ended the incoming connection.");
         }
         finally
         {
           closeEverything();
         }
       }
     }
     catch (IOException localIOException)
     {
       localIOException.printStackTrace();
     }
   }
   
   private void waitForConnection()
     throws IOException
   {
     showMessage("Waiting for incoming connection. \n");
     this.connection = this.server.accept();
     showMessage("Now connected to " + this.connection.getInetAddress().getHostName());
   }
   
   private void setupStreams()
     throws IOException
   {
     this.output = new ObjectOutputStream(this.connection.getOutputStream());
     
     this.output.flush();
     this.input = new ObjectInputStream(this.connection.getInputStream());
     showMessage("\nStreams are now set up and ready for use! \n");
   }
   
   private void whileChatting()
     throws IOException
   {
     String str = " You are now connected! ";
     sendMessage(str);
     ableToType(true);
     do
     {
       try
       {
         str = (String)this.input.readObject();
         showMessage("\n" + str);
       }
       catch (ClassNotFoundException localClassNotFoundException)
       {
         showMessage("\nUnknown incoming data type!");
       }
     } while (!str.equals("CLIENT - END"));
   }
   
   private void closeEverything()
   {
     showMessage("\nClosing connection and sockets..\n");
     ableToType(false);
     try
     {
       this.output.close();
       this.input.close();
       this.connection.close();
     }
     catch (IOException localIOException)
     {
       localIOException.printStackTrace();
     }
   }
   
   private void sendMessage(String paramString)
   {
     try
     {
       this.output.writeObject("SERVER - " + paramString);
       this.output.flush();
       showMessage("\nSERVER - " + paramString);
     }
     catch (IOException localIOException)
     {
       this.chatWindow.append("\nError: Failed to send the message.");
     }
   }
   
   private void showMessage(final String paramString)
   {
     SwingUtilities.invokeLater(new Runnable()
     {
       public void run()
       {
         Server.this.chatWindow.append(paramString);
       }
     });
   }
   
   private void ableToType(final boolean paramBoolean)
   {
     SwingUtilities.invokeLater(new Runnable()
     {
       public void run()
       {
         Server.this.userText.setEditable(paramBoolean);
       }
     });
   }
 }