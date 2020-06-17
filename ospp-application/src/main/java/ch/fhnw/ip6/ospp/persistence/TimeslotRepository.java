package ch.fhnw.ip6.ospp.persistence;

import ch.fhnw.ip6.ospp.model.Timeslot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface TimeslotRepository extends JpaRepository<Timeslot, Long> {
    
    Timeslot findByDate(String date);
}
