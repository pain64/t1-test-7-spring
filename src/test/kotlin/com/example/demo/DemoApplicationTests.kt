package com.example.demo

import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

// Annotations for Spring web controllers
// https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-requestmapping.html

@SpringBootTest(classes = [DemoApplication::class, DemoApplicationTests.TestConfiguration::class])
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DemoApplicationTests: FunSpec() {
    @Autowired
    private lateinit var mockMvc: MockMvc

    class TestConfiguration {
        class TestCatsService : CatsService {
            override fun randomCat(tag: String?): CatData? = TODO()
        }

        @Bean @Profile("test") fun catsService() = TestCatsService()
    }

    init {
        extension(SpringExtension)

        test("random cat no tag") {
            mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/randomCat")
            )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                    MockMvcResultMatchers.content().json(
                        """
                    {"id":"abcdef","mimeType":"image/jpeg","tags":["white"],"comments":[]}
                """
                    )
                )
        }
    }
}
