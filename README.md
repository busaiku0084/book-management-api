# book-management-api
書籍管理システムAPI

## 環境構築

### ビルド手順

```bash
./gradlew build
```

- `build` タスクの中で `jooqCodegen`（スキーマから自動生成）とテストが含まれます
- `jooqCodegen` やテストでは、**自動的に Docker コンテナ上の PostgreSQL（テスト用 DB）を起動**します
- Docker の終了処理も Gradle タスクで制御されるため、手動で起動・停止する必要はありません

#### クリーンビルド

```bash
./gradlew clean build
```

- クリーンビルド時にも `jooqCodegen` のコードが必要となるため、自動的に **Docker 起動 → コード生成 → 停止** の流れが実行されます

### Flyway について

- Flyway によるマイグレーションは **アプリケーションの初回起動時に自動実行**されます
- 事前に手動で `flywayMigrate` を実行する必要はありません

## アプリケーション起動

```bash
./gradlew bootRun
```

- `bootRun` 実行時に `compose.yaml` が読み込まれ、**PostgreSQL（開発用 DB）が自動で起動**します

## リンター実行（ktlint）

```bash
./gradlew ktlintCheck
```

- コーディングスタイルチェックを行います
- 自動生成コード（jOOQ）など一部ディレクトリは除外されています

## その他

- テスト時には `compose.override.test.yaml` を使用して専用の Postgres コンテナを起動します（ポートは 55432）
- `build.gradle` 内で Docker の起動・終了はすべて Gradle タスクで制御されています
