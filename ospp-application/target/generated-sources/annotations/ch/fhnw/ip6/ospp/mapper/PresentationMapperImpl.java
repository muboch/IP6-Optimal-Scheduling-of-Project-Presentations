package ch.fhnw.ip6.ospp.mapper;

import ch.fhnw.ip6.ospp.model.Lecturer;
import ch.fhnw.ip6.ospp.model.Presentation;
import ch.fhnw.ip6.ospp.model.Student;
import ch.fhnw.ip6.ospp.vo.LecturerVO;
import ch.fhnw.ip6.ospp.vo.PresentationVO;
import ch.fhnw.ip6.ospp.vo.StudentVO;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-04-04T17:13:18+0200",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 13.0.2 (N/A)"
)
@Component
public class PresentationMapperImpl implements PresentationMapper {

    @Override
    public ch.fhnw.ip6.common.dto.Presentation toDto(Presentation presentation) {
        if ( presentation == null ) {
            return null;
        }

        ch.fhnw.ip6.common.dto.Presentation presentation1 = new ch.fhnw.ip6.common.dto.Presentation();

        presentation1.setExpertInitials( presentationExpertInitials( presentation ) );
        presentation1.setSchoolclass( presentationStudentOneSchoolclass( presentation ) );
        presentation1.setName( presentationStudentOneName( presentation ) );
        presentation1.setCoachInitials( presentationCoachInitials( presentation ) );
        presentation1.setId( presentation.getExternalId() );
        presentation1.setName2( presentationStudentTwoName( presentation ) );
        presentation1.setSchoolclass2( presentationStudentTwoSchoolclass( presentation ) );
        presentation1.setNr( presentation.getNr() );
        presentation1.setTitle( presentation.getTitle() );
        presentation1.setCoach( lecturerToLecturer( presentation.getCoach() ) );
        presentation1.setExpert( lecturerToLecturer( presentation.getExpert() ) );
        presentation1.setType( presentation.getType() );

        return presentation1;
    }

    @Override
    public ch.fhnw.ip6.common.dto.Presentation toDto(PresentationVO presentation) {
        if ( presentation == null ) {
            return null;
        }

        ch.fhnw.ip6.common.dto.Presentation presentation1 = new ch.fhnw.ip6.common.dto.Presentation();

        presentation1.setExpertInitials( presentationExpertInitials1( presentation ) );
        presentation1.setSchoolclass( presentationStudentOneSchoolclass1( presentation ) );
        presentation1.setNr( presentation.getNr() );
        presentation1.setName( presentationStudentOneName1( presentation ) );
        presentation1.setCoachInitials( presentationCoachInitials1( presentation ) );
        presentation1.setId( presentation.getExternalId() );
        presentation1.setType( presentation.getType() );
        presentation1.setName2( presentationStudentTwoName1( presentation ) );
        presentation1.setSchoolclass2( presentationStudentTwoSchoolclass1( presentation ) );
        presentation1.setTitle( presentation.getTitle() );
        presentation1.setCoach( lecturerVOToLecturer( presentation.getCoach() ) );
        presentation1.setExpert( lecturerVOToLecturer( presentation.getExpert() ) );

        return presentation1;
    }

    private String presentationExpertInitials(Presentation presentation) {
        if ( presentation == null ) {
            return null;
        }
        Lecturer expert = presentation.getExpert();
        if ( expert == null ) {
            return null;
        }
        String initials = expert.getInitials();
        if ( initials == null ) {
            return null;
        }
        return initials;
    }

    private String presentationStudentOneSchoolclass(Presentation presentation) {
        if ( presentation == null ) {
            return null;
        }
        Student studentOne = presentation.getStudentOne();
        if ( studentOne == null ) {
            return null;
        }
        String schoolclass = studentOne.getSchoolclass();
        if ( schoolclass == null ) {
            return null;
        }
        return schoolclass;
    }

