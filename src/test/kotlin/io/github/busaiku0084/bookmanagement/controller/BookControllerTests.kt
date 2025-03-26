package io.github.busaiku0084.bookmanagement.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.github.busaiku0084.bookmanagement.dto.AuthorResponse
import io.github.busaiku0084.bookmanagement.dto.BookRequest
import io.github.busaiku0084.bookmanagement.dto.BookResponse
import io.github.busaiku0084.bookmanagement.exception.GlobalExceptionHandler
import io.github.busaiku0084.bookmanagement.service.BookService
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasItems
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDate

@DisplayName("BookControllerのテスト")
class BookControllerTests {

	private val bookService: BookService = org.mockito.kotlin.mock()
	private lateinit var mockMvc: MockMvc
	private val objectMapper = ObjectMapper()

	@BeforeEach
	fun setup() {
		objectMapper.registerModule(JavaTimeModule())
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

		mockMvc = MockMvcBuilders
			.standaloneSetup(BookController(bookService))
			.setControllerAdvice(GlobalExceptionHandler())
			.build()
	}

	@Nested
	inner class CreateBook {

		@Test
		fun `POST books - 正常系`() {
			val request = BookRequest("ノルウェイの森", 1980, "published", listOf(1))
			val response = BookResponse(
				1,
				request.title,
				request.price,
				request.status,
				listOf(
					AuthorResponse(1, "村上春樹", LocalDate.of(1949, 1, 12))
				)
			)

			whenever(bookService.createBook(request)).thenReturn(response)

			mockMvc.post("/books") {
				contentType = MediaType.APPLICATION_JSON
				content = objectMapper.writeValueAsString(request)
			}.andExpect {
				status { isCreated() }
				jsonPath("$.id", equalTo(1))
				jsonPath("$.title", equalTo("ノルウェイの森"))
				jsonPath("$.price", equalTo(1980))
				jsonPath("$.status", equalTo("published"))
				jsonPath("$.authors[0].id", equalTo(1))
				jsonPath("$.authors[0].name", equalTo("村上春樹"))
				jsonPath("$.authors[0].birthDate", equalTo("1949-01-12"))
			}
		}

		@Test
		fun `POST books - バリデーションエラー`() {
			val request = BookRequest("", -1, "invalid", emptyList())

			mockMvc.post("/books") {
				contentType = MediaType.APPLICATION_JSON
				content = objectMapper.writeValueAsString(request)
			}.andExpect {
				status { isBadRequest() }
				jsonPath("$.message", equalTo("入力値が不正です"))
				jsonPath("$.errors[*].field", hasItems("title", "price", "status", "authorIds"))
			}
		}
	}

	@Nested
	inner class GetBookById {
		@Test
		fun `GET books_id - 正常系`() {
			val response = BookResponse(
				id = 1,
				title = "海辺のカフカ",
				price = 1800,
				status = "published",
				authors = listOf(
					AuthorResponse(1, "村上春樹", LocalDate.of(1949, 1, 12))
				)
			)

			whenever(bookService.getBook(1)).thenReturn(response)

			mockMvc.get("/books/1")
				.andExpect {
					status { isOk() }
					jsonPath("$.id", equalTo(1))
					jsonPath("$.title", equalTo("海辺のカフカ"))
					jsonPath("$.price", equalTo(1800))
					jsonPath("$.status", equalTo("published"))
					jsonPath("$.authors[0].id", equalTo(1))
					jsonPath("$.authors[0].name", equalTo("村上春樹"))
					jsonPath("$.authors[0].birthDate", equalTo("1949-01-12"))
				}
		}

		@Test
		fun `GET books_id - 異常系 - 存在しないID`() {
			whenever(bookService.getBook(999)).thenThrow(IllegalArgumentException("指定された書籍が見つかりません"))

			mockMvc.get("/books/999")
				.andExpect {
					status { isNotFound() }
					jsonPath("$.message", equalTo("指定された書籍が見つかりません"))
				}
		}
	}

	@Nested
	inner class GetBooksByAuthorFilter {
		@Test
		fun `GET books?author=1 - 正常系`() {
			val response = listOf(
				BookResponse(
					id = 1,
					title = "海辺のカフカ",
					price = 1800,
					status = "published",
					authors = listOf(AuthorResponse(1, "村上春樹", LocalDate.of(1949, 1, 12)))
				)
			)
			whenever(bookService.getBooksByAuthorIds(listOf(1))).thenReturn(response)

			mockMvc.get("/books?author=1")
				.andExpect {
					status { isOk() }
					jsonPath("$[0].id", equalTo(1))
					jsonPath("$[0].title", equalTo("海辺のカフカ"))
					jsonPath("$[0].price", equalTo(1800))
					jsonPath("$[0].status", equalTo("published"))
					jsonPath("$[0].authors[0].id", equalTo(1))
					jsonPath("$[0].authors[0].name", equalTo("村上春樹"))
					jsonPath("$[0].authors[0].birthDate", equalTo("1949-01-12"))
				}
		}
	}

	@Nested
	inner class UpdateBook {
		@Test
		fun `PUT books_id - 正常系`() {
			val request = BookRequest(
				title = "海辺のカフカ(新)",
				price = 2000,
				status = "published",
				authorIds = listOf(1)
			)
			val updated = BookResponse(
				id = 1,
				title = "海辺のカフカ(新)",
				price = 2000,
				status = "published",
				authors = listOf(AuthorResponse(1, "村上春樹", LocalDate.of(1949, 1, 12)))
			)
			whenever(bookService.updateBook(1, request)).thenReturn(updated)

			mockMvc.put("/books/1") {
				contentType = MediaType.APPLICATION_JSON
				content = objectMapper.writeValueAsString(request)
			}.andExpect {
				status { isOk() }
				jsonPath("$.id", equalTo(1))
				jsonPath("$.title", equalTo("海辺のカフカ(新)"))
				jsonPath("$.price", equalTo(2000))
				jsonPath("$.status", equalTo("published"))
				jsonPath("$.authors[0].id", equalTo(1))
				jsonPath("$.authors[0].name", equalTo("村上春樹"))
				jsonPath("$.authors[0].birthDate", equalTo("1949-01-12"))
			}
		}

		@Test
		fun `PUT books_id - 異常系 - 書籍が存在しない`() {
			val request = BookRequest(
				title = "海辺のカフカ",
				price = 1000,
				status = "published",
				authorIds = listOf(999)
			)
			whenever(
				bookService.updateBook(
					999,
					request
				)
			).thenThrow(IllegalArgumentException("ID=999 の書籍が見つかりません"))

			mockMvc.put("/books/999") {
				contentType = MediaType.APPLICATION_JSON
				content = objectMapper.writeValueAsString(request)
			}.andExpect {
				status { isNotFound() }
				jsonPath("$.message", equalTo("ID=999 の書籍が見つかりません"))
			}
		}
	}
}
