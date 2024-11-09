package git.dimitrikvirik.sap.repository;

import git.dimitrikvirik.sap.model.entity.City;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CityRepository extends JpaRepository<City, Long> {

    @Query("SELECT c FROM  City c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%') ) ")
    List<City> findByNameContains(@Param("name") String name, Sort sort);


}
