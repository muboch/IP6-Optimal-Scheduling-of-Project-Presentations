package ch.fhnw.ip6.ospp.mapper;

import ch.fhnw.ip6.common.dto.Planning;
import ch.fhnw.ip6.ospp.model.Planning.PlanningBuilder;
import ch.fhnw.ip6.ospp.vo.PlanningVO;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-04-05T20:53:13+0200",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 11.0.6 (Oracle Corporation)"
)
@Component
public class PlanningMapperImpl implements PlanningMapper {

    @Override
    public Planning toDto(ch.fhnw.ip6.ospp.model.Planning planning) {
        if ( planning == null ) {
            return null;
        }

        Planning planning1 = new Planning();

        if ( planning.getNr() != null ) {
            planning1.setNr( Integer.parseInt( planning.getNr() ) );
        }
        planning1.setStatus( planning.getStatus() );

        return planning1;
    }

    @Override
    public Planning toDto(PlanningVO planning) {
        if ( planning == null ) {
            return null;
        }

        Planning planning1 = new Planning();

        if ( planning.getNr() != null ) {
            planning1.setNr( Integer.parseInt( planning.getNr() ) );
        }
        planning1.setStatus( planning.getStatus() );

        return planning1;
    }

    @Override
    public ch.fhnw.ip6.ospp.model.Planning toEntity(Planning planning) {
        if ( planning == null ) {
            return null;
        }

        PlanningBuilder planning1 = ch.fhnw.ip6.ospp.model.Planning.builder();

        planning1.nr( String.valueOf( planning.getNr() ) );
        planning1.status( planning.getStatus() );

        return planning1.build();
    }
}
