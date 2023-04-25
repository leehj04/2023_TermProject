import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class CMClientApp {
    private CMClientStub m_clientStub;
    private CMClientEventHandler m_eventHandler;

    public CMClientApp()
    {
        m_clientStub = new CMClientStub();
        m_eventHandler = new CMClientEventHandler(m_clientStub);
    }

    public CMClientStub getClientStub()
    {
        return m_clientStub;
    }

    public CMClientEventHandler getClientEventHandler()
    {
        return m_eventHandler;
    }



    public static void main(String[] args){

        Scanner scanner = new Scanner(System.in);

        CMClientApp client = new CMClientApp();
        // CMClientStub cmStub = client.getClientStub();
        // cmStub.setAppEventHandler(client.getClientEventHandler());
        //cmStub.startCM();
        CMClientStub clientStub = client.getClientStub();
        CMClientEventHandler eventHandler = client.getClientEventHandler();
        boolean ret = false;

        clientStub.setAppEventHandler(eventHandler);
        ret = clientStub.startCM();

        if(ret)
            System.out.println("init success");
        else{
            System.err.println("init error!");
            return;
        }

        String strUserName = null;
        String strPassword = null;
        boolean bRequestResult = false;
        Console console = System.console();

        System.out.print("user name: ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            strUserName = br.readLine();
            if(console == null)
            {
                System.out.print("password: ");
                strPassword = br.readLine();
            }
            else
                strPassword = new String(console.readPassword("password: "));
        } catch (IOException e){
            e.printStackTrace();
        }

        System.out.println("user name: "+strUserName);
        System.out.println("password: "+strPassword);

        bRequestResult = clientStub.loginCM(strUserName, strPassword);

        if(bRequestResult)
            System.out.println("successfully sent the login request.");
        else
            System.err.println("failed the login request!");

        // System.out.println("Press enter to execute next API:");
        // Scanner.nextLine();


    }
}
