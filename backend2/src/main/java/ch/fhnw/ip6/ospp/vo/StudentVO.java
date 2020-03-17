package ch.fhnw.ip6.ospp.vo;

import ch.fhnw.ip6.ospp.model.Discipline;


public interface StudentVO {

    long getId();

    String getFirstname();

    String getLastname();

    String getEmail();

    String getSchoolClass();

    Discipline getDiscipline();

}
