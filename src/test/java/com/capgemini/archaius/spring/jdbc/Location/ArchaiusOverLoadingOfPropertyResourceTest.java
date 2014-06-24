/*
 * Copyright (C) 2014 Capgemini (oss@capgemini.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.capgemini.archaius.spring.jdbc.Location;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.apache.camel.CamelContext;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.capgemini.archaius.spring.jdbc.JdbcTestSuper;
import com.capgemini.archaius.spring.jdbc.dataload.DeleteTestDataAndSchemaForArchaiusTest;
import com.capgemini.archaius.spring.jdbc.dataload.ResetTestDataForArchaiusTest;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;

/**
 * 
 * @author skumar81
 */
@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:archaiusJdbc/archaiusPropertiesOverLoadingTest.xml" })
@ActiveProfiles("default")
public class ArchaiusOverLoadingOfPropertyResourceTest extends JdbcTestSuper {

	@Autowired
	@Qualifier("camel")
	protected CamelContext context;
	
	private final String propertyArchaiusKeyOne = "Error400";
	private final String expectedArchaiusPropertyValueOne = "Bad Request";

	private final String propertyArchaiusKeyTwo = "Error404";
	private final String expectedArchaiusPropertyValueTwo = "Page not found";

	private final String propertyArchaiusKeyThree = "Error500";
	private final String expectedArchaiusPropertyValueThree = "Internal Server Error";
	
	private final String propertyArchaiusKeyFour = "Error405";
	private final String expectedArchaiusPropertyValueFour = "Method Not Allowed from Property file";

	private final String propertyArchaiusKeyFive = "Error407";
	private final String expectedArchaiusPropertyValueFive = "Proxy Authentication Required from Property file";

	private final String propertyArchaiusKeySix = "Error413";
	private final String expectedArchaiusPropertyValueSix = "Request Entity Too Large from Property file";

	@Test
	public void propertiesAreLoadedFromDatabaseAndAccessedViaArchaiusDynamicStringProperty() throws InterruptedException {

		// value of Error is loaded from Database and overriding the value read from properties file.
		DynamicStringProperty prop1 = DynamicPropertyFactory.getInstance().getStringProperty(propertyArchaiusKeyOne, propertyArchaiusKeyOne);

		assertThat(prop1.get(), is(equalTo(expectedArchaiusPropertyValueOne)));
		
		DynamicStringProperty prop3 = DynamicPropertyFactory.getInstance().getStringProperty(propertyArchaiusKeyThree, propertyArchaiusKeyThree);

		assertThat(prop3.get(), is(equalTo(expectedArchaiusPropertyValueThree)));
		
		DynamicStringProperty prop2 = DynamicPropertyFactory.getInstance().getStringProperty(propertyArchaiusKeyTwo, propertyArchaiusKeyTwo);

		assertThat(prop2.get(), is(equalTo(expectedArchaiusPropertyValueTwo)));
		
		// value of Errorcode is loaded from properties file as the same is not available in DB.
		DynamicStringProperty prop4 = DynamicPropertyFactory.getInstance().getStringProperty(propertyArchaiusKeyFour, propertyArchaiusKeyFour);

		assertThat(prop4.get(), is(equalTo(expectedArchaiusPropertyValueFour)));
		
		DynamicStringProperty prop5 = DynamicPropertyFactory.getInstance().getStringProperty(propertyArchaiusKeyFive, propertyArchaiusKeyFive);

		assertThat(prop5.get(), is(equalTo(expectedArchaiusPropertyValueFive)));
		
		DynamicStringProperty prop6 = DynamicPropertyFactory.getInstance().getStringProperty(propertyArchaiusKeySix, propertyArchaiusKeySix);

		assertThat(prop6.get(), is(equalTo(expectedArchaiusPropertyValueSix)));
		
		//resetting the data to initial value
		ResetTestDataForArchaiusTest resetData=new ResetTestDataForArchaiusTest();
		resetData.initializedDerby();
		Thread.sleep(100);
		
		//shutting down the in memory database.
		DeleteTestDataAndSchemaForArchaiusTest deleteDB= new DeleteTestDataAndSchemaForArchaiusTest();
		deleteDB.deleteDatabase();

	}

}
