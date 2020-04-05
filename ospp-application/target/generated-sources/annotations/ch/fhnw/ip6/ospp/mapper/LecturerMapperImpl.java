package ch.fhnw.ip6.ospp.mapper;

import ch.fhnw.ip6.common.dto.Lecturer;
import ch.fhnw.ip6.ospp.vo.LecturerVO;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-04-05T20:53:13+0200",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 11.0.6 (Oracle Corporation)"
)
@Component
public class LecturerMapperImpl implements LecturerMapper {

    @Override
    public Lecturer toDto(ch.fhnw.ip6.ospp.model.Lecturer lecturer) {
        if ( lecturer == null ) {
            return null;
        }

        Lecturer lecturer1 = new Lecturer();

        lecturer1.setId( lecturer.getExternalId() );
        lecturer1.setFirstname( lecturer.getFirstname() );
        lecturer1.setLastname( lecturer.getLastname() );
        lecturer1.setEmail( lecturer.getEmail() );
        lecturer1.setInitials( lecturer.getInitials() );

        return lecturer1;
    }

    @Override
    public Lecturer toDto(LecturerVO lecturer) {
        if ( lecturer == null ) {
            return null;
        }

        Lecturer lecturer1 = new Lecturer();

        lecturer1.setId( lecturer.getExternalId() );
        lecturer1.setFirstname( lecturer.getFirstname() );
        lecturer1.setLastname( lecturer.getLastname() );
        lecturer1.setEmail( lecturer.getEmail() );
        lecturer1.setInitials( lecturer.getInitials() );

        return lecturer1;
    }
}
