package ch.fhnw.ip6.ospp.persistence;

import ch.fhnw.ip6.ospp.model.Timeslot;
import ch.fhnw.ip6.ospp.vo.TimeslotVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface TimeslotRepository extends JpaRepository<Timeslot, Long> {

    List<TimeslotVO> findAllProjectedBy();

}