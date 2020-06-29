package ch.fhnw.ip6.common.dto.marker;

public interface L {

    int getId();

    String getFirstname();

    String getLastname();

    String getEmail();

    String getInitials();

    default String getName(){
        return getLastname() + " " + getFirstname();
    }

}
