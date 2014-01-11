package net.marioosh.jacob;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.UUID;

import org.apache.commons.io.IOUtils;

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
                System.out.println("UUID by JACOB: "+bb.getPropertyAsString("UUID"));
                break;
            }
        } finally {
            ComThread.Release();
        }		
        
        System.out.println("UUID by cscript: "+getComputerSystemProductUUID());
	}
	
	public String getComputerSystemProductUUID() {
		try {
			String result = "";
			Process p = execCScript("Win32_hwid.vbs");
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = input.readLine()) != null) {
				result += line;
			}
			input.close();
			return result.trim();
		} catch (IOException e) {
			return null;
		}
	}	
	
	private Process execCScript(String vbsScript) {
		try {
			String vbs = classpathResourceToPath(vbsScript, ".vbs");
			String[] cmd = { "cscript", "//NoLogo",  vbs };
			Process p = Runtime.getRuntime().exec(cmd);
			return p;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private String classpathResourceToPath(String classpath, String ext)
			throws IOException {
		File resource = File.createTempFile(UUID.randomUUID().toString(), ext);
		FileOutputStream out = new FileOutputStream(resource);
		IOUtils.copy(Main.class.getClassLoader().getResourceAsStream(classpath), out);
		out.close();
		return resource.getAbsolutePath();
	}

}

