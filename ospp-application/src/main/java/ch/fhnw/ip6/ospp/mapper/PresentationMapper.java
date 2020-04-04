package ch.fhnw.ip6.ospp.mapper;

import ch.fhnw.ip6.ospp.model.Presentation;
import ch.fhnw.ip6.ospp.vo.PresentationVO;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface PresentationMapper {


    @Mapping(target = "name", source = "presentation.studentOne.name")
    @Mapping(target = "schoolclass", source = "presentation.studentOne.schoolclass")
    @Mapping(target = "name2", source = "presentation.studentTwo.name")
    @Mapping(target = "schoolclass2", source = "presentation.studentTwo.schoolclass")
    @Mapping(target = "coachInitials", source = "presentation.coach.initials")
    @Mapping(target = "expertInitials", source = "presentation.expert.initials")
    @Mapping(target = "id", source = "presentation.externalId")
    ch.fhnw.ip6.common.dto.Presentation toDto(Presentation presentation);

    @Mapping(target = "name", source = "presentation.studentOne.name")
    @Mapping(target = "schoolclass", source = "presentation.studentOne.schoolclass")
    @Mapping(target = "name2", source = "presentation.studentTwo.name")
    @Mapping(target = "schoolclass2", source = "presentation.studentTwo.schoolclass")
    @Mapping(target = "coachInitials", source = "presentation.coach.initials")
    @Mapping(target = "expertInitials", source = "presentation.expert.initials")
    @Mapping(target = "id", source = "presentation.externalId")
    ch.fhnw.ip6.common.dto.Presentation toDto(PresentationVO presentation);

}
