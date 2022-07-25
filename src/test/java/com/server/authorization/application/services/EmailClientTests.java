package com.server.authorization.application.services;

import com.server.authorization.application.service.implementation.EmailClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EmailClientTests {

    @Test
    void getHtmlEmailTemplate_withValidTemplateNameAndVariables_returnsCorrectHtmlString() throws IOException {
        String testTemplatePath = "templates/test/test-template.html";
        String testText="test-replacement-text";
        String testLink="test-replacement-link";
        HashMap<String,String> testVariables = new HashMap<>(){{
            put("~TEST_TEXT~",testText);
            put("~TEST_LINK~",testLink);
        }};

        String actualHtml = EmailClient.getHtmlEmailTemplate(testTemplatePath,testVariables);

        assertEquals(2,StringUtils.countOccurrencesOf(actualHtml,"test-replacement-text"));
        assertEquals(1,StringUtils.countOccurrencesOf(actualHtml,"test-replacement-link"));
    }

    @Test
    void getHtmlEmailTemplate_withNullTemplateNameAndVariables_throwsException() {
        InvalidParameterException exception = assertThrows(InvalidParameterException.class,()->
                        EmailClient.getHtmlEmailTemplate(null,null));

        assertEquals("Template name is required.", exception.getMessage());
    }
}
