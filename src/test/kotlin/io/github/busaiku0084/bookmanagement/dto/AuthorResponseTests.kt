package io.github.busaiku0084.bookmanagement.dto

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDate

@DisplayName("AuthorResponseのテスト")
class AuthorResponseTests {

	@Test
	fun `birthDate が yyyy-MM-dd 形式でシリアライズされる`() {
		val response = AuthorResponse(
			id = 1,
			name = "村上春樹",
			birthDate = LocalDate.of(1949, 1, 12)
		)

		val mapper = ObjectMapper().apply {
			registerModule(JavaTimeModule())
			disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
		}

		val json = mapper.writeValueAsString(response)
		assert(json.contains("\"birthDate\":\"1949-01-12\"")) {
			"birthDate should be serialized as yyyy-MM-dd, but was: $json"
		}
	}
}
