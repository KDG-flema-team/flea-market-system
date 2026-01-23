# JWT認証の実装

## 概要

このアプリケーションは、Auth0のJWKSエンドポイントを使用したJWT認証を実装しています。

## 設定情報

### JWKS設定
- **JWKS URI**: `https://dev-3hpu1z5igskz6tna.us.auth0.com/.well-known/jwks.json`
- **Issuer**: `https://dev-3hpu1z5igskz6tna.us.auth0.com/`
- **Audience**: `flea-market-system`

## APIリクエストの認証方法

### 1. JWTトークンの取得

Auth0からJWTトークンを取得してください。

### 2. APIリクエストにトークンを含める

取得したJWTトークンを`Authorization`ヘッダーに含めてAPIリクエストを送信します:

```bash
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  http://localhost:8080/api/items
```

### curlでのテスト例

```bash
# 公開エンドポイント（認証不要）
curl http://localhost:8080/api/auth/login

# 保護されたエンドポイント（JWT必須）
curl -H "Authorization: Bearer eyJhbGc..." \
  http://localhost:8080/api/items

# 管理者専用エンドポイント（ADMIN権限必須）
curl -H "Authorization: Bearer eyJhbGc..." \
  http://localhost:8080/api/admin/users
```

## エンドポイントの保護レベル

### 公開エンドポイント（認証不要）
- `/api/auth/**` - 認証関連

### 認証必須エンドポイント
- `/api/**` - すべてのAPIエンドポイント（authを除く）

### 管理者権限必須
- `/api/admin/**` - 管理者専用エンドポイント

## JWT検証の流れ

1. クライアントがAuthorizationヘッダーにBearerトークンを含めてリクエスト
2. Spring SecurityのBearerTokenAuthenticationFilterがトークンを抽出
3. JwtDecoderがJWKSエンドポイントから公開鍵を取得
4. トークンの署名を検証
5. Issuer、Audience、有効期限を検証
6. 検証成功後、SecurityContextに認証情報を設定
7. コントローラーでリクエストを処理

## トラブルシューティング

### 401 Unauthorized エラー
- JWTトークンが含まれているか確認
- トークンの形式が`Bearer <token>`になっているか確認
- トークンの有効期限が切れていないか確認
- トークンのAudienceが`flea-market-system`になっているか確認

### 403 Forbidden エラー
- 必要な権限（ROLE_ADMIN等）があるか確認
- トークンの`permissions`クレームに必要な権限が含まれているか確認

## 実装ファイル

- `SecurityConfig.java` - セキュリティ設定とJWT検証の設定
- `AudienceValidator.java` - Audienceクレームの検証
- `application.yml` - JWT設定（Issuer URI、JWKS URI、Audience）
- `pom.xml` - OAuth2 Resource Serverの依存関係

## 開発環境での起動

```bash
mvn clean compile
mvn spring-boot:run
```

サーバーは http://localhost:8080 で起動します。
