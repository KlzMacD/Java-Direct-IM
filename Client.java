name import java.awt.event.ActionEvent;
name import java.awt.event.ActionListener;
name import java.io.EOFException;
name import java.io.IOException;
name import java.io.ObjectInputStream;
name import java.io.ObjectOutputStream;
name import java.net.InetAddress;
name import java.net.Socket;
name import javax.swing.JFrame;
name import javax.swing.JScrollPane;
name import javax.swing.JTextArea;
name import javax.swing.JTextField;
name import javax.swing.SwingUtilities;
name 
name public class Client
name   extends JFrame
name {
name   private JTextField userText;
name   private JTextArea chatWindow;
name   private ObjectOutputStream output;
name   private ObjectInputStream input;
   private String message = "";
name   private String serverIP;
name   private Socket connection;
name   
name   public Client(String paramString)
name   {
     super("Crypto Instant Messenger");
     this.serverIP = paramString;
     this.userText = new JTextField();
     this.userText.setEditable(false);
     this.userText.addActionListener(new ActionListener()
name     {
name       public void actionPerformed(ActionEvent paramAnonymousActionEvent)
name       {
         Client.this.sendMessage(paramAnonymousActionEvent.getActionCommand());
         Client.this.userText.setText("");
name       }
     });
     add(this.userText, "North");
     this.chatWindow = new JTextArea();
     add(new JScrollPane(this.chatWindow), "Center");
     setSize(300, 150);
     setVisible(true);
name   }
name   
name   public void startRunningClient()
name   {
name     try
name     {
       connectToServer();
       setupStreams();
       whileChatting();
name     }
name     catch (EOFException localEOFException)
name     {
       showMessage("\nClient terminated the connection.");
name     }
name     catch (IOException localIOException)
name     {
       localIOException.printStackTrace();
name     }
name     finally
name     {
       closeEverything();
name     }
name   }
name   
name   private void connectToServer()
name     throws IOException
name   {
     showMessage("Attempting to connect to server: " + this.serverIP + "\n");
     this.connection = new Socket(InetAddress.getByName(this.serverIP), 6789);
     showMessage("Connected to: " + this.connection.getInetAddress().getHostName());
name   }
name   
name   private void setupStreams()
name     throws IOException
name   {
     this.output = new ObjectOutputStream(this.connection.getOutputStream());
     this.output.flush();
     this.input = new ObjectInputStream(this.connection.getInputStream());
     showMessage("\nYour streams are up and running.\n");
name   }
name   
name   private void whileChatting()
name     throws IOException
name   {
     ableToType(true);
name     do
name     {
name       try
name       {
         this.message = ((String)this.input.readObject());
         showMessage("\n" + this.message);
name       }
name       catch (ClassNotFoundException localClassNotFoundException)
name       {
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