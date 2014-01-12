package net.marioosh.jacob;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.UUID;

import org.apache.commons.io.IOUtils;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.EnumVariant;
import com.jacob.com.Variant;


public class Main {

	static {
		loadJacobLib();
	}
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
        System.out.println("UUID by wmic: "+getByWMIC(csproductUUID));
	}
	
	private static void loadJacobLib() {
		try {
			InputStream in = Main.class.getClassLoader().getResourceAsStream("jacob-1.14.3-x86.dll");
		    byte[] buffer = new byte[1024];
		    int read = -1;
		    File temp = File.createTempFile("jacob-1.14.3-x86", ".dll");
		    temp.deleteOnExit();
		    FileOutputStream fos = new FileOutputStream(temp);
		    while((read = in.read(buffer)) != -1) {
		        fos.write(buffer, 0, read);
		    }
		    fos.close();
		    in.close();	
		    System.setProperty("jacob.dll.path", temp.getAbsolutePath());
		    // System.load(temp.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	
	private static final String[] csproductUUID = new String[] { "wmic", "csproduct", "get", "uuid" };
	
	private static String getByWMIC(String[] command) {
		try {
		    Process process = Runtime.getRuntime().exec(command);
	        process.getOutputStream().close();
	        Scanner sc = new Scanner(process.getInputStream()).useDelimiter("\n");
	        String property = sc.next();
	        String serial = sc.next();
	        return serial.trim();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}	

}

