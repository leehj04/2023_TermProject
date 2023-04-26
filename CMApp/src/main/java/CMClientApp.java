import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
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
        CMSessionEvent loginAckEvent = null;

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

        // System.out.println("user name: "+strUserName);
        // System.out.println("password: "+strPassword);

        loginAckEvent = clientStub.syncLoginCM(strUserName, strPassword);
        // bRequestResult = clientStub.loginCM(strUserName, strPassword);
        /*
        if(bRequestResult)
            System.out.println("successfully sent the login request.");
        else
            System.err.println("failed the login request!");
        */

        if(loginAckEvent != null)
        {
            if(loginAckEvent.isValidUser() == 0)
                System.err.println("This client fails authentication by the default server!");
            else if(loginAckEvent.isValidUser() == -1)
                System.err.println("This client is already in the login-user list!");
            else
                System.out.println("This client successfully logs in to the default server.");
        }
        else
            System.err.println("failed the login request!");

        // System.out.println("Press enter to execute next API:");
        // Scanner.nextLine();

        CMInteractionInfo interInfo = clientStub.getCMInfo().getInteractionInfo();
        CMUser myself = interInfo.getMyself();

        System.out.println("===== test CMDummyEvent in current group");
        BufferedReader br2 = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("input message: ");
        String strInput = null;
        try {
            strInput = br2.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        CMDummyEvent due = new CMDummyEvent();
        due.setHandlerSession(myself.getCurrentSession());
        due.setHandlerGroup(myself.getCurrentGroup());
        due.setDummyInfo(strInput);
        clientStub.cast(due, myself.getCurrentSession(), myself.getCurrentGroup());
        due = null;

        String strFileName = null;
        String strFileOwner = null;
        BufferedReader br3 = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("===== request a file");
        try {
            System.out.print("File name: ");
            strFileName = br3.readLine();
            System.out.print("File owner(server name): ");
            strFileOwner = br3.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        clientStub.requestFile(strFileName, strFileOwner);

        String strFilePath = null;
        String strReceiver = null;
        BufferedReader br4 = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("===== push a file");
        try {
            System.out.print("File path name: ");
            strFilePath = br4.readLine();
            System.out.print("File receiver: ");
            strReceiver = br4.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        clientStub.pushFile(strFilePath, strReceiver);


    }
}
