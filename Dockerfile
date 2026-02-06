# マルチステージビルド：ビルドステージ
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# 依存関係の解決を先に実行（キャッシュ活用）
COPY pom.xml .
RUN mvn dependency:go-offline -B

# ソースコードをコピーしてビルド
COPY src ./src
RUN mvn clean package -DskipTests -B

# 実行ステージ（ARM64/Apple Silicon対応）
FROM eclipse-temurin:17-jre
WORKDIR /app

# curlをインストール（ヘルスチェック用）
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# ビルドステージからJARファイルをコピー
COPY --from=build /app/target/flea-market-system-complete-0.0.1-SNAPSHOT.jar app.jar

# 非rootユーザーで実行（Debian系コマンド）
RUN groupadd -r spring && useradd -r -g spring spring && \
    chown spring:spring app.jar
USER spring:spring

# ヘルスチェック（curlを使用）
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# ポート公開
EXPOSE 8080

# JVM設定を最適化して起動
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
