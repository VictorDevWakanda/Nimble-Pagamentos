# ATENÇÃO!
# Este arquivo serve para gerar o container da aplicação Java.
# Ele realiza o build do projeto e prepara o ambiente para execução.
# Caso queira testar automações, o arquivo já está pronto para uso em pipelines de CI/CD.

# syntax=docker/dockerfile:1             # habilita recursos modernos

###########################
# 1) STAGE : BUILD
###########################
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copia apenas o que raramente muda p/ aproveitar cache
COPY pom.xml .
RUN mvn -B dependency:go-offline          # baixa dependências

# Copia o restante do código e gera o artefato
COPY src ./src
RUN mvn -B clean package                  # gera target/*.jar


###########################
# 2) STAGE : RUNTIME
###########################
FROM eclipse-temurin:17-jre-jammy AS runtime
ENV APP_HOME=/app
WORKDIR $APP_HOME

# Copia o jar gerado na stage anterior
COPY --from=build /workspace/target/*.jar app.jar

# cria usuário não-root
RUN useradd --system --uid 10001 appuser
USER appuser

HEALTHCHECK --start-period=60s --interval=30s --timeout=5s --retries=5 \
  CMD curl -fs $HEALTH_URL || exit 1

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "exec java ${JAVA_OPTS} -jar ${APP_HOME}/app.jar --spring.profiles.active=${APP_PROFILE:-default}"]