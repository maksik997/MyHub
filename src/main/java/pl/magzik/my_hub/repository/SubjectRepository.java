package pl.magzik.my_hub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.magzik.my_hub.model.studies.Subject;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {
}
