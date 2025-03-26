package io.github.busaiku0084.bookmanagement.repository

import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.conf.Settings
import org.jooq.impl.DSL
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

/**
 * jOOQ の DSLContext を構成する設定クラス。
 */
@Configuration
class JooqConfig {

	/**
	 * jOOQ の DSLContext Bean を生成する。
	 *
	 * @param dataSource Spring が管理するデータソース
	 * @return DSLContext（スキーマを含めてSQLを生成する設定）
	 */
	@Bean
	fun dslContext(dataSource: DataSource): DSLContext {
		val settings = Settings()
			.withRenderSchema(true) // スキーマ名をSQLに含める

		return DSL.using(dataSource, SQLDialect.POSTGRES, settings)
	}
}
