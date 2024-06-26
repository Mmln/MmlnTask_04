package com.stepup.MmlnTask_04;

import com.stepup.MmlnTask_04.interfaces.*;
import com.stepup.MmlnTask_04.processing.*;
import com.stepup.MmlnTask_04.repositories.LoginsRepository;
import com.stepup.MmlnTask_04.repositories.UsersRepository;
import io.restassured.RestAssured;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import com.stepup.MmlnTask_04.entities.Logins;
import com.stepup.MmlnTask_04.entities.Users;
import com.stepup.MmlnTask_04.entities.DataFromFiles;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;


@SpringBootTest
//(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MmlnTask04ApplicationTests {

	//	@LocalServerPort
//	private Integer port;
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

	@BeforeAll
	static void beforeAll() {
		postgres.start();
	}

	@AfterAll
	static void afterAll() {
		postgres.stop();
	}

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.name", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
	}

	@BeforeEach
	void setUp() {
//		RestAssured.baseURI = "http://localhost" + port;
		//System.out.println("@BeforeEach Before deleteAll");
		loginsRepository.deleteAll();
		loginsRepository.flush();
		usersRepository.deleteAll();
		usersRepository.flush();
		System.out.println("@BeforeEach After deleteAll");
	}

    @Autowired
    Conveyer conveyer;

	@Autowired
    Handler01FileReader fileReader;
	@Autowired
    Handler02DbWirter dataWriter;


    @Autowired
    Checker01Fio checkFio;
    @Autowired
    Checker02AppType checkerAppType;
    @Autowired
    Checker03Date checkerDate;

	@Autowired
    UsersRepository usersRepository;

	@Autowired
    LoginsRepository loginsRepository;

	@Test
	void testOK() {
		System.out.println("@Test-testOK Test passed\n");
	}

	@Test
	void testHandlers() throws IOException {
		Assertions.assertNotNull(fileReader);
		System.out.println("@Test-testHandlers() Assertions.assertNotNull fileReader");
		Assertions.assertNotNull(checkFio);
		System.out.println("@Test-testHandlers() Assertions.assertNotNull checkFio");
        Assertions.assertNotNull(checkerAppType);
        System.out.println("@Test-testHandlers() Assertions.assertNotNull checkerAppType");
        Assertions.assertNotNull(checkerDate);
        System.out.println("@Test-testHandlers() Assertions.assertNotNull checkerDate");
		Assertions.assertNotNull(dataWriter);
		System.out.println("@Test-testHandlers() Assertions.assertNotNull dataWriter");
		System.out.println("@Test-testHandlers() Test passed\n");
	}

	@Test
	void testDb() throws IOException {

		List<DataFromFiles> dataFromFiles = new ArrayList<>();
		List<Users> users;
		List<Logins> logins;

		Boolean CheckResult = false;
		String fromFiles = "";
		String fromDataBase = "";
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

		System.out.println("@Test-testDb() Filling DB - started...");
        conveyer.produce();
//        dataFromFiles = fileReader.process(dataFromFiles);
//        dataFromFiles = checkFio.process(dataFromFiles);
//        dataFromFiles = checkerAppType.process(dataFromFiles);
//        dataFromFiles = checkerDate.process(dataFromFiles);
//        dataFromFiles = dataWriter.process(dataFromFiles);
		System.out.println("@Test-testDb() Filling DB - finished...");

		System.out.println("@Test-testDb() Get data from files...");
        dataFromFiles = conveyer.getDataFromFiles();

		System.out.println("@Test-testDb() Read data from DB...");
		users = (List<Users>) usersRepository.findAll();
		logins = (List<Logins>) loginsRepository.findAll();

		System.out.println("@Test-testDb() Check data form files with data from DB...");
		for (DataFromFiles dFF : dataFromFiles) {
			fromFiles = "#" + dFF.getUsername() + "#" + dFF.getFio() + "#" + dFF.getAccessDate() + "#" + dFF.getAppType();
			//System.out.println("fromFiles=" + fromFiles);
			CheckResult = false;
			for (Users us : users) {
				for (Logins lg : logins) {
					lg.getAccess_date().toString().replace('T', ' ');
					fromDataBase = "#" + us.getUsername() + "#" + us.getFio() + "#" +
							lg.getAccess_date().format(dateTimeFormatter) + "#" +
							lg.getApplication();
					//System.out.println("fromDataBase=" + fromDataBase);
					CheckResult = fromFiles.equals(fromDataBase);
					if (CheckResult) break;
				}
				if (CheckResult) break;
			}
			assert CheckResult : "Error: fromFiles not equals fromDataBase\n" +
					"___fromFiles=" + fromFiles + "\n" +
					"fromDataBase=" + fromDataBase + "\n";
		}
		System.out.println("@Test-testDb() Test passed\n");
	}

}

