package com.cetera.test;

import com.cetera.domain.Person;
import com.cetera.domain.Revenue;
import com.cetera.domain.Sessions;
import com.cetera.model.PersonRegisterRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by danni on 3/31/16.
 */
public class PeopleControllerIT extends ControllerIT {
    @Value("${local.server.port}")
    private int port;

    @Value("${cetera.apiKey}")
    private String apiKey;

    private URI base;
    private RestTemplate template;

    @Before
    public void setUp() throws URISyntaxException {
        this.base = new URI("http://localhost:" + port + "/api/people/external");
        template = new TestRestTemplate();
    }

    @After
    public void cleanUp() {
    }

    @Test
    public void createExternalPersonTest() throws URISyntaxException {

        Person person = new Person();
        person.setFirstName("testSooryen");
        person.setLastName("testSooryen");
        person.setEmail("test@sooryen.com");

        Revenue revenue = new Revenue();
        revenue.setNonRecurringPerc(new BigDecimal("88"));
        revenue.setQualifiedPerc(new BigDecimal("43"));
        revenue.setYearlyRevenue(new BigDecimal(10000));

        PersonRegisterRequest personRegisterRequest = new PersonRegisterRequest();
        personRegisterRequest.setPerson(person);
        personRegisterRequest.setRevenue(revenue);

        LinkedMultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>() {{
            add("X-CS-Auth", apiKey);
        }};

        this.base = new URI("http://localhost:" + port + "/api/people/external");

        RequestEntity<PersonRegisterRequest> requestEntity = new RequestEntity<PersonRegisterRequest>(
            personRegisterRequest,
            header,
            HttpMethod.PUT,
            base);

        ResponseEntity<Sessions> responseEntity = template.exchange(requestEntity, Sessions.class);

        //assertThat(responseEntity.getBody().getId(), notNullValue());

    }

}
