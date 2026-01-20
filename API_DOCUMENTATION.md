# Flea Market System - API Documentation

このドキュメントでは、新しく実装されたREST API機能について説明します。

## 認証について

このAPIは、Auth0を使用したJWT (JSON Web Token) 認証を実装しています。

### JWT認証の設定

- **JWKS URI**: `https://dev-3hpu1z5igskz6tna.us.auth0.com/.well-known/jwks.json`
- **Issuer**: `https://dev-3hpu1z5igskz6tna.us.auth0.com/`
- **Audience**: `flea-market-system`

### APIリクエストの認証

保護されたAPIエンドポイントにアクセスするには、HTTPリクエストのAuthorizationヘッダーにBearerトークンを含める必要があります:

```
GET /api/items
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjEyMyJ9...
```

### JWT検証

バックエンドは以下の項目を検証します:
- トークンの署名 (JWKSを使用)
- Issuer (発行者)
- Audience (対象者)
- 有効期限

### 公開エンドポイント

以下のエンドポイントは認証なしでアクセス可能です:
- `/api/auth/**` - 認証関連のエンドポイント

### 保護されたエンドポイント

- `/api/admin/**` - ADMIN ロールが必要
- その他の `/api/**` - 有効なJWTトークンが必要

## 実装された機能

### 1. ユーザー管理
- **User エンティティの拡張**
  - `role`: ユーザーのロール (USER/ADMIN)
  - `rank`: ユーザーランク (bronze/silver/gold/platinum)
  - `banned`: BAN状態のフラグ
  - `bannedAt`: BAN日時
  - `bannedByAdminId`: BANを実行した管理者のID

### 2. 認証機能 (Authentication)

#### エンドポイント

##### ユーザー登録
```
POST /api/auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}
```

##### ログイン
```
POST /api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}

Response:
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "userId": 1,
  "email": "john@example.com",
  "name": "John Doe",
  "role": "USER",
  "rank": "bronze"
}
```

##### ログアウト
```
POST /api/auth/logout
```

#### SNS認証 (OAuth2)
- Google認証: `/oauth2/authorization/google`
- GitHub認証: `/oauth2/authorization/github`

環境変数で以下を設定：
```
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
GITHUB_CLIENT_ID=your-github-client-id
GITHUB_CLIENT_SECRET=your-github-client-secret
```

### 3. Star（評価）機能

#### エンドポイント

##### 評価を作成
```
POST /api/stars
Authorization: Bearer {token}
Content-Type: application/json

{
  "targetUserId": 2,
  "rating": 5,
  "comment": "Great seller!"
}
```

##### ユーザーの評価一覧を取得
```
GET /api/stars/user/{userId}
```

##### ユーザーの評価統計を取得
```
GET /api/stars/user/{userId}/stats

Response:
{
  "averageRating": 4.5,
  "totalStars": 10
}
```

##### 評価を更新
```
PUT /api/stars/{starId}
Authorization: Bearer {token}
Content-Type: application/json

{
  "rating": 4,
  "comment": "Updated comment"
}
```

##### 評価を削除
```
DELETE /api/stars/{starId}
Authorization: Bearer {token}
```

### 4. Report（通報）機能

#### エンドポイント

##### 通報を作成
```
POST /api/reports
Authorization: Bearer {token}
Content-Type: application/json

{
  "reportedUserId": 3,
  "reason": "Spam or inappropriate behavior"
}
```

##### すべての通報を取得（管理者のみ）
```
GET /api/reports
Authorization: Bearer {admin-token}
```

##### ステータス別に通報を取得（管理者のみ）
```
GET /api/reports/status/{status}
Authorization: Bearer {admin-token}

status: PENDING, REVIEWED, RESOLVED
```

##### ユーザーに関する通報を取得（管理者のみ）
```
GET /api/reports/user/{userId}
Authorization: Bearer {admin-token}
```

##### 通報をレビュー（管理者のみ）
```
PUT /api/reports/{reportId}/review
Authorization: Bearer {admin-token}
Content-Type: application/json

{
  "status": "REVIEWED"
}
```

##### ペンディング中の通報数を取得（管理者のみ）
```
GET /api/reports/user/{userId}/pending-count
Authorization: Bearer {admin-token}
```

### 5. 管理者によるユーザーBAN機能

#### エンドポイント

##### ユーザーをBAN（管理者のみ）
```
POST /api/admin/users/{userId}/ban
Authorization: Bearer {admin-token}
Content-Type: application/json

{
  "reason": "Violation of terms of service"
}
```

##### ユーザーのBAN解除（管理者のみ）
```
POST /api/admin/users/{userId}/unban
Authorization: Bearer {admin-token}
```

##### ユーザーランクを更新（管理者のみ）
```
PUT /api/admin/users/{userId}/rank
Authorization: Bearer {admin-token}
Content-Type: application/json

{
  "rank": "gold"
}

rank: bronze, silver, gold, platinum
```

## データベーススキーマ

### Star テーブル
```sql
CREATE TABLE star (
  id SERIAL PRIMARY KEY,
  user_id INT NOT NULL,
  target_user_id INT NOT NULL,
  rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
  comment TEXT,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (target_user_id) REFERENCES users(id),
  UNIQUE (user_id, target_user_id)
);
```

### Report テーブル
```sql
CREATE TABLE report (
  id SERIAL PRIMARY KEY,
  reporter_id INT NOT NULL,
  reported_user_id INT NOT NULL,
  reason TEXT NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  reviewed_at TIMESTAMP,
  reviewed_by_admin_id INT,
  FOREIGN KEY (reporter_id) REFERENCES users(id),
  FOREIGN KEY (reported_user_id) REFERENCES users(id),
  FOREIGN KEY (reviewed_by_admin_id) REFERENCES users(id)
);
```

## セキュリティ

### JWT認証
- すべてのAPIエンドポイント（`/api/auth/**`を除く）は、JWTトークンによる認証が必要です
- Authorizationヘッダーに`Bearer {token}`形式でトークンを含める必要があります
- トークンの有効期限は24時間です

### 権限管理
- `USER`: 通常のユーザー権限
- `ADMIN`: 管理者権限（ユーザーBAN、通報レビューなど）

## CORS設定
- API（`/api/**`）は、以下のオリジンからのリクエストを許可しています：
  - `http://localhost:3000`
  - `http://localhost:8080`

## 使用技術
- Spring Boot 3.2.0
- Spring Security 6
- JWT (jjwt 0.11.5)
- OAuth2 Client
- PostgreSQL
- JPA/Hibernate
- Lombok

## セットアップ

1. 依存関係のインストール
```bash
mvn clean install
```

2. データベースの設定
- PostgreSQLデータベースを作成
- `application.properties`でデータベース接続情報を設定

3. アプリケーションの起動
```bash
mvn spring-boot:run
```

4. APIテスト
- Postman、curl、またはお好みのHTTPクライアントを使用してAPIをテストできます

## 環境変数
```
# JWT設定
JWT_SECRET=your-jwt-secret-key
JWT_EXPIRATION=86400000

# OAuth2設定
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
GITHUB_CLIENT_ID=your-github-client-id
GITHUB_CLIENT_SECRET=your-github-client-secret
```

## 注意事項
- 本番環境では、JWTシークレットキーを環境変数で管理してください
- OAuth2のクライアントIDとシークレットも同様に環境変数で管理してください
- CORS設定は、本番環境に合わせて適切に設定してください
- BANされたユーザーは認証を通過できません
