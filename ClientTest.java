 public class ClientTest
 {
   public static void main(String[] paramArrayOfString)
   {
     Client localClient = new Client("127.0.0.1");
     localClient.setDefaultCloseOperation(3);
     localClient.startRunningClient();
   }
 }