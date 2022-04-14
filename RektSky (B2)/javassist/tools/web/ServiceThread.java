package javassist.tools.web;

import java.net.*;
import java.io.*;

class ServiceThread extends Thread
{
    Webserver web;
    Socket sock;
    
    public ServiceThread(final Webserver w, final Socket s) {
        this.web = w;
        this.sock = s;
    }
    
    @Override
    public void run() {
        try {
            this.web.process(this.sock);
        }
        catch (IOException ex) {}
    }
}
