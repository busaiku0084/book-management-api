package io.github.busaiku0084.bookmanagement.repository

import io.github.busaiku0084.bookmanagement.jooq.tables.Authors.AUTHORS
import io.github.busaiku0084.bookmanagement.model.Author
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

/**
 * 著者情報を管理するリポジトリクラス
 *
 * @property dsl jOOQ DSLContext を使用した DB 操作
 */
@Repository
class AuthorRepository(private val dsl: DSLContext) {

	/**
	 * 著者を新規登録する
	 *
	 * @param author 登録対象の著者モデル
	 * @return 登録された著者モデル（ID含む）
	 */
	fun create(author: Author): Author {
		val record = dsl.insertInto(AUTHORS)
			.columns(AUTHORS.NAME, AUTHORS.BIRTH_DATE)
			.values(author.name, author.birthDate)
			.returning(AUTHORS.ID, AUTHORS.NAME, AUTHORS.BIRTH_DATE)
			.fetchOne() ?: throw RuntimeException("著者の登録に失敗しました")

		return Author(
			id = record.get(AUTHORS.ID),
			name = record.get(AUTHORS.NAME),
			birthDate = record.get(AUTHORS.BIRTH_DATE)
		)
	}

	/**
	 * すべての著者を取得する
	 *
	 * @return 著者モデルのリスト
	 */
	fun findAll(): List<Author> {
		return dsl.selectFrom(AUTHORS)
			.fetchInto(Author::class.java)
	}

	/**
	 * ID によって著者を取得する
	 *
	 * @param id 著者ID
	 * @return 該当する著者、存在しない場合は null
	 */
	fun findById(id: Int): Author? {
		return dsl.selectFrom(AUTHORS)
			.where(AUTHORS.ID.eq(id))
			.fetchOneInto(Author::class.java)
	}

	/**
	 * 著者情報を更新する
	 *
	 * @param author 更新対象の著者情報
	 */
	fun update(author: Author) {
		dsl.update(AUTHORS)
			.set(AUTHORS.NAME, author.name)
			.set(AUTHORS.BIRTH_DATE, author.birthDate)
			.where(AUTHORS.ID.eq(author.id))
			.execute()
	}
}
