import Business.InquiryManager;
import ClientServer.Server.InquiryManagerServer;
import Processes.InquiryProcess;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws Exception{
        InquiryManager inquiryManager = new InquiryManager();
        InquiryProcess inquiryProcess = new InquiryProcess(inquiryManager);
        inquiryProcess.start();

        InquiryManagerServer inquiryManagerServer = new InquiryManagerServer(6000);
        inquiryManagerServer.start();

        try {
            System.out.println("Server is running. Press Enter to stop");
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        inquiryManagerServer.stop();

    }

}





