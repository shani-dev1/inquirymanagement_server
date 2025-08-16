package ClientServer.Server;

import Business.InquiryHandling;
import Business.InquiryManager;
import ClientServer.RequestData;
import ClientServer.ResponseData;
import ClientServer.ResponseStatus;
import Data.Inquiry;
import Data.InquiryStatus;
import Data.Representative;
import HandleStoreFiles.HandleFiles;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.List;

public class HandleClient implements Runnable {
    private Socket clientSocket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    public HandleClient(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            inputStream = new ObjectInputStream(clientSocket.getInputStream());

            while (true) {
                try {
                    RequestData request = (RequestData) inputStream.readObject();
                    ResponseData responseData = handleClientRequest(request);
                    outputStream.writeObject(responseData);
                    outputStream.flush();
                } catch (IOException e) {
                    break;
                }
            }
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResponseData handleClientRequest(RequestData request) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        switch (request.action) {
            case ADD_INQUIRY:
                return addInquiry(request.parameters);
            case ALL_INQUIRY:
                return getAllInquiries(request.parameters);
            case GET_INQUIRY_STATUS:
                return getInquiryStatus(request.parameters);
            case CANCEL_INQUIRY:
                return cancelInquiry(request.parameters);
            case GET_REPRESENTATIVE_NAME_BY_INQUIRY_CODE:
                return getRepresentativeNameByInquiryCode(request.parameters);
            case ADD_REPRESENTATIVE:
                return addRepresentative(request.parameters);
            case DELETE_REPRESENTATIVE:
                return deleteRepresentative(request.parameters);
            case IS_REPRESENTATIVE_ACTIVE:
                return isRepresentativeActive(request.parameters);
            case GET_REPRESENTATIVE_INQUIRIES:
                return getAllInquiriesByRepresentative(request.parameters);
            case GET_ACTIVE_REPRESENTATIVES:
                return getActiveRepresentatives();
            default:
                return new ResponseData(ResponseStatus.FAIL, "Invalid request", null);
        }
    }

    public ResponseData getRepresentativeNameByInquiryCode(List<Object> parameters) {
        try {
            int code = Integer.parseInt((String) parameters.get(0));
            if (!isInquiryExists(code, true)) {
                return new ResponseData(ResponseStatus.FAIL, "Inquiry not found", null);
            }
            String repName = InquiryHandling.getRepresentativeNameByInquiryCode(code);
            return new ResponseData(ResponseStatus.SUCCESS, "", repName);
        } catch (Exception e) {
            return new ResponseData(ResponseStatus.FAIL, "", null);
        }
    }

    public ResponseData addInquiry(List<Object> parameters) {
        Inquiry inquiry = (Inquiry) parameters.get(0);
        InquiryManager inquiryManager = new InquiryManager();
        try {
            int result = inquiryManager.addInquiryFromClient(inquiry);
            return new ResponseData(ResponseStatus.SUCCESS, "Inquiry added successfully", result);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseData(ResponseStatus.FAIL, "Error adding inquiry", -1);
        }
    }

    public ResponseData getAllInquiries(List<Object> parameters) {
        try {
            InquiryManager inquiryManager = new InquiryManager();
            Object result = inquiryManager.getQueue();
            return new ResponseData(ResponseStatus.SUCCESS, "Retrieved all inquiries", result);
        } catch (Exception e) {
            return new ResponseData(ResponseStatus.FAIL, "Error retrieving inquiries", null);
        }
    }

