package ch.fhnw.ip6.ospp.mapper;

import ch.fhnw.ip6.common.dto.PresentationDto;
import ch.fhnw.ip6.ospp.model.Presentation;
import ch.fhnw.ip6.ospp.vo.PresentationVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface PresentationMapper {


    @Mapping(target = "coachInitials", source = "presentation.coach.initials")
    @Mapping(target = "expertInitials", source = "presentation.expert.initials")
    PresentationDto fromEntityToDto(Presentation presentation);

    @Mapping(target = "studentOne", source = "presentation.studentOne.id")
    @Mapping(target = "studentTwo", source = "presentation.studentTwo.id")
    @Mapping(target = "coach", source = "presentation.coach.id")
    @Mapping(target = "expert", source = "presentation.expert.id")
    @Mapping(target = "timeslot", source = "presentation.timeslot.id")
    @Mapping(target = "room", source = "presentation.room.id")
    PresentationVO fromEntityToVo(Presentation presentation);

    @Mapping(target="room", ignore=true)
    @Mapping(target="studentOne", ignore=true)
    @Mapping(target="studentTwo", ignore=true)
    @Mapping(target="coach", ignore=true)
    @Mapping(target="expert", ignore=true)
    @Mapping(target="timeslot", ignore=true)
    Presentation fromVoToEntity(PresentationVO lecturerVO);

}
