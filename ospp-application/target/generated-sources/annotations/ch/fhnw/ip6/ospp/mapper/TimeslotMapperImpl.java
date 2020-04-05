package ch.fhnw.ip6.ospp.mapper;

import ch.fhnw.ip6.common.dto.Timeslot;
import ch.fhnw.ip6.ospp.vo.TimeslotVO;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-04-05T20:53:13+0200",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 11.0.6 (Oracle Corporation)"
)
@Component
public class TimeslotMapperImpl implements TimeslotMapper {

    @Override
    public Timeslot toDto(ch.fhnw.ip6.ospp.model.Timeslot timeslot) {
        if ( timeslot == null ) {
            return null;
        }

        Timeslot timeslot1 = new Timeslot();

        timeslot1.setId( timeslot.getExternalId() );
        timeslot1.setDate( timeslot.getDate() );
        timeslot1.setBlock( timeslot.getBlock() );
        timeslot1.setPriority( timeslot.getPriority() );

        return timeslot1;
    }

    @Override
    public Timeslot toDto(TimeslotVO timeslot) {
        if ( timeslot == null ) {
            return null;
        }

        Timeslot timeslot1 = new Timeslot();

        timeslot1.setId( timeslot.getExternalId() );
        timeslot1.setDate( timeslot.getDate() );
        timeslot1.setBlock( timeslot.getBlock() );

        return timeslot1;
    }
}
