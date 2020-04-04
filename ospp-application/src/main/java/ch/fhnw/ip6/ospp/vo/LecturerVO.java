package ch.fhnw.ip6.ospp.vo;


import org.springframework.beans.factory.annotation.Value;

public interface LecturerVO {

    @Value("#{target.externalId}")
    int getId();

    int getExternalId();

    String getFirstname();

    String getLastname();

    String getEmail();

    String getInitials();

}