    public ResponseData cancelInquiry(List<Object> parameters) {
        int code = Integer.parseInt((String) parameters.get(0));
        if (!isInquiryExists(code, false)) {
            return new ResponseData(ResponseStatus.FAIL, "Inquiry not found", null);
        }
        try {
            new InquiryHandling().cancelInquiry(code);
            return new ResponseData(ResponseStatus.SUCCESS, "Inquiry cancelled successfully", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseData(ResponseStatus.FAIL, "Error cancelling inquiry", null);
        }
    }

    public ResponseData getInquiryStatus(List<Object> parameters) {
        int code = Integer.parseInt((String) parameters.get(0));
        if (!isInquiryExists(code, true)) {
            return new ResponseData(ResponseStatus.FAIL, "Inquiry not found", null);
        }
        try {
            InquiryStatus inquiryStatus = new InquiryHandling().getInquiryStatusByCode(code);
            return new ResponseData(ResponseStatus.SUCCESS, "Retrieved inquiry status", inquiryStatus);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseData(ResponseStatus.FAIL, "Error retrieving inquiry status", null);
        }
    }

    public ResponseData addRepresentative(List<Object> parameters) {
        try {
            if (parameters.size() < 2) {
                return new ResponseData(ResponseStatus.FAIL, "Missing parameters for representative creation", null);
            }
            int id = Integer.parseInt(parameters.get(0).toString());
            String name = parameters.get(1).toString();
            if (isRepresentativeExists(id)) {
                return new ResponseData(ResponseStatus.FAIL, "Representative already exists", null);
            }
            Representative representative = new Representative(id, name);
            InquiryManager.representativeList.add(representative);
            File directory = new File("Representative");
            if (!directory.exists()) directory.mkdir();
            String filename = "Representative/" + representative.getCode() + ".csv";
            boolean success = new HandleFiles().saveCSV(representative, filename);
            return new ResponseData(success ? ResponseStatus.SUCCESS : ResponseStatus.FAIL, success ? "Representative added successfully" : "Error saving representative", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseData(ResponseStatus.FAIL, "Error adding representative: " + e.getMessage(), null);
        }
    }

    public ResponseData deleteRepresentative(List<Object> parameters) {
        int id;
        try {
            id = Integer.parseInt(parameters.get(0).toString());
        } catch (NumberFormatException e) {
            return new ResponseData(ResponseStatus.FAIL, "Invalid representative ID", null);
        }
        if (!isRepresentativeExists(id)) {
            return new ResponseData(ResponseStatus.FAIL, "Representative not found", null);
        }
        Representative toRemove = InquiryManager.representativeList.stream().filter(rep -> rep.getId() == id).findFirst().orElse(null);
        if (toRemove != null) InquiryManager.representativeList.remove(toRemove);
        File file = new File("Representative/" + id + ".csv");
        if (file.exists()) file.delete();
        return new ResponseData(ResponseStatus.SUCCESS, "Representative deleted successfully", null);
    }

    public ResponseData isRepresentativeActive(List<Object> parameters) {
        int code = (int) parameters.get(0);
        try {
            boolean result = isRepresentativeExists(code) && new InquiryManager().isRepresentativeActive(code);
            return new ResponseData(ResponseStatus.SUCCESS, "Check completed", result);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseData(ResponseStatus.FAIL, "Server error", false);
        }
    }

    public ResponseData getAllInquiriesByRepresentative(List<Object> parameters) {
        int code = (int) parameters.get(0);
        try {
            if (!isRepresentativeExists(code)) {
                return new ResponseData(ResponseStatus.FAIL, "Representative not found", null);
            }
            List<Inquiry> result = new InquiryManager().getAllInquiriesByRepresentative(code);
            return new ResponseData(ResponseStatus.SUCCESS, "Retrieved inquiries by representative", result);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseData(ResponseStatus.FAIL, "Server error", null);
        }
    }

    public ResponseData getActiveRepresentatives() {
        try {
            int result = InquiryManager.activeInquiriesMap.size();
            return new ResponseData(ResponseStatus.SUCCESS, "Retrieved active representatives", result);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseData(ResponseStatus.FAIL, "Server error", null);
        }
    }

    public static boolean isRepresentativeExists(int repId) {
        for (Representative rep : InquiryManager.representativeList) {
            if (rep.getId() == repId) return true;
        }
        for (Representative rep : InquiryManager.activeInquiriesMap.values()) {
            if (rep.getId() == repId) return true;
        }
        File dir = new File("Representative");
        if (dir.exists()) {
            HandleFiles hf = new HandleFiles();
            for (File file : dir.listFiles()) {
                Representative rep = (Representative) hf.readCsv(file.getAbsolutePath());
                if (rep != null && rep.getId() == repId) return true;
            }
        }
        return false;
    }

    public static boolean isInquiryExists(int code, boolean searchInHistory) {
        for (Inquiry inq : InquiryManager.queue) {
            if (inq.getCode() == code) return true;
        }
        for (Inquiry inq : InquiryManager.activeInquiriesMap.keySet()) {
            if (inq.getCode() == code) return true;
        }
        if (searchInHistory) {
            File dir = new File("InquiryHistory");
            if (dir.exists()) {
                HandleFiles hf = new HandleFiles();
                for (File file : dir.listFiles()) {
                    Inquiry inq = (Inquiry) hf.readTxt(file);
                    if (inq != null && inq.getCode() == code) return true;
                }
            }
        }
        return false;
    }
}
