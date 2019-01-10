package com.quadcore.Lively;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.ByteArrayOutputStream;
import java.util.Properties;

	public class SSHConnection extends AsyncTask<Integer, Void, String> {
	    private final String TAG = "SSHConnection";

	    private JSch jsch;
	    private Session session;
	    private ChannelExec channel;
	    private String username;
	    private String host;
	    private int port;
	    private ByteArrayOutputStream baos;

	    public SSHConnection(String username, String host, int port) {
	        this.username = username;
	        this.host = host;
	        this.port = port;

	        try {
	            jsch = new JSch();
	            session = jsch.getSession(username, host, port);
	        } catch (JSchException e) {
	            e.printStackTrace();
	        }
	    }

	    @Override
	    protected String doInBackground(Integer... integers) {

	        try {
	            baos = new ByteArrayOutputStream();
	            session.setPassword("hadoop");
	            Properties prop = new Properties();
	            prop.put("StrictHostKeyChecking", "no");
	            session.setConfig(prop);
	            session.connect();

	            channel = (ChannelExec) session.openChannel("exec");
	            channel.setOutputStream(baos);
	            channel.setCommand("ls -al");
	            channel.connect();
	            System.out.println("doInBackground: 시작 " + channel.isConnected() + ", " + channel.isClosed() + ", " + channel.getExitStatus());
	            //doInBackground: 여기 시작 true, false, -1
	            while (channel.isConnected()) { /** TODO : 이 부분 해결해야한다. */ // 참고 : channel.getExitStatus() != 1
	                try {
	                    Thread.sleep(1000); // 최소 1초 이상 필요
	                    System.out.println("doInBackground: " + baos.toString()); // 명령에 대한 결과 호출 (반드시 Thread.sleep 이후에 작동)
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }

	                channel.disconnect();
	            }
	            session.disconnect();
	            System.out.println("doInBackground: 끝 " + channel.isConnected() + ", " + channel.isClosed() + ", " + channel.getExitStatus());

	        } catch (JSchException e) {
	            e.printStackTrace();
	        }

	        return baos.toString();
	    }

	}
	
}
