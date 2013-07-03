package VirtualClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class VirtualClient {
	
	
	
	
	public static void main(String[] args) {
		ArrayList<ClientThread> list = new ArrayList<ClientThread>();
		
		for(int i=0;i<10;i++)
		{
			VirtualClient virtualClient = new VirtualClient();
			ClientThread clientThread = virtualClient.new ClientThread(i);
			list.add(clientThread);
			System.out.println("start "+ i );
		}
	}
	
	
	
	
	class ClientThread 
	{
		private boolean ThreadFlag=true;
		private boolean EndFlag=true;
		private String end_tag_String="<end>";
		private Socket socket=null;
		private int num=0;
		private static final String HOST = "192.168.1.223";
	    private static final int PORT = 2000; 
		
		public ClientThread(int i) {
			// TODO Auto-generated constructor stub
			new Thread(new SendThread()).start();
			new Thread(new ReceiveThread()).start();
			num=i;
		}
		private byte [] GetPicForByte()
		{
			ByteArrayOutputStream baos= new ByteArrayOutputStream();
			try {
				FileInputStream fis= new FileInputStream(new File("C:\\test\\"+num+".jpg"));
				
				byte [] bytes = new byte [1024];
				int b;
				while(true)
				{
					if(fis.available()<1024)
					{
						while((b=fis.read())!=-1)
						{
							baos.write(b);
						}
						break;
					}
					else {
						fis.read(bytes);
						baos.write(bytes);
					}
				}
				fis.close();
				baos.close();
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return baos.toByteArray();
			
		}

		class SendThread implements Runnable
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(ThreadFlag)
				{
					try {
						if(socket==null)
							socket = new Socket(HOST,PORT);
						OutputStream outputStream=socket.getOutputStream();
						
						while(true)
						{

							if(EndFlag==false)
							{
								Thread.sleep(1);
							}

							if(EndFlag==true)
							{
								String toString="";
								outputStream.write((toString+GetPicForByte().length).getBytes("UTF-8"));
								EndFlag=false;
								break;
							}

						}
            		 while(true)
  						{
  							if(EndFlag==false)
  							{
  								Thread.sleep(1);
  							}
  							
  							if(EndFlag==true)
  							{
  								outputStream.write(GetPicForByte());
  								EndFlag=false;
  								break;
  							}

  						}
						Thread.sleep(1000);
						
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
				
			}
			
		}
		class ReceiveThread implements Runnable
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				byte [] bytes = new byte [1024];
				while(ThreadFlag)
				{
					try {
						while(socket==null)
						{
						Thread.sleep(1);
						}
						InputStream inputStream = socket.getInputStream();
						inputStream.read(bytes);
						String inString=new String(bytes,"UTF-8");
						if(inString.contains(end_tag_String))
						{
							EndFlag=true;
						}		
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				
			}
			
		}
		
	}
	

}
