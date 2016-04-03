package com.example.dashdemo2;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.R.integer;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity implements OnClickListener {

	private Button button;
	private Map<String, downObject> urlHashMap = new ConcurrentHashMap<String,downObject>(); 
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button=(Button) findViewById(R.id.button1);
        button.setOnClickListener(this);
		Log.d("Test", "onCreate");

    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){
		case R.id.button1:
			Log.d("Test", "button");
			new Thread(new Runnable(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					testDownload();
				}
            }).start();
			break;
		}
	}
	
	public  Map<String, downObject> testDownload() {
        String urlString = "http://10.105.36.71/dashdemo/download.php";
        byte[] buff=null;
        int hasWritten=0;
        boolean[] isWrite=null;
        downObject downPiece = new downObject(buff,isWrite,hasWritten);
        
        HttpURLConnection connection=null;
        try {
            URL url = new URL(urlString);
            connection= (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(5000);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            String data = "filename=photo.jpg&sessionid=lykfr9oyqipf2q3tvy2l73bqo3a2&id=1";
            OutputStream out=connection.getOutputStream();
            out.write(data.getBytes());
            out.flush();
            out.close();
            Log.d("ResponseCode",String.valueOf(connection.getResponseCode()));
            if (connection.getResponseCode()==206) {
                InputStream in=connection.getInputStream();
                String contentRange=connection.getHeaderField("Content-Range").toString();
                String range = contentRange.split(" ")[1].trim();
                String start = range.split("-")[0];
                String end = range.split("-")[1].split("/")[0];
                String total = range.split("-")[1].split("/")[1];
                Log.d("Total",total);
                Log.d("PieceStart",start);
                Log.d("PieceEnd",end);
                //...拿到输入流操作
                int startOffset=Integer.parseInt(start);
                int endOffset=Integer.parseInt(end);
                int totalLength=Integer.parseInt(total);
                int pieceLength=endOffset-startOffset+1;
               
                if(urlHashMap.containsKey(urlString)){
                	downPiece=urlHashMap.get(urlString);
                	buff=downPiece.getBuff();
                	hasWritten=downPiece.getHasWritten();
                	isWrite=downPiece.getIsWrite();
                }else{
                	//新建数组用于存放流数据和判断是否写完
                	buff=new byte[totalLength];
                    isWrite=new boolean[totalLength];
                    for(int i=0;i<totalLength;i++){
                    	isWrite[i]=false;
                    }
//                    downPiece.setBuff(buff);
//                    downPiece.setHasWritten(hasWritten);
//                    downPiece.setIsWrite(isWrite);
//                    urlHashMap.put(urlString, downPiece);
                }
                if(hasWritten<totalLength){
                	
                	//inputstream转byte[]
//	                byte[] tmpstash=new byte[1024];
//	                ByteArrayOutputStream tmpout = new ByteArrayOutputStream(); 
//	                int n=0;
//	                while((n=in.read(tmpstash))>0){
//	                	tmpout.write(tmpstash,0,n);
//	                }
//	                byte[] tmpbuff =tmpout.toByteArray();
//	                tmpout.close();
//	                byte[] tmpcontent=new byte[totalLength];
//	                for(int i=startOffset;i<endOffset+1;i++){
//	                	tmpcontent[i]=tmpbuff[i-startOffset];
//	                }
//	                String result=new String(tmpbuff);
//	            	Log.d("test", result);
//	            	Log.d("ttst",new String(tmpcontent));
	               // Log.d("Test",tmpbuff.toString());
	                
                	byte[] tmpbuff=new byte[totalLength];
                	int hasRead=0;
                    while(hasRead<pieceLength){
                    	hasRead+=in.read(tmpbuff, startOffset+hasRead, pieceLength-hasRead);
                    }
                    
                    Log.d("hasRead",String.valueOf(hasRead));


	                for(int i=startOffset;i<endOffset+1;i++){
	                	if (isWrite[i]==false){
	                		buff[i]=tmpbuff[i];
	                		isWrite[i]=true;
	                		hasWritten++;
	                	}
	                }
	                Log.d("hasWritten",String.valueOf(hasWritten));
	                downPiece.setBuff(buff);
                    downPiece.setHasWritten(hasWritten);
                    downPiece.setIsWrite(isWrite);
                    urlHashMap.put(urlString, downPiece);
                }
                
                if(hasWritten==totalLength){
                	Log.d("Test",String.valueOf(hasWritten)+"Full");
                	String dir = Environment.getExternalStorageDirectory().toString();
                	String outputFile=dir+ "/android/data/photo.jpg";
                	File file=getFileFromBytes(buff, outputFile);
                }
                
                in.close();
            }else {
            	Log.d("Test", "else");
                //...返回其他状态码
//            	InputStream in=connection.getInputStream();
//            	byte[] buf=new byte[20];
//            	while(in.read(buf)!=-1);
//            	String resultString=new String(buf);
//            	Log.d("test", resultString);
//            	byte[] data1=new byte[65530];
//            	int len=0;
//            	FileOutputStream fileOutputStream=null;
//            	fileOutputStream=new FileOutputStream("D:\\test1.jpg");
//            	while ((len = in.read(data1)) != -1) {
//                    fileOutputStream.write(data1, 0, len);
//                    	
//                  }
//            	Bitmap map = BitmapFactory.decodeStream(in);
//            	Message msg=pic_hdl.obtainMessage();
//            	msg.what=0;
//            	msg.obj=map;
//            	pic_hdl.sendMessage(msg);
            	
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        	Log.d("Test", "MalformedURLException");

        } catch (IOException e) {
            e.printStackTrace();
        	Log.d("Test", "IOException");

        }finally {
            connection.disconnect();
    }
		return urlHashMap;

	}
	
	public static File getFileFromBytes(byte[] b, String outputFile) {  
        BufferedOutputStream stream = null;  
        File file = null;  
        try {  
            file = new File(outputFile); 
            FileOutputStream fstream = new FileOutputStream(file);  
            stream = new BufferedOutputStream(fstream);  
            stream.write(b);  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            if (stream != null) {  
                try {  
                    stream.close();  
                } catch (IOException e1) {  
                    e1.printStackTrace();  
                }  
            }  
        }  
        return file;  
    }  

}
