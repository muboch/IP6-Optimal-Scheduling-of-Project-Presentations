package ch.fhnw.ip6.ospp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:csv.yml")
public class LecturersProperties {

    @Value("${csv.lecturers.firstname}")
    public String lecturersFirstname;

    @Value("${csv.lecturers.initials}")
    public String lecturersInitials;

    @Value("${csv.lecturers.email}")
    public String lecturersEmail;

    @Value("${csv.lecturers.lastname}")
    public String lecturersLastname;
}