/*   1:    */ import java.awt.event.ActionEvent;
/*   2:    */ import java.awt.event.ActionListener;
/*   3:    */ import java.io.EOFException;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.io.ObjectInputStream;
/*   6:    */ import java.io.ObjectOutputStream;
/*   7:    */ import java.net.InetAddress;
/*   8:    */ import java.net.Socket;
/*   9:    */ import javax.swing.JFrame;
/*  10:    */ import javax.swing.JScrollPane;
/*  11:    */ import javax.swing.JTextArea;
/*  12:    */ import javax.swing.JTextField;
/*  13:    */ import javax.swing.SwingUtilities;
/*  14:    */ 
/*  15:    */ public class Client
/*  16:    */   extends JFrame
/*  17:    */ {
/*  18:    */   private JTextField userText;
/*  19:    */   private JTextArea chatWindow;
/*  20:    */   private ObjectOutputStream output;
/*  21:    */   private ObjectInputStream input;
/*  22: 14 */   private String message = "";
/*  23:    */   private String serverIP;
/*  24:    */   private Socket connection;
/*  25:    */   
/*  26:    */   public Client(String paramString)
/*  27:    */   {
/*  28: 20 */     super("Crypto Instant Messenger");
/*  29: 21 */     this.serverIP = paramString;
/*  30: 22 */     this.userText = new JTextField();
/*  31: 23 */     this.userText.setEditable(false);
/*  32: 24 */     this.userText.addActionListener(new ActionListener()
/*  33:    */     {
/*  34:    */       public void actionPerformed(ActionEvent paramAnonymousActionEvent)
/*  35:    */       {
/*  36: 27 */         Client.this.sendMessage(paramAnonymousActionEvent.getActionCommand());
/*  37: 28 */         Client.this.userText.setText("");
/*  38:    */       }
/*  39: 31 */     });
/*  40: 32 */     add(this.userText, "North");
/*  41: 33 */     this.chatWindow = new JTextArea();
/*  42: 34 */     add(new JScrollPane(this.chatWindow), "Center");
/*  43: 35 */     setSize(300, 150);
/*  44: 36 */     setVisible(true);
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void startRunningClient()
/*  48:    */   {
/*  49:    */     try
/*  50:    */     {
/*  51: 42 */       connectToServer();
/*  52: 43 */       setupStreams();
/*  53: 44 */       whileChatting();
/*  54:    */     }
/*  55:    */     catch (EOFException localEOFException)
/*  56:    */     {
/*  57: 46 */       showMessage("\nClient terminated the connection.");
/*  58:    */     }
/*  59:    */     catch (IOException localIOException)
/*  60:    */     {
/*  61: 48 */       localIOException.printStackTrace();
/*  62:    */     }
/*  63:    */     finally
/*  64:    */     {
/*  65: 50 */       closeEverything();
/*  66:    */     }
/*  67:    */   }
/*  68:    */   
/*  69:    */   private void connectToServer()
/*  70:    */     throws IOException
/*  71:    */   {
/*  72: 56 */     showMessage("Attempting to connect to server: " + this.serverIP + "\n");
/*  73: 57 */     this.connection = new Socket(InetAddress.getByName(this.serverIP), 6789);
/*  74: 58 */     showMessage("Connected to: " + this.connection.getInetAddress().getHostName());
/*  75:    */   }
/*  76:    */   
/*  77:    */   private void setupStreams()
/*  78:    */     throws IOException
/*  79:    */   {
/*  80: 63 */     this.output = new ObjectOutputStream(this.connection.getOutputStream());
/*  81: 64 */     this.output.flush();
/*  82: 65 */     this.input = new ObjectInputStream(this.connection.getInputStream());
/*  83: 66 */     showMessage("\nYour streams are up and running.\n");
/*  84:    */   }
/*  85:    */   
/*  86:    */   private void whileChatting()
/*  87:    */     throws IOException
/*  88:    */   {
/*  89: 71 */     ableToType(true);
/*  90:    */     do
/*  91:    */     {
/*  92:    */       try
/*  93:    */       {
/*  94: 74 */         this.message = ((String)this.input.readObject());
/*  95: 75 */         showMessage("\n" + this.message);
/*  96:    */       }
/*  97:    */       catch (ClassNotFoundException localClassNotFoundException)
/*  98:    */       {
/*  99: 77 */         showMessage("\nUnknown incoming data type!");
/* 100:    */       }
/* 101: 79 */     } while (!this.message.equals("SERVER - END"));
/* 102:    */   }
/* 103:    */   
/* 104:    */   private void closeEverything()
/* 105:    */   {
/* 106: 84 */     showMessage("\nClosing connection and sockets..");
/* 107: 85 */     ableToType(false);
/* 108:    */     try
/* 109:    */     {
/* 110: 87 */       this.input.close();
/* 111: 88 */       this.output.close();
/* 112: 89 */       this.connection.close();
/* 113:    */     }
/* 114:    */     catch (IOException localIOException)
/* 115:    */     {
/* 116: 91 */       localIOException.printStackTrace();
/* 117:    */     }
/* 118:    */   }
/* 119:    */   
/* 120:    */   private void sendMessage(String paramString)
/* 121:    */   {
/* 122:    */     try
/* 123:    */     {
/* 124: 98 */       this.output.writeObject("CLIENT - " + paramString);
/* 125: 99 */       this.output.flush();
/* 126:100 */       showMessage("\nCLIENT - " + paramString);
/* 127:    */     }
/* 128:    */     catch (IOException localIOException)
/* 129:    */     {
/* 130:102 */       this.chatWindow.append("\nError: Failed to send the message.");
/* 131:    */     }
/* 132:    */   }
/* 133:    */   
/* 134:    */   private void showMessage(final String paramString)
/* 135:    */   {
/* 136:108 */     SwingUtilities.invokeLater(new Runnable()
/* 137:    */     {
/* 138:    */       public void run()
/* 139:    */       {
/* 140:111 */         Client.this.chatWindow.append(paramString);
/* 141:    */       }
/* 142:    */     });
/* 143:    */   }
/* 144:    */   
/* 145:    */   private void ableToType(final boolean paramBoolean)
/* 146:    */   {
/* 147:120 */     SwingUtilities.invokeLater(new Runnable()
/* 148:    */     {
/* 149:    */       public void run()
/* 150:    */       {
/* 151:123 */         Client.this.userText.setEditable(paramBoolean);
/* 152:    */       }
/* 153:    */     });
/* 154:    */   }
/* 155:    */ }


/* Location:           C:\Users\Mac\Drive\WEBDEV FINAL\Java Direct IM\
 * Qualified Name:     Client
 * JD-Core Version:    0.7.0.1
 */