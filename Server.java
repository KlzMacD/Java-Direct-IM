/*   1:    */ import java.awt.event.ActionEvent;
/*   2:    */ import java.awt.event.ActionListener;
/*   3:    */ import java.io.EOFException;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.io.ObjectInputStream;
/*   6:    */ import java.io.ObjectOutputStream;
/*   7:    */ import java.net.InetAddress;
/*   8:    */ import java.net.ServerSocket;
/*   9:    */ import java.net.Socket;
/*  10:    */ import javax.swing.JFrame;
/*  11:    */ import javax.swing.JScrollPane;
/*  12:    */ import javax.swing.JTextArea;
/*  13:    */ import javax.swing.JTextField;
/*  14:    */ import javax.swing.SwingUtilities;
/*  15:    */ 
/*  16:    */ public class Server
/*  17:    */   extends JFrame
/*  18:    */ {
/*  19:    */   private JTextField userText;
/*  20:    */   private JTextArea chatWindow;
/*  21:    */   private ObjectOutputStream output;
/*  22:    */   private ObjectInputStream input;
/*  23:    */   private ServerSocket server;
/*  24:    */   private Socket connection;
/*  25:    */   
/*  26:    */   public Server()
/*  27:    */   {
/*  28: 25 */     super("Crypto Instant Messenger");
/*  29:    */     
/*  30: 27 */     this.userText = new JTextField();
/*  31: 28 */     this.userText.setEditable(false);
/*  32:    */     
/*  33: 30 */     this.userText.addActionListener(new ActionListener()
/*  34:    */     {
/*  35:    */       public void actionPerformed(ActionEvent paramAnonymousActionEvent)
/*  36:    */       {
/*  37: 34 */         Server.this.sendMessage(paramAnonymousActionEvent.getActionCommand());
/*  38: 35 */         Server.this.userText.setText("");
/*  39:    */       }
/*  40: 38 */     });
/*  41: 39 */     add(this.userText, "North");
/*  42:    */     
/*  43: 41 */     this.chatWindow = new JTextArea();
/*  44: 42 */     add(new JScrollPane(this.chatWindow));
/*  45: 43 */     setSize(300, 150);
/*  46: 44 */     setVisible(true);
/*  47:    */   }
/*  48:    */   
/*  49:    */   public void startRunning()
/*  50:    */   {
/*  51:    */     try
/*  52:    */     {
/*  53: 51 */       this.server = new ServerSocket(6789, 100);
/*  54:    */       for (;;)
/*  55:    */       {
/*  56:    */         try
/*  57:    */         {
/*  58: 55 */           waitForConnection();
/*  59: 56 */           setupStreams();
/*  60: 57 */           whileChatting();
/*  61:    */         }
/*  62:    */         catch (EOFException localEOFException)
/*  63:    */         {
/*  64: 59 */           showMessage("\n Server ended the incoming connection.");
/*  65:    */         }
/*  66:    */         finally
/*  67:    */         {
/*  68: 61 */           closeEverything();
/*  69:    */         }
/*  70:    */       }
/*  71:    */     }
/*  72:    */     catch (IOException localIOException)
/*  73:    */     {
/*  74: 65 */       localIOException.printStackTrace();
/*  75:    */     }
/*  76:    */   }
/*  77:    */   
/*  78:    */   private void waitForConnection()
/*  79:    */     throws IOException
/*  80:    */   {
/*  81: 71 */     showMessage("Waiting for incoming connection. \n");
/*  82: 72 */     this.connection = this.server.accept();
/*  83: 73 */     showMessage("Now connected to " + this.connection.getInetAddress().getHostName());
/*  84:    */   }
/*  85:    */   
/*  86:    */   private void setupStreams()
/*  87:    */     throws IOException
/*  88:    */   {
/*  89: 78 */     this.output = new ObjectOutputStream(this.connection.getOutputStream());
/*  90:    */     
/*  91: 80 */     this.output.flush();
/*  92: 81 */     this.input = new ObjectInputStream(this.connection.getInputStream());
/*  93: 82 */     showMessage("\nStreams are now set up and ready for use! \n");
/*  94:    */   }
/*  95:    */   
/*  96:    */   private void whileChatting()
/*  97:    */     throws IOException
/*  98:    */   {
/*  99: 87 */     String str = " You are now connected! ";
/* 100: 88 */     sendMessage(str);
/* 101: 89 */     ableToType(true);
/* 102:    */     do
/* 103:    */     {
/* 104:    */       try
/* 105:    */       {
/* 106: 93 */         str = (String)this.input.readObject();
/* 107: 94 */         showMessage("\n" + str);
/* 108:    */       }
/* 109:    */       catch (ClassNotFoundException localClassNotFoundException)
/* 110:    */       {
/* 111: 96 */         showMessage("\nUnknown incoming data type!");
/* 112:    */       }
/* 113: 98 */     } while (!str.equals("CLIENT - END"));
/* 114:    */   }
/* 115:    */   
/* 116:    */   private void closeEverything()
/* 117:    */   {
/* 118:103 */     showMessage("\nClosing connection and sockets..\n");
/* 119:104 */     ableToType(false);
/* 120:    */     try
/* 121:    */     {
/* 122:106 */       this.output.close();
/* 123:107 */       this.input.close();
/* 124:108 */       this.connection.close();
/* 125:    */     }
/* 126:    */     catch (IOException localIOException)
/* 127:    */     {
/* 128:110 */       localIOException.printStackTrace();
/* 129:    */     }
/* 130:    */   }
/* 131:    */   
/* 132:    */   private void sendMessage(String paramString)
/* 133:    */   {
/* 134:    */     try
/* 135:    */     {
/* 136:118 */       this.output.writeObject("SERVER - " + paramString);
/* 137:119 */       this.output.flush();
/* 138:120 */       showMessage("\nSERVER - " + paramString);
/* 139:    */     }
/* 140:    */     catch (IOException localIOException)
/* 141:    */     {
/* 142:122 */       this.chatWindow.append("\nError: Failed to send the message.");
/* 143:    */     }
/* 144:    */   }
/* 145:    */   
/* 146:    */   private void showMessage(final String paramString)
/* 147:    */   {
/* 148:128 */     SwingUtilities.invokeLater(new Runnable()
/* 149:    */     {
/* 150:    */       public void run()
/* 151:    */       {
/* 152:131 */         Server.this.chatWindow.append(paramString);
/* 153:    */       }
/* 154:    */     });
/* 155:    */   }
/* 156:    */   
/* 157:    */   private void ableToType(final boolean paramBoolean)
/* 158:    */   {
/* 159:140 */     SwingUtilities.invokeLater(new Runnable()
/* 160:    */     {
/* 161:    */       public void run()
/* 162:    */       {
/* 163:143 */         Server.this.userText.setEditable(paramBoolean);
/* 164:    */       }
/* 165:    */     });
/* 166:    */   }
/* 167:    */ }


/* Location:           C:\Users\Mac\Drive\WEBDEV FINAL\Java Direct IM\
 * Qualified Name:     Server
 * JD-Core Version:    0.7.0.1
 */