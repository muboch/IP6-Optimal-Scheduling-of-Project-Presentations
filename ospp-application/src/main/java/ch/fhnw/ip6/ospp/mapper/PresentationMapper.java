package ch.fhnw.ip6.ospp.mapper;

import ch.fhnw.ip6.common.dto.PresentationDto;
import ch.fhnw.ip6.ospp.model.Presentation;
import ch.fhnw.ip6.ospp.vo.PresentationVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(uses = {LecturerMapper.class, StudentMapper.class})
public interface PresentationMapper {


    @Mapping(target = "name", source = "presentation.studentOne.name")
    @Mapping(target = "schoolclass", source = "presentation.studentOne.schoolclass")
    @Mapping(target = "name2", source = "presentation.studentTwo.name")
    @Mapping(target = "schoolclass2", source = "presentation.studentTwo.schoolclass")
    @Mapping(target = "coachInitials", source = "presentation.coach.initials")
    @Mapping(target = "expertInitials", source = "presentation.expert.initials")
    PresentationDto fromEntityToDto(Presentation presentation);

    @Mapping(target = "studentOne", source = "presentation.studentOne")
    @Mapping(target = "studentTwo", source = "presentation.studentTwo")
    @Mapping(target = "coach", source = "presentation.coach")
    @Mapping(target = "expert", source = "presentation.expert")
    @Mapping(target = "timeslot", source = "presentation.timeslot.id")
    @Mapping(target = "room", source = "presentation.room.id")
    PresentationVO fromEntityToVo(Presentation presentation);

}