    private String presentationStudentOneName(Presentation presentation) {
        if ( presentation == null ) {
            return null;
        }
        Student studentOne = presentation.getStudentOne();
        if ( studentOne == null ) {
            return null;
        }
        String name = studentOne.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private String presentationCoachInitials(Presentation presentation) {
        if ( presentation == null ) {
            return null;
        }
        Lecturer coach = presentation.getCoach();
        if ( coach == null ) {
            return null;
        }
        String initials = coach.getInitials();
        if ( initials == null ) {
            return null;
        }
        return initials;
    }

    private String presentationStudentTwoName(Presentation presentation) {
        if ( presentation == null ) {
            return null;
        }
        Student studentTwo = presentation.getStudentTwo();
        if ( studentTwo == null ) {
            return null;
        }
        String name = studentTwo.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private String presentationStudentTwoSchoolclass(Presentation presentation) {
        if ( presentation == null ) {
            return null;
        }
        Student studentTwo = presentation.getStudentTwo();
        if ( studentTwo == null ) {
            return null;
        }
        String schoolclass = studentTwo.getSchoolclass();
        if ( schoolclass == null ) {
            return null;
        }
        return schoolclass;
    }

    protected ch.fhnw.ip6.common.dto.Lecturer lecturerToLecturer(Lecturer lecturer) {
        if ( lecturer == null ) {
            return null;
        }

        ch.fhnw.ip6.common.dto.Lecturer lecturer1 = new ch.fhnw.ip6.common.dto.Lecturer();

        lecturer1.setId( (int) lecturer.getId() );
        lecturer1.setFirstname( lecturer.getFirstname() );
        lecturer1.setLastname( lecturer.getLastname() );
        lecturer1.setEmail( lecturer.getEmail() );
        lecturer1.setInitials( lecturer.getInitials() );

        return lecturer1;
    }

    private String presentationExpertInitials1(PresentationVO presentationVO) {
        if ( presentationVO == null ) {
            return null;
        }
        LecturerVO expert = presentationVO.getExpert();
        if ( expert == null ) {
            return null;
        }
        String initials = expert.getInitials();
        if ( initials == null ) {
            return null;
        }
        return initials;
    }

    private String presentationStudentOneSchoolclass1(PresentationVO presentationVO) {
        if ( presentationVO == null ) {
            return null;
        }
        StudentVO studentOne = presentationVO.getStudentOne();
        if ( studentOne == null ) {
            return null;
        }
        String schoolclass = studentOne.getSchoolclass();
        if ( schoolclass == null ) {
            return null;
        }
        return schoolclass;
    }

    private String presentationStudentOneName1(PresentationVO presentationVO) {
        if ( presentationVO == null ) {
            return null;
        }
        StudentVO studentOne = presentationVO.getStudentOne();
        if ( studentOne == null ) {
            return null;
        }
        String name = studentOne.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private String presentationCoachInitials1(PresentationVO presentationVO) {
        if ( presentationVO == null ) {
            return null;
        }
        LecturerVO coach = presentationVO.getCoach();
        if ( coach == null ) {
            return null;
        }
        String initials = coach.getInitials();
        if ( initials == null ) {
            return null;
        }
        return initials;
    }

    private String presentationStudentTwoName1(PresentationVO presentationVO) {
        if ( presentationVO == null ) {
            return null;
        }
        StudentVO studentTwo = presentationVO.getStudentTwo();
        if ( studentTwo == null ) {
            return null;
        }
        String name = studentTwo.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private String presentationStudentTwoSchoolclass1(PresentationVO presentationVO) {
        if ( presentationVO == null ) {
            return null;
        }
        StudentVO studentTwo = presentationVO.getStudentTwo();
        if ( studentTwo == null ) {
            return null;
        }
        String schoolclass = studentTwo.getSchoolclass();
        if ( schoolclass == null ) {
            return null;
        }
        return schoolclass;
    }

    protected ch.fhnw.ip6.common.dto.Lecturer lecturerVOToLecturer(LecturerVO lecturerVO) {
        if ( lecturerVO == null ) {
            return null;
        }

        ch.fhnw.ip6.common.dto.Lecturer lecturer = new ch.fhnw.ip6.common.dto.Lecturer();

        lecturer.setId( lecturerVO.getId() );
        lecturer.setFirstname( lecturerVO.getFirstname() );
        lecturer.setLastname( lecturerVO.getLastname() );
        lecturer.setEmail( lecturerVO.getEmail() );
        lecturer.setInitials( lecturerVO.getInitials() );

        return lecturer;
    }
}
