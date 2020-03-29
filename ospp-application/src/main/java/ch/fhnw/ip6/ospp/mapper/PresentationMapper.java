package ch.fhnw.ip6.ospp.mapper;

import ch.fhnw.ip6.ospp.model.Presentation;
import ch.fhnw.ip6.ospp.vo.PresentationVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface PresentationMapper {


    @Mapping(target = "studentOne", source = "presentation.firstStudent.name")
    @Mapping(target = "schoolclass", source = "presentation.firstStudent.schoolclass")
    @Mapping(target = "studentTwo", source = "presentation.secondStudent.name")
    @Mapping(target = "schoolclass2", source = "presentation.firstStudent.schoolclass")
    @Mapping(target = "coachInitials", source = "presentation.coach.initials")
    @Mapping(target = "expertInitials", source = "presentation.expert.initials")
    ch.fhnw.ip6.common.dto.Presentation toDto(Presentation presentation);

    @Mapping(target = "studentOne", source = "presentation.firstStudent.name")
    @Mapping(target = "schoolclass", source = "presentation.firstStudent.schoolclass")
    @Mapping(target = "studentTwo", source = "presentation.secondStudent.name")
    @Mapping(target = "schoolclass2", source = "presentation.firstStudent.schoolclass")
    @Mapping(target = "coachInitials", source = "presentation.coach.initials")
    @Mapping(target = "expertInitials", source = "presentation.expert.initials")
    ch.fhnw.ip6.common.dto.Presentation toDto(PresentationVO presentation);

}
