package Processes;

import Data.Inquiry;

public class Simulation extends Thread{
    Inquiry inquiry;

    public Simulation(Inquiry inquiry) {
        this.inquiry = inquiry;
    }

    @Override
    public void run() {
        InquiryHandlingProcess handlingProcess = new InquiryHandlingProcess(inquiry);
        handlingProcess.start();

    }

}
