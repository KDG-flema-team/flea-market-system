# Flea Market System - Backend API

このリポジトリは、フリマ風システムのバックエンド（REST API）を管理します。
Java / Spring Boot 3.2 を使用し、Auth0 による認証を採用しています。

## 関連プロジェクト

本APIを利用するフロントエンドの実装は、以下のリポジトリにあります。
[flea-market-frontend (React)](https://github.com/KDG-flema-team/flea-market-frontend.git)

## クイックスタートガイド

### 前提条件

- Java: JDK 17+ (Spring Boot 3.2.0)
- Build Tool: Maven 3.9.x
- PostgreSQL 16+

### 動作確認済み環境

***開発チームでは以下の環境で正常な動作を確認しています。***

- OS: macOS(Apple Silicon)
- Java: 17.0.17 (Azul Zulu 17.jdk)
- Maven: Apache Maven 3.9.12

### 実行手順

1. 任意のディレクトリでリポジトリをクローン

```bash
git clone git@github.com:KDG-flema-team/flea-market-system.git
```

2. プロジェクト直下に移動

```bash
cd flea-market-system
```

3. 環境ファイルの準備

application.properties には Stripe や Cloudinary のシークレットが含まれるため、Git 管理の対象外です。雛形ファイルをコピーして使用してください。

```bash
touch src/main/resources/application.properties
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

4. データベースなどの環境を設定

例: PostgreSQL の設定

```bash
# psql -U postgres
# パスワードを入力

# psql プロンプトが表示されたら
CREATE DATABASE fleamarketdb;
CREATE USER your_name WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE fleamarketdb TO your_name;
\q
```

必要に応じて Cloudinary や Stripe のアカウントを作成し、シークレットキー等を `application.properties` に設定してください。

5. アプリ起動

```bash
mvn spring-boot:run
```

## API仕様書の場所

[API仕様書](API_DOCUMENTATION.md)

## フロントエンドと連携するための重要事項

### CORS設定

現在、以下のオリジンを許可しています。フロントエンドの起動ポートが異なる場合は [CORS設定](src/main/java/com/example/fleamarketsystem/config/CorsConfig.java) を修正してください。

- http://localhost:3000

## 認証の流れ

***Auth0を前提とした実装になっています。フロントエンド側で取得した `id_token` (または `access_token`) を、`Authorization: Bearer {token}` として各リクエストに付与してください。***
