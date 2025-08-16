package Processes;

import Data.*;
import Business.InquiryHandling;
import Business.InquiryManager;
import HandleStoreFiles.HandleFiles;

import static Business.InquiryManager.activeInquiriesMap;
import static Business.InquiryManager.representativeList;

public class InquiryHandlingProcess extends Thread {
    private final Inquiry inquiry;

    public InquiryHandlingProcess(Inquiry inquiry) {
        this.inquiry = inquiry;
    }

    @Override
    public void run() {
        try {
            System.out.println("Handling inquiry");
            InquiryHandling.handleInquiry(inquiry);

            System.out.println("Changing status");
            inquiry.setStatus(InquiryStatus.ARCHIVED);
            HandleFiles.updateFile(inquiry);

            System.out.println("Moving inquiry to history");
            InquiryHandling.moveInquiryToHistory(inquiry);

            System.out.println("Returning representative to queue");
            Representative rep = activeInquiriesMap.get(inquiry);
            representativeList.add(rep);

            System.out.println("Removing representative and inquiry from map");
            activeInquiriesMap.remove(inquiry);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
