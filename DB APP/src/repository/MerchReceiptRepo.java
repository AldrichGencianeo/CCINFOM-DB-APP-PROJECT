package repository;

import model.MerchReceipt;

import java.util.List;

public interface MerchReceiptRepo {
    List<MerchReceipt> findByEventID(int eventID);
    List<MerchReceipt> findByMonth(int year, int month);
    List<MerchReceipt> findAll();
}
