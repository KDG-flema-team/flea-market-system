# Flea Market System - Backend API

このリポジトリは、フリマ風システムのバックエンド（REST API）を管理しています。
Java/Spring Boot 3.2を使用し、Auth0による堅牢な認証を特徴としています。


## 関連プロジェクト
本APIを利用するフロントエンドの実装は、以下のリポジトリにあります。
[flea-market-frontend (React)](https://github.com/KDG-flema-team/flea-market-frontend.git)


## クイックスタートガイド

### 前提条件

- Java: JDK 17+ (Spring Boot 3.2.0)
- Build Tool: Maven 3.9.x
- PostgresSQL 16+

### 動作確認済み環境

***開発チームでは以下の環境で正常な動作を確認しています。***

- OS: macOS(Apple Silicon)
- Java: 17.0.17 (Azul Zulu 17.jdk)
- Maven: Apache Maven 3.9.12


### 実行手順

1) 任意のディレクトリでリポジトリをクローン
```bash
    git clone git@github.com:KDG-flema-team/flea-market-system.git
```
2) プロジェクト直下に移動
```bash
    cd fleama-market-system
```
3) 環境変数の設定

***application.propertiesにはStripe、Cloudinaryのシークレットキーが含まれているためGit管理の対象外となっています。そのため雛型のapplication.properties.exampleを使用しています。***

```bash
    touch src/main/resources/application.properties
```
4) application.properties.exampleをコピー＆ペースト
```bash
    cp src/main/resources/application.properties.example src/main/resources/application.properties
```
5) 環境変数を設定
```bash
    # PostgreSQLの例
    psql -U postgres
    # パスワードの入力を求められます

    # postgres=# が表示されたら
    CREATE DATABASE fleamarketdb;

    # ユーザーを作成(your_nameとyour_passwordを自身で設定)
    CREATE USER your-name WITH PASSWORD 'your_password';

    # 作成したDBに対する全権限を作成したユーザーに与えます
    GRANT ALL PRIVILEGES ON DATABASE fleamarketdb TO your_name;

    # psqlを終了
    \q


    # その他Cloudinary, Stripeもアカウントを作成しそれぞれシークレットキーなどを設定
    
```
6) 起動
```bash
    mvn spring-boot:run
```


## API仕様書の場所

[API仕様書](API_DOCUMENTATION.md)


## フロントエンドと連携するための重要事項

### CORS設定
***現在、以下のオリジンを許可しています。フロントエンドの起動ポートが異なる場合は [CORS設定](src/main/java/com/example/fleamarketsystem/config/CorsConfig.java) を修正してください。***
- 1 `http://localhost:3000`

## 認証の流れ
***Auth0を前提とした実装になっています。フロントエンド側で取得した `id_token` (または `access_token`) を、`Authorization: Bearer {token}` として各リクエストに付与してください。***
