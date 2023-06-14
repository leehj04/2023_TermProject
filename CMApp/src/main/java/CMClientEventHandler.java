import kr.ac.konkuk.ccslab.cm.entity.CMSessionInfo;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMFileEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;

import java.util.Iterator;

public class CMClientEventHandler implements CMAppEventHandler {
    private CMClientStub m_clientStub;
    private long startTimeOfFileSync;

    public CMClientEventHandler(CMClientStub clientStub)
    {
        m_clientStub = clientStub;
        startTimeOfFileSync = 0;
    }

    @Override
    public void processEvent(CMEvent cme){
        switch(cme.getType())
        {
            case CMInfo.CM_SESSION_EVENT:
                processSessionEvent(cme);
                break;
            case CMInfo.CM_FILE_EVENT:
                processFileEvent(cme);
                break;
            default:
                return;
        }
    }

    private void processSessionEvent(CMEvent cme)
    {
        CMSessionEvent se = (CMSessionEvent) cme;
        switch (se.getID())
        {
            case CMSessionEvent.RESPONSE_SESSION_INFO:
                processRESPONSE_SESSION_INFO(se);
                break;
            case CMSessionEvent.LOGIN_ACK:
                if(se.isValidUser() == 0)
                {
                    System.err.println("This client fails authentication by the default server!");
                }
                else if(se.isValidUser() == -1)
                {
                    System.err.println("This client is already in the login-user list!");
                }
                else
                {
                    System.out.println("This client successfully logs in to the default server.");
                }
                break;
            default:
                return;
        }

    }

    private void processFileEvent(CMEvent cme)
    {
        CMFileEvent fe = (CMFileEvent) cme;
        switch (fe.getID())
        {
            case CMFileEvent.REPLY_PERMIT_PULL_FILE:
                if(fe.getReturnCode() == -1)
                {
                    System.err.print("["+fe.getFileName()+"] does not exist in the owner!\n");
                }
                else if(fe.getReturnCode() == 0)
                {
                    System.err.print("["+fe.getFileSender()+"] rejects to send file("+fe.getFileName()+").\n");
                }
                else {
                    System.out.print("["+fe.getFileSender()+"] send file("+fe.getFileName()+").\n");
                }
                break;
        }
        return;
    }

    private void processRESPONSE_SESSION_INFO(CMSessionEvent se)
    {
        Iterator<CMSessionInfo> iter = se.getSessionInfoList().iterator();
        System.out.format("%-60s%n", "------------------------------------------------------------");
        System.out.format("%-20s%-20s%-10s%-10s%n", "name", "address", "port",
                "user num");
        System.out.format("%-60s%n", "------------------------------------------------------------");
        while(iter.hasNext())
        {
            CMSessionInfo tInfo = iter.next();
            System.out.format("%-20s%-20s%-10d%-10d%n",
                    tInfo.getSessionName(), tInfo.getAddress(), tInfo.getPort(),
                    tInfo.getUserNum());
        }
    }

    public void setStartTimeOfFileSync(long startTimeOfFileSync) {
        this.startTimeOfFileSync = startTimeOfFileSync;
    }




}
