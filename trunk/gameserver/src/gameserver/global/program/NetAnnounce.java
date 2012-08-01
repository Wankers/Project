/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameserver.global.program;

import gameserver.global.additions.MessagerAddition;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Dean
 */
public class NetAnnounce {
   
    public static void init()
    {
          Global global = new Global();
          Thread thread = new Thread(global);
          thread.setName("[Global]Net Announcement");
          thread.start();
    }
    
   static class Global implements Runnable {

        @Override
        public void run() {
            try {
                ServerSocket ss=new ServerSocket(1800);
                while(true)
                {
                    Socket sc = ss.accept();
                    BufferedReader br = new BufferedReader(new InputStreamReader(sc.getInputStream()));
                     MessagerAddition.announceAll("[Global]:"+br.readLine(), 0);
                     PrintWriter wr=new PrintWriter(new OutputStreamWriter(sc.getOutputStream()),true);
                }
                    
            }
            catch(Exception ex) {
                
            }
        }
        
   
    }
}
