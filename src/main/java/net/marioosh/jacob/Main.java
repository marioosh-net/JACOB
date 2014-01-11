package net.marioosh.jacob;

import java.util.Enumeration;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.EnumVariant;
import com.jacob.com.Variant;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Main();
	}
	
	public Main() {
	    ComThread.InitMTA();
        try {
            ActiveXComponent wmi = new ActiveXComponent("winmgmts:\\\\.");
            Variant instances = wmi.invoke("InstancesOf", "Win32_ComputerSystemProduct");
            Enumeration<Variant> en = new EnumVariant(instances.getDispatch());
            while (en.hasMoreElements())
            {
                ActiveXComponent bb = new ActiveXComponent(en.nextElement().getDispatch());
                System.out.println(bb.getPropertyAsString("UUID"));
                break;
            }
        } finally {
            ComThread.Release();
        }		
	}

}
