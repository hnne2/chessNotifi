package push700.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface pushRep extends JpaRepository<Push,Integer> {
    @Query(value = "select * from push",nativeQuery = true)
    List<Push> getAllPush();
}
