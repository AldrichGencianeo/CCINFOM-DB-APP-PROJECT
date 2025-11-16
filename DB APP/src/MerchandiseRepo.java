import java.util.List;

public interface MerchandiseRepo {
    Merchandise findByID(int merchandiseID);
    List<Merchandise> findAll();
}
