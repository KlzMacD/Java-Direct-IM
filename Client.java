 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.io.EOFException;
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.net.InetAddress;
 import java.net.Socket;
 import javax.swing.JFrame;
 import javax.swing.JScrollPane;
 import javax.swing.JTextArea;
 import javax.swing.JTextField;
 import javax.swing.SwingUtilities;
 
 public class Client
   extends JFrame
 {
   private JTextField userText;
   private JTextArea chatWindow;
   private ObjectOutputStream output;
   private ObjectInputStream input;
   private String message = "";
   private String serverIP;
   private Socket connection;
   
   public Client(String paramString)
   {
     super("Crypto Instant Messenger");
     this.serverIP = paramString;
     this.userText = new JTextField();
     this.userText.setEditable(false);
     this.userText.addActionListener(new ActionListener()
     {
       public void actionPerformed(ActionEvent paramAnonymousActionEvent)
       {
         Client.this.sendMessage(paramAnonymousActionEvent.getActionCommand());
         Client.this.userText.setText("");
       }
     });
     add(this.userText, "North");
     this.chatWindow = new JTextArea();
     add(new JScrollPane(this.chatWindow), "Center");
     setSize(300, 150);
     setVisible(true);
   }
   
   public void startRunningClient()
   {
     try
     {
       connectToServer();
       setupStreams();
       whileChatting();
     }
     catch (EOFException localEOFException)
     {
       showMessage("\nClient terminated the connection.");
     }
     catch (IOException localIOException)
     {
       localIOException.printStackTrace();
     }
     finally
     {
       closeEverything();
     }
   }
   
   private void connectToServer()
     throws IOException
   {
     showMessage("Attempting to connect to server: " + this.serverIP + "\n");
     this.connection = new Socket(InetAddress.getBy(this.serverIP), 6789);
     showMessage("Connected to: " + this.connection.getInetAddress().getHost());
   }
   
   private void setupStreams()
     throws IOException
   {
     this.output = new ObjectOutputStream(this.connection.getOutputStream());
     this.output.flush();
     this.input = new ObjectInputStream(this.connection.getInputStream());
     showMessage("\nYour streams are up and running.\n");
   }
   
   private void whileChatting()
     throws IOException
   {
     ableToType(true);
     do
     {
       try
       {
         this.message = ((String)this.input.readObject());
         showMessage("\n" + this.message);
       }
       catch (ClassNotFoundException localClassNotFoundException)
       {
         showMessage("\nUnknown incoming data type!");
       }
     } while (!this.message.equals("SERVER - END"));
   }
   
   private void closeEverything()
   {
     showMessage("\nClosing connection and sockets..");
     ableToType(false);
     try
     {
       this.input.close();
       this.output.close();
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
       this.output.writeObject("CLIENT - " + paramString);
       this.output.flush();
       showMessage("\nCLIENT - " + paramString);
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
         Client.this.chatWindow.append(paramString);
       }
     });
   }
   
   private void ableToType(final boolean paramBoolean)
   {
     SwingUtilities.invokeLater(new Runnable()
     {
       public void run()
       {
         Client.this.userText.setEditable(paramBoolean);
       }
     });
   }
 }