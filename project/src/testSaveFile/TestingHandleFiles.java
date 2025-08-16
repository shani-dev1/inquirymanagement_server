package testSaveFile;

import Business.InquiryHandling;
import Business.InquiryManager;
import Data.Inquiry;
import HandleStoreFiles.HandleFiles;
import java.io.IOException;

public class TestingHandleFiles {
    public static void main(String[] args) throws IOException {
        HandleFiles handleFiles = new HandleFiles();

        Inquiry i = InquiryManager.getQueue().remove();
        Inquiry i1 = InquiryManager.getQueue().remove();

        InquiryHandling.moveInquiryToHistory(i);
        InquiryHandling.moveInquiryToHistory(i1);
    }
}
