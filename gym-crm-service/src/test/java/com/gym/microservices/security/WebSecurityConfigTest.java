package com.gym.microservices.security;

import com.gym.microservices.service.impl.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class WebSecurityConfigTest {

    private static final String ROOT_URL = "http://localhost:8084";

    @Mock
    private CustomUserDetailsService userService;

    @Mock
    private JwtFilter jwtFilter;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private WebSecurityConfig webSecurityConfig;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test
    void checkIfAuthenticationManagerUseCustomUserDetailsServiceAndPasswordEncoder() throws Exception {
        HttpSecurity httpSecurity = mock(HttpSecurity.class);

        ObjectPostProcessor<Object> objectPostProcessor = new ObjectPostProcessor<>() {
            @Override
            public <O> O postProcess(O object) {
                return object;
            }
        };

        AuthenticationManagerBuilder authManagerBuilder = new AuthenticationManagerBuilder(objectPostProcessor);

        authManagerBuilder.userDetailsService(userService).passwordEncoder(webSecurityConfig.passwordEncoder());
        AuthenticationManager authenticationManager = authManagerBuilder.build();

        assertNotNull(authenticationManager);
        assertNotNull(userService);
        assertNotNull(passwordEncoder);
    }

    @Test
    void checkIfPasswordEncoderReturnCustomPasswordEncoder() {
        PasswordEncoder encoder = webSecurityConfig.passwordEncoder();

        assertNotNull(encoder);
        assertTrue(encoder instanceof CustomPasswordEncoder);
    }

    @Test
    void checkIfGetPermitAllUrlsIsWorked() throws Exception {
        Method method = WebSecurityConfig.class.getDeclaredMethod("getPermitAllUrls");
        method.setAccessible(true);

        String[] permitAllUrls = (String[]) method.invoke(webSecurityConfig);

        assertNotNull(permitAllUrls);
        assertEquals(7, permitAllUrls.length);
    }

    @Test
    public void checkIfCorsConfigurationIsWorked() throws Exception {
        mockMvc.perform(options("/trainee")
                        .header("Origin", ROOT_URL)
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Access-Control-Allow-Origin", ROOT_URL))
                .andExpect(header().string("Access-Control-Allow-Methods", "GET,POST,PUT,PATCH,DELETE"));
    }
}